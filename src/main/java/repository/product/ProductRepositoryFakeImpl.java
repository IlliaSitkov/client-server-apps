package repository.product;

import exceptions.ProductNotFoundException;
import model.Product;
import utils.FilterCriteria;
import utils.SortCriteria;
import utils.SortOrder;
import utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductRepositoryFakeImpl implements ProductRepository {

    private List<Product> products;

    private static volatile ProductRepositoryFakeImpl productRepository;

    private ProductRepositoryFakeImpl() {
        this.products = Utils.getEmptySynchronizedList();
    }

    public static ProductRepositoryFakeImpl getInstance() {
        ProductRepositoryFakeImpl repository = productRepository;
        if (repository != null) {
            return repository;
        }
        synchronized (ProductRepositoryFakeImpl.class) {
            if (productRepository == null) {
                productRepository = new ProductRepositoryFakeImpl();
            }
            return productRepository;
        }
    }

    /*
     some methods are not synchronized as they make
     use of already synchronized list
     (not all methods of the synchronized list are synchronized though)
     */


    @Override
    public Product save(Product p) {
        products.add(p);
        return p;
    }

    @Override
    public Product update(Product p) {
        delete(p.getId());
        save(p);
        return p;
    }

    @Override
    public synchronized void delete(Long id) {
        products.removeIf(p -> Objects.equals(p.getId(), id));
    }

    @Override
    public synchronized Product getById(Long id) {
        return products.stream().filter(p -> Objects.equals(p.getId(), id)).findFirst().orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public synchronized List<Product> getAll() {
        return products;
    }

    @Override
    public synchronized void deleteAll() {
        products = Utils.getEmptySynchronizedList();
    }

    @Override
    public synchronized void deleteOfGroup(Long groupId) {
        products.removeIf(p -> Objects.equals(p.getGroupId(), groupId));
    }

    @Override
    public synchronized boolean existsWithName(String name) {
        return products.stream().anyMatch(p -> p.getName().equalsIgnoreCase(name));
    }

    @Override
    public boolean existsWithId(Long id) {
        return false;
    }


    @Override
    public List<Product> listByCriteria(Map<SortCriteria, SortOrder> sortMap) {
        return null;
    }

    @Override
    public List<Product> findByCriteria(Map<FilterCriteria, Object> criteria) {
        return null;
    }

}
