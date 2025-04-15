package me.jetby.treexgames.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final Pattern hexPattern1 = Pattern.compile("&#([a-fA-F0-9]{6})", Pattern.CASE_INSENSITIVE);
    private static final Pattern hexPattern2 = Pattern.compile("&x([&a-fA-F0-9]){12}", Pattern.CASE_INSENSITIVE);
    private static final Pattern hexPattern3 = Pattern.compile("<#([a-fA-F0-9]{6})>", Pattern.CASE_INSENSITIVE);

    public static  String hex( String message) {
        message = replaceHexFormat(message, hexPattern1, "&#");
        message = replaceHexFormat(message, hexPattern2, "&x");
        message = replaceHexFormat(message, hexPattern3, "<#");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String replaceHexFormat(String message, Pattern pattern, String formatPrefix) {
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexColor = matcher.group();
            String normalizedColor = normalizeHexColor(hexColor, formatPrefix);
            message = message.replace(hexColor, ChatColor.of(normalizedColor).toString());
        }
        return message;
    }

    private static String normalizeHexColor(String hexColor, String formatPrefix) {
        // Если формат &#RRGGBB или <#RRGGBB>
        if (formatPrefix.equals("&#") || formatPrefix.equals("<#")) {
            hexColor = hexColor.substring(2, 8); // Убираем "&#"/"<#" и оставляем только RRGGBB
            return "#" + hexColor;
        }

        // Если формат &x&R&R&G&G&B&B
        if (formatPrefix.equals("&x")) {
            StringBuilder normalized = new StringBuilder("#");
            for (int i = 2; i < hexColor.length(); i += 2) {
                char colorChar = hexColor.charAt(i + 1);
                if (Character.isDigit(colorChar) || (colorChar >= 'a' && colorChar <= 'f') || (colorChar >= 'A' && colorChar <= 'F')) {
                    normalized.append(colorChar);
                }
            }
            return normalized.toString();
        }

        return hexColor;
    }
}
