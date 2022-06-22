package repository.product;

import exceptions.ProductNotFoundException;
import model.Product;
import utils.Utils;

import java.util.List;
import java.util.Objects;

public class ProductRepositoryImpl implements ProductRepository {

    private List<Product> products;

    private static volatile ProductRepositoryImpl productRepository;

    private ProductRepositoryImpl() {
        this.products = Utils.getEmptySynchronizedList();
    }

    public static ProductRepositoryImpl getInstance() {
        ProductRepositoryImpl repository = productRepository;
        if (repository != null) {
            return repository;
        }
        synchronized (ProductRepositoryImpl.class) {
            if (productRepository == null) {
                productRepository = new ProductRepositoryImpl();
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
}
