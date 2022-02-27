package com.github.maciejmalewicz.Desert21.utils;

import java.util.Date;

public class DateUtils {

    public static Date millisecondsFromNow(long milliseconds) {
        return new Date(new Date().getTime() + milliseconds);
    }

    public static long millisecondsTo(Date date) {
        return date.getTime() - new Date().getTime();
    }
}
