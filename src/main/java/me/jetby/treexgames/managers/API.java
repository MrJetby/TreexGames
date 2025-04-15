package me.jetby.treexgames.managers;

import java.util.List;

public class API {

    private static int timer;
    private static List<String> timeZones;
    private static int minPlayers;
    private static String nowEvent;
    private static String nextEvent;

    public static int getTimer() { return timer; }
    public static void setTimer(int timer) { API.timer = timer; }

    public static int getMinPlayers() { return minPlayers; }
    public static void setMinPlayers(int minPlayers) { API.minPlayers = minPlayers; }

    public static String getNowEvent() { return nowEvent; }
    public static void setNowEvent(String nowEvent) { API.nowEvent = nowEvent; }

    public static String getNextEvent() { return nextEvent; }
    public static void setNextEvent(String nextEvent) { API.nextEvent = nextEvent; }

    public static List<String> getTimeZones() {return timeZones;}
    public static void setTimeZones(List<String> timeZones) {API.timeZones = timeZones;}
}
