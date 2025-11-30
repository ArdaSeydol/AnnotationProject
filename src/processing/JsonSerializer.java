package processing;

import json.JsonIgnore;
import json.JsonProperty;
import json.JsonSerializable;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class JsonSerializer {

    public static String serialize(Object obj) throws IllegalAccessException {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize null object");
        }

        Class<?> clazz = obj.getClass();

        if (!clazz.isAnnotationPresent(JsonSerializable.class)) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getName() + " is not annotated with @JsonSerializable");
        }

        StringJoiner joiner = new StringJoiner(",");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(JsonIgnore.class)) {
                continue;
            }

            String key;
            if (field.isAnnotationPresent(JsonProperty.class)) {
                JsonProperty prop = field.getAnnotation(JsonProperty.class);
                key = prop.value();
            } else {
                key = field.getName();
            }

            Object value = field.get(obj);
            String valueString;

            if (value == null) {
                valueString = "null";
            } else if (value instanceof String s) {
                valueString = "\"" + escapeJson(s) + "\"";
            } else {
                valueString = String.valueOf(value);
            }

            String element = "\"" + escapeJson(key) + "\":" + valueString;
            joiner.add(element);
        }

        return "{" + joiner.toString() + "}";
    }

    private static String escapeJson(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
