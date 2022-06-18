package service.product;

import model.Product;

import java.util.List;

public interface ProductService {

    Product createProduct(String name, String description, String producer, int quantity, double price, long groupId);

    Product updateProduct(Long productId, String name, String description, String producer, int quantity, double price, long groupId);

    void deleteProduct(Long productId);

    Product getProductById(Long productId);

    boolean addProducts(Long productId, int quantityToAdd);

    boolean takeProducts(Long productId, int quantityToRemove);

    List<Product> getAllProducts();

    int getProductQuantity(Long productId);

    boolean setProductPrice(Long productId, double price);

    void deleteAllProducts();

}
