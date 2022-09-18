package fr.aerwyn81.oreregen.utils;

public class MillisecondConverter {

    private final long milliseconds;
    private final long seconds;
    private final long minutes;
    private final long hours;
    private final long days;

    public MillisecondConverter(long millis) {
        this.milliseconds = millis;
        this.seconds = millis / 1000;
        this.minutes = seconds / 60;
        this.hours = minutes / 60;
        this.days = hours / 24;
    }

    public long getMilliseconds() {
        return milliseconds % 1000;
    }

    public long getSeconds() {
        return seconds % 60;
    }

    public long getMinutes() {
        return minutes % 60;
    }

    public long getHours() {
        return hours % 24;
    }

    public long getDays() {
        return days;
    }
}