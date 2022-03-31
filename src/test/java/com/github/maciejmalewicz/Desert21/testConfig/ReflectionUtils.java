package com.github.maciejmalewicz.Desert21.testConfig;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

//        Field modifiersField = Field.class.getDeclaredField("modifiers");
//        modifiersField.setAccessible(true);
//        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
