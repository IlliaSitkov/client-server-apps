package service.product;

import exceptions.InsufficientQuantityException;
import exceptions.NameNotUniqueException;
import exceptions.ProductNotFoundException;
import model.Product;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import service.group.GroupService;
import service.group.GroupServiceImpl;
import utils.FilterCriteria;
import utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private static volatile ProductServiceImpl productService;

    private final GroupService groupService;

    private ProductServiceImpl() {
        this.productRepository = ProductRepositoryImpl.getInstance();
        this.groupService = GroupServiceImpl.getInstance();
    }

    public static ProductServiceImpl getInstance() {
        ProductServiceImpl service = productService;
        if (service != null) {
            return service;
        }
        synchronized (ProductServiceImpl.class) {
            if (productService == null) {
                productService = new ProductServiceImpl();
            }
            return productService;
        }
    }

    @Override
    public synchronized Product createProduct(String name, String description, String producer, int quantity, double price, long groupId) {
        synchronized (groupService) {
            String pName = Utils.processString(name);
            String pDescription = Utils.processString(description);
            String pProducer = Utils.processString(producer);
            validateParams(quantity, price, groupId, pName, pDescription, pProducer);
            Product p = new Product(pName, pDescription, pProducer, quantity, price, groupId);
            return productRepository.save(p);
        }
    }

    // TODO Should quantity, price be allowed to be updated?
    @Override
    public synchronized Product updateProduct(Long productId, String name, String description, String producer, double price, long groupId) {
        synchronized (groupService) {
            Product product = getProductById(productId);
            String pName = Utils.processString(name);
            String pDescription = Utils.processString(description);
            String pProducer = Utils.processString(producer);
            validateParams(product.getQuantity(), price, groupId, pName, pDescription, pProducer);
            product.setName(pName);
            product.setDescription(description);
            product.setPrice(price);
            product.setGroupId(groupId);
            return productRepository.update(product).orElseThrow(() -> new ProductNotFoundException(productId));
        }
    }

    @Override
    public synchronized void deleteProduct(Long productId) {
        productRepository.delete(productId);
    }

    @Override
    public synchronized Product getProductById(Long productId) {
        return productRepository.getById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public synchronized void addProducts(Long productId, int quantityToAdd) {
        Product product = getProductById(productId);
        Utils.validateNumber(quantityToAdd);
        product.setQuantity(product.getQuantity() + quantityToAdd);
        productRepository.update(product);
    }

    @Override
    public synchronized void takeProducts(Long productId, int quantityToRemove) {
        Product product = getProductById(productId);
        Utils.validateNumber(quantityToRemove);
        if (product.getQuantity() < quantityToRemove) {
            throw new InsufficientQuantityException(product.getQuantity(), quantityToRemove);
        }
        product.setQuantity(product.getQuantity() - quantityToRemove);
        productRepository.update(product);
    }

    @Override
    public synchronized List<Product> getAllProducts() {
        return productRepository.getAll();
    }

    @Override
    public synchronized int getProductQuantity(Long productId) {
        return getProductById(productId).getQuantity();
    }

    @Override
    public synchronized void setProductPrice(Long productId, double price) {
        Product product = getProductById(productId);
        Utils.validateNumber(price);
        product.setPrice(price);
        productRepository.update(product);
    }

    @Override
    public synchronized void deleteAllProducts() {
        productRepository.deleteAll();
    }

    @Override
    public List<Product> getFilteredProducts(String queryString) {
        if (queryString == null) {
            return getAllProducts();
        }
        Map<FilterCriteria, Object> map = new HashMap<>();
        for (String string: queryString.split("&")) {
            String[] keyValue = string.split("=");
            map.put(FilterCriteria.getValue(keyValue[0]), keyValue[1]);
        }
        return productRepository.filterByCriteria(map);
    }

    private void validateNameIsUnique(String name) {
        if (productRepository.existsWithName(name)) {
            throw new NameNotUniqueException(name);
        }
    }

    private void validateParams(int quantity, double price, long groupId, String pName, String pDescription, String pProducer) {
        validateNameIsUnique(pName);
        Utils.validateNumber(quantity);
        Utils.validateNumber(price);
        Utils.validateString(pName, true, 100);
        Utils.validateString(pProducer, true, 100);
        Utils.validateString(pDescription, false, 255);
        groupService.getGroupById(groupId);
    }


}
