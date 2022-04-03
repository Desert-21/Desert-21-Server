package com.github.maciejmalewicz.Desert21.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilsTest {

    private final Date testingDate = Date.from(LocalDate.of(2022, 3, 31)
            .atStartOfDay(ZoneId.of("UTC")).toInstant());

    private Supplier<Date> currentSupplier;

    @BeforeEach
    void prepareDateUtils() throws Exception {
        var dateField = DateUtils.class.getDeclaredField("date");
        dateField.setAccessible(true);

        currentSupplier = (Supplier<Date>) dateField.get(null);

        Supplier<Date> testSupplier = () -> testingDate;
        dateField.set(null, testSupplier);
    }

    @AfterEach
    void afterDateUtils() throws Exception {
        var dateField = DateUtils.class.getDeclaredField("date");
        dateField.setAccessible(true);
        dateField.set(null, currentSupplier);
    }

    @Test
    void millisecondsFromNow() {
        var date = DateUtils.millisecondsFromNow(1000);
        assertEquals(1648684801000L, date.getTime());
    }

    @Test
    void millisecondsTo() {
        var ms = DateUtils.millisecondsTo(new Date(1648684801000L));
        assertEquals(1000L, ms);
    }
}