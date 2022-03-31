package com.github.maciejmalewicz.Desert21.utils;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomGeneratorTest {

    @Test
    void generateBetween() {
        var generated = IntStream.range(0, 10000)
                .mapToObj(i -> RandomGenerator.generateBetween(1, 10))
                .toList();

        var min = Collections.min(generated);
        var max = Collections.max(generated);
        assertTrue(min >= 1);
        assertTrue(max <= 10);
    }

    @Test
    void generateTrueOrFalse() {
        var generated = IntStream.range(0, 100)
                .mapToObj(i -> RandomGenerator.generateTrueOrFalse())
                .toList();
        var containsTrue = generated.stream().anyMatch(bool -> bool);
        var containsFalse = generated.stream().anyMatch(bool -> !bool);
        assertTrue(containsTrue);
        assertTrue(containsFalse);
    }
}