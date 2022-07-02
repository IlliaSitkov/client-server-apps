package repository.product;

import model.Product;
import utils.FilterCriteria;
import utils.SortCriteria;
import utils.SortOrder;

import java.util.List;
import java.util.Map;

public interface ProductRepository {

    Product save(Product p);

    Product update(Product p);

    void delete(Long id);

    Product getById(Long id);

    List<Product> getAll();

    void deleteAll();

    void deleteOfGroup(Long groupId);

    boolean existsWithName(String name);

    boolean existsWithId(Long id);

    List<Product> findByCriteria(
            String searchString,
            double minPrice, double maxPrice,
            int minQuantity, int maxQuantity,
            Long groupId);

    List<Product> listByCriteria(Map<SortCriteria, SortOrder> sortMap);

    List<Product> findByCriteria(Map<FilterCriteria, Object> criteria);


}
