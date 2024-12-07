package ru.jetlabs.acquiringmockbackend.util;
import java.time.LocalDateTime;

public class TimeUtil {

    public static boolean nowBetweenOf(LocalDateTime first, LocalDateTime second){
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(first) && now.isBefore(second);
    }
}
