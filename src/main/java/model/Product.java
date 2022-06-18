package model;

import lombok.Getter;
import lombok.Setter;
import utils.Utils;

@Getter
public class Product {

    private final Long id;
    @Setter
    private String name;
    @Setter
    private String description;
    @Setter
    private String producer;
    @Setter
    private int quantity;
    @Setter
    private double price;
    @Setter
    private Long groupId;

    public Product(Long id, String name, String description, String producer, int quantity, double price, Long groupId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.producer = producer;
        this.quantity = quantity;
        this.price = price;
        this.groupId = groupId;
    }

    public Product(String name, String description, String producer, int quantity, double price, Long groupId) {
        this(Utils.generateId(),name,description,producer,quantity,price,groupId);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", producer='" + producer + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", groupId=" + groupId +
                '}';
    }
}
