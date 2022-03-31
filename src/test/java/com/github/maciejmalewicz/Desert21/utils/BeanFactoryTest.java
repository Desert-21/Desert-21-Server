package com.github.maciejmalewicz.Desert21.utils;

import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeanFactoryTest {

    @Data
    public static class ExampleClass {
        private String property1;
        private int property2;
    }

    @Data
    public static class ExampleClass2 {
        private String property1;
        private int property2;
    }

    @Test
    void getBeanWithName() {
        var bean = BeanFactory.getBean("testExamples/testConfig.yml", ExampleClass.class, "exampleClass");
        assertNotNull(bean);
        assertEquals(bean.getProperty1(), "Some text");
        assertEquals(bean.property2, 123);
    }

    @Test
    void getBeanWithClassNameOnly() {
        var bean = BeanFactory.getBean("testExamples/testConfig.yml", ExampleClass.class);
        assertNotNull(bean);
        assertEquals(bean.getProperty1(), "Some text");
        assertEquals(bean.property2, 123);
    }

}