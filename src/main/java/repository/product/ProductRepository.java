package repository.product;

import model.Product;
import utils.FilterCriteria;
import utils.SortCriteria;
import utils.SortOrder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product p);

    Optional<Product> update(Product p);

    boolean delete(Long id);

    Optional<Product> getById(Long id);

    List<Product> getAll();

    void deleteAll();

    boolean deleteOfGroup(Long groupId);
    
    boolean deleteByName(String name);
    
    boolean existsWithName(String name);

    boolean existsWithId(Long id);

    List<Product> listByCriteria(Map<SortCriteria, SortOrder> sortMap);

    List<Product> findByCriteria(Map<FilterCriteria, Object> criteria);




}
