package repository.product;

import model.Product;

import java.util.List;

public interface ProductRepository {

    Product save(Product p);

    Product update(Product p);

    void delete(Long id);

    Product getById(Long id);

    List<Product> getAll();

    void deleteAll();

    void deleteOfGroup(Long groupId);

    boolean existsWithName(String name);


}
