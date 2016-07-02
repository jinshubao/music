package com.jean.music.utils;

import com.sun.org.apache.xpath.internal.operations.Number;
import javafx.util.Duration;

import java.text.DecimalFormat;

/**
 * Created by jinshubao on 2016/6/3.
 */
public class FormatUtil {
    private static DecimalFormat format = new DecimalFormat("#.##");

    public static String formatTime(Duration duration) {
        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {
                return String.format("%d:%02d:%02d", durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d", durationMinutes, durationSeconds);
            }
        }
        return "00:00";
    }

    public static String formatTime(double second) {
        if (second > 0) {
            int intDuration = (int) second;
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {
                return String.format("%d:%02d:%02d", durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d", durationMinutes, durationSeconds);
            }
        }
        return "00:00";
    }

    public static String formatFileSize(long size) {
        if (size >= 1024 * 1204 * 1024) {
            return format.format(size / (1024 * 1204 * 1024D)) + "G";
        } else if (size >= 1024 * 1204) {
            return format.format(size / (1024 * 1204D)) + "M";
        } else if (size >= 1024) {
            return format.format(size / (1024D)) + "K";
        }
        return format.format(size) + "B";
    }

    public static String numberFormat(Number number) {
        return format.format(number);
    }
}
