package pt.ipleiria.estg.dei.ei.UpFeed.utils;

import java.io.Serializable;

public class Hour implements Serializable {
    private int hours;
    private int minutes;
    private int seconds;

    public Hour(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public Hour(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
