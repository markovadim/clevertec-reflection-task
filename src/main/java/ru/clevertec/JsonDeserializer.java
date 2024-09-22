package ru.clevertec;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class JsonDeserializer {

    public <T> T convertJsonToObject(String json, Class<T> clazz) throws Exception {
        json = json.trim();

        if (json.startsWith("{") && json.endsWith("}")) {
            return convertObject(json, clazz);
        } else {
            throw new IllegalArgumentException("Invalid format json string.");
        }
    }

    private <T> T convertObject(String json, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        Map<String, String> fieldsMap = convertJsonToMap(json);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if (fieldsMap.containsKey(fieldName)) {
                String value = fieldsMap.get(fieldName);
                field.set(instance, convertValue(value, field.getType(), field.getGenericType()));
            }
        }

        return instance;
    }

    private Map<String, String> convertJsonToMap(String json) {
        json = json.substring(1, json.length() - 1).trim();
        Map<String, String> map = new HashMap<>();
        String[] fields = splitJsonObject(json);

        for (String field : fields) {
            String[] keyValue = field.split(":", 2);
            String key = keyValue[0].trim().replaceAll("\"", "");
            String value = keyValue[1].trim();
            map.put(key, value);
        }
        return map;
    }

    private String[] splitJsonObject(String json) {
        List<String> result = new ArrayList<>();
        int braceCounter = 0;
        int bracketCounter = 0;
        StringBuilder current = new StringBuilder();

        for (char c : json.toCharArray()) {
            if (c == '{') braceCounter++;
            if (c == '}') braceCounter--;
            if (c == '[') bracketCounter++;
            if (c == ']') bracketCounter--;

            if (c == ',' && braceCounter == 0 && bracketCounter == 0) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            result.add(current.toString().trim());
        }
        return result.toArray(new String[0]);
    }

    private Object convertValue(String value, Class<?> fieldType, Type genericType) throws Exception {
        value = value.trim();
        if (value.equals("null")) {
            return null;
        }
        if (fieldType == String.class) {
            return value.replaceAll("\"", "");
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(value);
        } else if (fieldType == double.class || fieldType == Double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (List.class.isAssignableFrom(fieldType)) {
            return convertListConversion(value, genericType);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            return convertMap(value, genericType);
        } else if (UUID.class.isAssignableFrom(fieldType) || Date.class.isAssignableFrom(fieldType)
                || LocalDate.class.isAssignableFrom(fieldType) || LocalDateTime.class.isAssignableFrom(fieldType)
                || OffsetDateTime.class.isAssignableFrom(fieldType) || BigDecimal.class.isAssignableFrom(fieldType)) {
            return convertDateUuidBigDecimal(value, fieldType);
        } else {
            return convertJsonToObject(value, fieldType);
        }
    }

    private Object convertDateUuidBigDecimal(String value, Class<?> clazz) throws ParseException {
        if (clazz == UUID.class) {
            return UUID.fromString(value.replaceAll("\"", ""));
        } else if (clazz == Date.class) {
            return new SimpleDateFormat("yyyy-MM-dd").parse(value.replaceAll("\"", ""));
        } else if (clazz == LocalDate.class) {
            return LocalDate.parse(value.replaceAll("\"", ""));
        } else if (clazz == LocalDateTime.class) {
            return LocalDateTime.parse(value.replaceAll("\"", ""));
        } else if (clazz == OffsetDateTime.class) {
            return OffsetDateTime.parse(value.replaceAll("\"", ""));
        } else if (clazz == BigDecimal.class) {
            long val = Long.parseLong(value.replaceAll("\"", ""));
            return BigDecimal.valueOf(val);
        }
        return value;
    }

    private Object convertListConversion(String value, Type genericType) throws Exception {
        if (!value.startsWith("[") || !value.endsWith("]")) {
            throw new IllegalArgumentException("Invalid list part json format");
        }

        value = value.substring(1, value.length() - 1).trim();
        String[] elements = splitJsonObject(value);

        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type elementType = parameterizedType.getActualTypeArguments()[0];
            Class<?> elementClass = (Class<?>) elementType;

            List<Object> result = new ArrayList<>();
            for (String element : elements) {
                result.add(convertValue(element, elementClass, elementType));
            }
            return result;
        }
        return new ArrayList<>();
    }

    private Object convertMap(String value, Type genericType) throws Exception {
        if (!value.startsWith("{") || !value.endsWith("}")) {
            throw new IllegalArgumentException("Invalid map part json format");
        }
        Map<String, String> map = convertJsonToMap(value);

        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type keyType = parameterizedType.getActualTypeArguments()[0];
            Type valueType = parameterizedType.getActualTypeArguments()[1];
            Class<?> keyClass = (Class<?>) keyType;
            Class<?> valueClass = (Class<?>) valueType;

            Map<Object, Object> result = new HashMap<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object key = convertValue("\"" + entry.getKey() + "\"", keyClass, keyType);
                Object val = convertValue(entry.getValue(), valueClass, valueType);
                result.put(key, val);
            }
            return result;
        }
        return new HashMap<>();
    }
}
