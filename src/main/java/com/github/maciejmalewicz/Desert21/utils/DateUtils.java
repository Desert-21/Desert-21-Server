package com.github.maciejmalewicz.Desert21.utils;

import java.util.Date;
import java.util.function.Supplier;

public class DateUtils {

    private static Supplier<Date> date = Date::new;

    public static Date millisecondsFromNow(long milliseconds) {
        return new Date(date.get().getTime() + milliseconds);
    }

    public static long millisecondsTo(Date toDate) {
        return toDate.getTime() - date.get().getTime();
    }
}
