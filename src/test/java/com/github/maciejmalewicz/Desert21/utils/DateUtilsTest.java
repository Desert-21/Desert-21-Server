package com.github.maciejmalewicz.Desert21.utils;

import com.github.maciejmalewicz.Desert21.testConfig.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    private final Date testingDate = Date.from(LocalDate.of(2022, 3, 31)
            .atStartOfDay(ZoneId.of("UTC")).toInstant());

    @BeforeEach
    void prepareDateUtils() throws Exception {
        Supplier<Date> testSupplier = () -> testingDate;
        ReflectionUtils.setFinalStatic(DateUtils.class.getDeclaredField("date"), testSupplier);
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