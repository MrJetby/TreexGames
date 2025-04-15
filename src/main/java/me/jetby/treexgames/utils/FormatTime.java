package me.jetby.treexgames.utils;

import static me.jetby.treexgames.configurations.Config.CFG;

public class FormatTime {

    public static String stringFormat(int Sec) {
        int hour = Sec / 3600;
        int min = Sec % 3600 / 60;
        int sec = Sec % 60;
        StringBuilder formattedTime = new StringBuilder();

        if (hour > 0) {
            formattedTime.append(formatUnit(hour,
                    CFG().getString("placeholders.formattedTime.hours.form1", "час"),
                    CFG().getString("placeholders.formattedTime.hours.form2", "часа"),
                    CFG().getString("placeholders.formattedTime.hours.form3", "часов"))).append(" ");
        }

        if (min > 0) {
            formattedTime.append(formatUnit(min,
                            CFG().getString("placeholders.formattedTime.minutes.form1", "минуту"),
                            CFG().getString("placeholders.formattedTime.minutes.form2", "минуты"),
                            CFG().getString("placeholders.formattedTime.minutes.form3", "минут"))).append(" ");
        }

        // Всегда добавляем секунды, если они есть
        if (sec > 0) {
            formattedTime.append(formatUnit(sec,
                    CFG().getString("placeholders.formattedTime.seconds.form1", "секунду"),
                    CFG().getString("placeholders.formattedTime.seconds.form2", "секунды"),
                    CFG().getString("placeholders.formattedTime.seconds.form3", "секунд"))).append(" ");
        }

        // Если ни часы, ни минуты, ни секунды не указаны, добавляем "0 секунд"
        if (hour == 0 && min == 0 && sec == 0) {
            formattedTime.append("0 сек");
        }

        return formattedTime.toString().trim();
    }

    private static String formatUnit(int value, String form1, String form2, String form5) {
        value = Math.abs(value);
        int remainder10 = value % 10;
        int remainder100 = value % 100;

        if (remainder10 == 1 && remainder100 != 11) {
            return String.format("%d %s", value, form1);
        } else if (remainder10 >= 2 && remainder10 <= 4 && (remainder100 < 10 || remainder100 >= 20)) {
            return String.format("%d %s", value, form2);
        } else {
            return String.format("%d %s", value, form5);
        }
    }
}
