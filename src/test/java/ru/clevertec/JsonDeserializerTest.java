package ru.clevertec;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.clevertec.models.Customer;

import static org.junit.jupiter.api.Assertions.*;

class JsonDeserializerTest {

    static ObjectMapper mapper;
    static JsonDeserializer deserializer;

    @BeforeAll
    static void init() {
        mapper = new ObjectMapper();
        deserializer = new JsonDeserializer();
    }

    @Test
    @DisplayName("Десериализация БЕЗ дат")
    void shouldReturnTrue_whenConvertJsonToObject() throws Exception {
        // given
        String json = TestUtil.getJsonCustomer();

        // when
        Customer expectedCustomer = mapper.readValue(json, Customer.class);
        Customer actualCustomer = deserializer.convertJsonToObject(json, Customer.class);

        // then
        assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    @DisplayName("Десериализация некорректной строки")
    void shouldThrowException_whenConvertInvalidJsonToObject() {
        // given
        String json = TestUtil.getInvalidJsonFormat();

        // then
        assertThrows(IllegalArgumentException.class, () -> deserializer.convertJsonToObject(json, Customer.class));
    }
}
