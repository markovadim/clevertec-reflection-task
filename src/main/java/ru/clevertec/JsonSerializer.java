package ru.clevertec;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class JsonSerializer {

    public String convertObjectToJsonString(Object object) throws IllegalAccessException {
        if (object == null) {
            return "null";
        }

        StringBuilder result = new StringBuilder();
        Class<?> clazz = object.getClass();

        if (isPrimitiveOrString(object)) {
            result.append(convertPrimitiveStringBigDecimalUuidOrDate(object));
        } else if (object instanceof Map) {
            result.append(convertMap((Map<?, ?>) object));
        } else if (object instanceof List) {
            result.append(convertList((List<?>) object));
        } else if (object instanceof BigDecimal || object instanceof UUID || object instanceof LocalDate || object instanceof Date || object instanceof OffsetDateTime) {
            result.append(convertPrimitiveStringBigDecimalUuidOrDate(object));
        } else if (object.getClass().isArray()) {
            result.append(convertArray(object));
        } else {
            result.append(convertObject(object, clazz));
        }
        return result.toString();
    }

    private String convertPrimitiveStringBigDecimalUuidOrDate(Object value) {
        if (value instanceof String || value instanceof Character || value instanceof UUID ||
                value instanceof LocalDate || value instanceof Date || value instanceof OffsetDateTime) {
            return "\"" + value + "\"";
        } else if (value instanceof BigDecimal) {
            return "" + value + "";
        }
        return value.toString();
    }

    private String convertMap(Map<?, ?> map) throws IllegalAccessException {
        StringBuilder resultPart = new StringBuilder();
        resultPart.append("{");
        int i = 0;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            resultPart.append("\"").append(entry.getKey()).append("\":");
            resultPart.append(convertObjectToJsonString(entry.getValue()));
            if (i < map.size() - 1) {
                resultPart.append(",");
            }
            i++;
        }

        resultPart.append("}");
        return resultPart.toString();
    }

    private String convertList(List<?> list) throws IllegalAccessException {
        StringBuilder resultPart = new StringBuilder();
        resultPart.append("[");

        for (int i = 0; i < list.size(); i++) {
            resultPart.append(convertObjectToJsonString(list.get(i)));
            if (i < list.size() - 1) {
                resultPart.append(",");
            }
        }

        resultPart.append("]");
        return resultPart.toString();
    }

    private String convertArray(Object list) throws IllegalAccessException {
        StringBuilder resultPart = new StringBuilder();
        resultPart.append("[");

        for (int i = 0; i < Array.getLength(list); i++) {
            resultPart.append(convertObjectToJsonString(Array.get(list, i)));
            if (i < Array.getLength(list) - 1) {
                resultPart.append(",");
            }
        }

        resultPart.append("]");
        return resultPart.toString();
    }

    private String convertObject(Object object, Class<?> clazz) throws IllegalAccessException {
        StringBuilder resultPart = new StringBuilder();
        resultPart.append("{");
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            resultPart.append("\"").append(field.getName()).append("\":");
            Object value = field.get(object);
            if (value == null) {
                resultPart.append("null");
            } else if (isPrimitiveOrString(value)) {
                resultPart.append(convertPrimitiveStringBigDecimalUuidOrDate(value));
            } else if (value instanceof Map) {
                resultPart.append(convertMap((Map<?, ?>) value));
            } else if (value instanceof List) {
                resultPart.append(convertList((List<?>) value));
            } else {
                resultPart.append(convertObjectToJsonString(value));
            }
            if (i < fields.length - 1) {
                resultPart.append(",");
            }
        }

        resultPart.append("}");
        return resultPart.toString();
    }

    private boolean isPrimitiveOrString(Object value) {
        return value.getClass().isPrimitive() || value instanceof String ||
                value instanceof Integer || value instanceof Long ||
                value instanceof Boolean || value instanceof Double ||
                value instanceof Float || value instanceof Byte ||
                value instanceof Short || value instanceof Character;
    }
}
