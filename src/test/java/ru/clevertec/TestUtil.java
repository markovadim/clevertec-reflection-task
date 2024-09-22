package ru.clevertec;

import ru.clevertec.models.Customer;
import ru.clevertec.models.Order;
import ru.clevertec.models.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestUtil {

    public static Customer getCustomer() {
        List<Product> products = List.of(
                new Product(UUID.randomUUID(), "1 name", 12.12, Map.of(UUID.randomUUID(), BigDecimal.ZERO, UUID.randomUUID(), BigDecimal.ZERO)),
                new Product(UUID.randomUUID(), "2 name", 33.33, Map.of(UUID.randomUUID(), BigDecimal.ZERO, UUID.randomUUID(), BigDecimal.ZERO))
        );

        List<Order> orders = List.of(
                new Order(UUID.randomUUID(), products, null)
        );
        Customer customer = new Customer(UUID.randomUUID(), "First Name", "Last Name", null, orders);
        return customer;
    }

    public static String getJsonCustomer() {
        return "{\"id\":\"4e8f9fa2-09fa-4df5-be92-866d5d6b95b0\",\"firstName\":\"First Name\",\"lastName\":\"Last Name\",\"dateBirth\":null,\"orders\":[{\"id\":\"1207fee7-1d7f-4109-9169-bf8967b9b6cd\",\"products\":[{\"id\":\"9bbbd37e-2288-4131-a3d6-1582577f8f19\",\"name\":\"1 name\",\"price\":12.12,\"map\":{\"da12b709-b0c5-4bf6-9c87-33e4089732dd\":0,\"eed38e07-7133-477e-8d1b-8b2f59ca44df\":0}},{\"id\":\"12a98b3f-d7ff-46ce-9382-fa45fb7c4c59\",\"name\":\"2 name\",\"price\":33.33,\"map\":{\"ec5596ae-4084-4bb6-ab41-3ffa42ab352b\":0,\"aa6fe97d-6932-4e18-a88d-e6decc526013\":0}}],\"createDate\":null}]}\n";
    }

    public static String getInvalidJsonFormat() {
        return "id:4f23f23f2, name:name";
    }
}
