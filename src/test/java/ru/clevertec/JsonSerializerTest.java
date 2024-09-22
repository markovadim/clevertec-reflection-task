package ru.clevertec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.clevertec.models.Customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonSerializerTest {

    static ObjectMapper mapper;
    static JsonSerializer serializer;

    @BeforeAll
    static void init() {
        mapper = new ObjectMapper();
        serializer = new JsonSerializer();
    }

    @Test
    @DisplayName("Сериализация объекта БЕЗ дат")
    void shouldReturnTrue_whenConvertObjectToJsonStringByCustomSerializerWithoutDate() throws IllegalAccessException, JsonProcessingException {
        // given
        Customer customer = TestUtil.getCustomer();

        // when
        String expectedString = mapper.writeValueAsString(customer);
        String actualString = serializer.convertObjectToJsonString(customer);

        // then
        assertEquals(expectedString, actualString);
    }

    @Test
    @DisplayName("Парсинг null объекта")
    void shouldReturnTrue_whenSerializerNullObject() throws IllegalAccessException {
        // given
        Customer nullCustomer = null;

        // when
        String expectedString = "null";
        String actualString = serializer.convertObjectToJsonString(nullCustomer);

        // then
        assertEquals(expectedString, actualString);
    }
}
