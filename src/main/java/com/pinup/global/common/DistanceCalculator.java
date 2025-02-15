package com.pinup.global.common;

import org.springframework.stereotype.Component;

import static java.lang.Math.*;

@Component
public class DistanceCalculator {

    private static final double EARTH_RADIUS = 6371; // 단위: km

    public static double getDistanceFromPlace(
            double currLat, double currLon, double placeLat, double placeLon
    ) {
        return EARTH_RADIUS *
                acos(cos(toRadians(currLat)) *
                    cos(toRadians(placeLat)) *
                    cos(toRadians(placeLon) - toRadians(currLon)) +
                    sin(toRadians(currLat)) * sin(toRadians(placeLat))
                );
    }
}