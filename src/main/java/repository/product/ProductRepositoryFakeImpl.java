package repository.product;

import model.Product;
import utils.FilterCriteria;
import utils.SortCriteria;
import utils.SortOrder;
import utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    public Optional<Product> update(Product p) {
        delete(p.getId());
        save(p);
        return Optional.of(p);
    }

    @Override
    public synchronized boolean delete(Long id) {
        products.removeIf(p -> Objects.equals(p.getId(), id));
        return true;
    }

    @Override
    public synchronized Optional<Product> getById(Long id) {
        return products.stream().filter(p -> Objects.equals(p.getId(), id)).findFirst();
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
    public synchronized boolean deleteOfGroup(Long groupId) {
        products.removeIf(p -> Objects.equals(p.getGroupId(), groupId));
        return true;
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

	@Override
	public boolean deleteByName(String name) {
		// TODO Auto-generated method stub
		return true;
	}

}
