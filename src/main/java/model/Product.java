package model;

import lombok.Getter;
import lombok.Setter;
import utils.Utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    private final Long id;
    @Setter
    @Column(name = "product_name")
    private String name;
    @Setter
    @Column(name = "product_description")
    private String description;
    @Setter
    @Column(name = "product_producer")
    private String producer;
    @Setter
    @Column(name = "product_quantity")
    private int quantity;
    @Setter
    @Column(name = "product_price")
    private double price;
    @Setter
    @Column(name = "group_id")
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

    public Product() {
        id = Utils.generateId();
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
