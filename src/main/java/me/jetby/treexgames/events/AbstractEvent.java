package me.jetby.treexgames.events;

import me.jetby.treexgames.Main;
import me.jetby.treexgames.utils.Actions;
import me.jetby.treexgames.utils.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.jetby.treexgames.configurations.Config.CFG;
import static me.jetby.treexgames.configurations.EventsConfig.E_CFG;
import static me.jetby.treexgames.managers.API.*;
import static me.jetby.treexgames.utils.FormatTime.stringFormat;
import static me.jetby.treexgames.utils.Parser.hex;

public abstract class AbstractEvent {
    protected static String eventName;
    protected static final Map<UUID, Integer> progressMap = new HashMap<>();
    protected final Pattern pattern = Pattern.compile("%treexgames_top_(\\d+)(?:_(progress|name))?%");


    public static String getEventName() {
        return eventName;
    }

    public void startTimer(String configPath) {
        setTimer(E_CFG(configPath).getInt("duration", 3600));

        Actions actions = new Actions();
        List<String> onStart = E_CFG(configPath).getStringList("actions.on_start");
        List<String> onEndPrizes = List.of();

        if (getPrizeSection()!=null) {
            onEndPrizes = E_CFG(configPath).getStringList("actions.on_end_prizes." + getPrizeSection());
        }
        List<String> onEnd = E_CFG(configPath).getStringList("actions.on_end");


        List<String> finalOnEndPrizes = onEndPrizes;
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> executeActions(actions, onStart));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getTimer() == 0) {
                    cancel();

                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        if (getPrizeSection() != null) {
                            executeActions(actions, finalOnEndPrizes);
                        }
                        executeActions(actions, onEnd);

                    });
                    setTimer(0);
                    setNowEvent(null);
                    Hologram.updateHologram(replacement(CFG().getStringList("holo"), getTimer(), configPath));
                } else {
                    List<String> holoLines = E_CFG(configPath).getStringList("holo-start");
                    Hologram.updateHologram(replacement(holoLines, getTimer(), configPath));

                    setTimer(getTimer()-1);
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L);
    }

    protected void executeActions(Actions actions, List<String> actionList) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!shouldGiveReward(player.getUniqueId())) continue;

            actions.execute(player, replacement(actionList, 0, getConfigPath()));
            markRewardGiven(player.getUniqueId());
        }
    }

    private boolean shouldGiveReward(UUID uuid) {
        if (!CFG().getBoolean("redis.enable")) return true;

        String rewardKey = "treexgames:event:" + eventName + ":rewarded:" + uuid;
        String given = Main.getInstance().getRedis().get(rewardKey);

        // если уже выдано – не выдавать повторно
        if ("1".equals(given)) return false;

        Main.getInstance().getRedis().set(rewardKey, "1");
        return true;
    }
    private void markRewardGiven(UUID uuid) {
        if (!CFG().getBoolean("redis.enable")) return;

        String rewardKey = "treexgames:event:" + eventName + ":rewarded:" + uuid;
        try (var jedis = Main.getInstance().getRedis().getPool().getResource()) {
            jedis.setex(rewardKey, 86400 * 2, "1"); // 2 дня
        }
    }

    protected List<String> replacement(List<String> lines, int time, String configPath) {
        List<String> updatedLines = new ArrayList<>();
        String formattedTime = String.valueOf(time);
        FileConfiguration config = E_CFG(configPath);

        String format = CFG().getString("placeholders.player", "%player% - %progress%");
        String noFormat = CFG().getString("placeholders.notFound", "не найден");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                int place = Integer.parseInt(matcher.group(1));
                String type = matcher.group(2);

                String name = getTopName(place);
                int progress = getTopProgress(place);

                String replacement;
                if ("progress".equalsIgnoreCase(type)) {
                    replacement = String.valueOf(progress);
                } else if ("name".equalsIgnoreCase(type)) {
                    replacement = name;
                } else {
                    replacement = (progress == 0 || name.equals("???"))
                            ? noFormat
                            : format.replace("%player%", name).replace("%progress%", String.valueOf(progress));
                }

                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }

            matcher.appendTail(sb);
            updatedLines.add(sb.toString()
                    .replace("%treexgames_time_to_end_string%", stringFormat(time))
                    .replace("%treexgames_time_to_end%", formattedTime)
                    .replace("%treexgames_nowEvent_prefix%", hex(config.getString("prefix")))
                    .replace(getProgressPlaceholder() != null ? getProgressPlaceholder() : "",
                            getProgressPlaceholder() != null ? eventName : ""));
        }
        return updatedLines;
    }

    public abstract String getConfigPath();
    public abstract String getPrizeSection();
    public abstract String getProgressPlaceholder();
    public abstract void startEvent();
    public void addProgress(Player player, int amount) {
        if (CFG().getBoolean("redis.enable")) {
            String key = "treexgames:event:" + eventName + ":progress:" + player.getUniqueId();
            int newProgress = getPlayerProgress(player) + amount;
            Main.getInstance().getRedis().set(key, String.valueOf(newProgress));
        } else {
            progressMap.put(player.getUniqueId(), getPlayerProgress(player) + amount);
        }
    }

    public static void resetProgress() {
        progressMap.clear();

        if (CFG().getBoolean("redis.enable")) {
            try (var jedis = Main.getInstance().getRedis().getPool().getResource()) {

                String[] patterns = {
                        "treexgames:event:" + eventName + ":progress:*",
                        "treexgames:event:" + eventName + ":rewarded:*"
                };

                for (String pattern : patterns) {
                    ScanParams scanParams = new ScanParams().match(pattern).count(100);
                    String cursor = ScanParams.SCAN_POINTER_START;

                    do {
                        ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                        cursor = scanResult.getCursor();
                        for (String key : scanResult.getResult()) {
                            jedis.del(key);
                        }
                    } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
                }

            }
        }
    }




    public static UUID getTopUUID(int number) {
        if (progressMap.isEmpty()) return null;
        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(progressMap.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return number <= 0 || number > sorted.size() ? null : sorted.get(number - 1).getKey();
    }

    public static String getTopName(int number) {
        UUID uuid = getTopUUID(number);
        return uuid == null ? "???" : Bukkit.getOfflinePlayer(uuid).getName();
    }

    public static int getTopProgress(int number) {
        UUID uuid = getTopUUID(number);
        return uuid == null ? 0 : progressMap.getOrDefault(uuid, 0);
    }

    public static int getPlayerProgress(Player player) {
        if (CFG().getBoolean("redis.enable")) {
            String key = "treexgames:event:" + eventName + ":progress:" + player.getUniqueId();
            String value = Main.getInstance().getRedis().get(key);
            return value == null ? 0 : Integer.parseInt(value);
        } else {
            return player == null ? 0 : progressMap.getOrDefault(player.getUniqueId(), 0);
        }
    }
}