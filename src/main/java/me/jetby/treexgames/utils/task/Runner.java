package me.jetby.treexgames.utils.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Runner {

    void runPlayer(@NotNull Runnable task, @NotNull Player player);

    void run(@NotNull Runnable task);

    void runAsync(@NotNull Runnable task);

    void runLater(@NotNull Runnable task, long delayTicks);

    void runLaterAsync(@NotNull Runnable task, long delayTicks);

    void startTimer(@NotNull Runnable task, long delayTicks, long periodTicks);

    void startTimerAsync(@NotNull Runnable task, long delayTicks, long periodTicks);

    void cancelTasks();
    void cancelTask(int taskId);
    int getTaskdId();

}