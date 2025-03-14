package com.pinup.global.common;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Formatter {

    public static double formatStarRating(double starRating) {
        return BigDecimal.valueOf(starRating).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public static String formatDistance(Double distance) {
        if (distance == null) {
            return null;
        }
        return distance < 1
                ? Math.round(distance * 1000) + "m"
                : Math.round(distance) + "km";
    }

    public static String formatDate(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("yy.MM.dd").format(dateTime);
    }
}
