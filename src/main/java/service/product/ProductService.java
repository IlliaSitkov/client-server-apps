package service.product;

import model.Product;

import java.util.List;

public interface ProductService {

    Product createProduct(String name, String description, String producer, int quantity, double price, long groupId);

    Product updateProduct(Long productId, String name, String description, String producer, int quantity, double price, long groupId);

    void deleteProduct(Long productId);

    Product getProductById(Long productId);

    void addProducts(Long productId, int quantityToAdd);

    void takeProducts(Long productId, int quantityToRemove);

    List<Product> getAllProducts();

    int getProductQuantity(Long productId);

    void setProductPrice(Long productId, double price);

    void deleteAllProducts();

}
