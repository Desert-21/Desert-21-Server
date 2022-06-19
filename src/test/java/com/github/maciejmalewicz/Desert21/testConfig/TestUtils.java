package com.github.maciejmalewicz.Desert21.testConfig;

import java.util.Collection;
import java.util.List;

public class TestUtils {

     public static <T> T findWithinCollection(Collection<?> collection, Class<T> clazz) {
        return collection.stream()
                .filter(clazz::isInstance)
                 .map(clazz::cast)
                 .findAny()
                 .orElseThrow();
    }

    public static <T> List<T> findAllWithinCollection(Collection<?> collection, Class<T> clazz) {
        return collection.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
    }
}
