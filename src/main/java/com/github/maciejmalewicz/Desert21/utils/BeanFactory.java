package com.github.maciejmalewicz.Desert21.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BeanFactory {

    public static <T> T getBean(String sourceYAML, Class<T> toFind, String name) {
        var map = buildMap(sourceYAML);
        return getBean(map, toFind, name);
    }

    public static <T> T getBean(String sourceYAML, Class<T> toFind) {
        var map = buildMap(sourceYAML);
        return getBean(map, toFind);
    }

    public static <T> T getBean(Map<String, Object> mainMap, Class<T> toFind, String name) {
        Map<String, Object> inside = (Map) mainMap.get(name);

        var mapper = new ObjectMapper();
        return mapper.convertValue(inside, toFind);
    }

    public static <T> T getBean(Map<String, Object> mainMap, Class<T> toFind) {
        String target = toFind.getSimpleName();
        target = convertToCamel(target);
        Map<String, Object> inside = (Map) mainMap.get(target);

        var mapper = new ObjectMapper();
        return mapper.convertValue(inside, toFind);
    }

    public static String convertToCamel(String toConvert) {
        return toConvert.substring(0, 1).toLowerCase() + toConvert.substring(1);
    }

    public static HashMap<String, Object> buildMap(String sourceYAML) {
        Yaml yaml = new Yaml();
        try {
            InputStream in = BeanFactory.class.getClassLoader()
                    .getResourceAsStream(sourceYAML);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            var yamlString = reader.lines().reduce((prev, next) -> prev + "\n" + next).orElse("");
            return yaml.load(yamlString);
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }
}


