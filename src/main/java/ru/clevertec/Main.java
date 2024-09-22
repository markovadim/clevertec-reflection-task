package ru.clevertec;

import ru.clevertec.models.Customer;
import ru.clevertec.models.Order;
import ru.clevertec.models.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Main {
    public static void main(String[] args) throws Exception {

        List<Product> products = List.of(
                new Product(UUID.randomUUID(), "1 name", 12.12, Map.of(UUID.randomUUID(), BigDecimal.ZERO, UUID.randomUUID(), BigDecimal.ZERO)),
                new Product(UUID.randomUUID(), "2 name", 33.33, Map.of(UUID.randomUUID(), BigDecimal.ZERO, UUID.randomUUID(), BigDecimal.ZERO))
        );

        List<Order> orders = List.of(
                new Order(UUID.randomUUID(), products, OffsetDateTime.now())
        );
        Customer customer = new Customer(UUID.randomUUID(), "First Name", "Last Name", LocalDate.now(), orders);

        JsonSerializer serializer = new JsonSerializer();
        String str = serializer.convertObjectToJsonString(customer);
        System.out.println(str);

        JsonDeserializer deserializer = new JsonDeserializer();
        Customer customer1 = deserializer.convertJsonToObject(str, Customer.class);

        assert customer.equals(customer1);
    }
}
