package service.product;

import exceptions.NameNotUniqueException;
import model.Product;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import service.group.GroupService;
import service.group.GroupServiceImpl;
import utils.Utils;

import java.util.List;

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
    public synchronized Product updateProduct(Long productId, String name, String description, String producer, int quantity, double price, long groupId) {
        synchronized (groupService) {
            String pName = Utils.processString(name);
            String pDescription = Utils.processString(description);
            String pProducer = Utils.processString(producer);
            validateParams(quantity, price, groupId, pName, pDescription, pProducer);
            Product product = getProductById(productId);
            product.setName(pName);
            product.setDescription(description);
            product.setQuantity(quantity);
            product.setPrice(price);
            product.setGroupId(groupId);
            return productRepository.update(product);
        }
    }

    @Override
    public synchronized void deleteProduct(Long productId) {
        productRepository.delete(productId);
    }

    @Override
    public synchronized Product getProductById(Long productId) {
        return productRepository.getById(productId);
    }

    @Override
    public synchronized boolean addProducts(Long productId, int quantityToAdd) {
        try {
            Product product = getProductById(productId);
            Utils.validateNumber(quantityToAdd);
            product.setQuantity(product.getQuantity() + quantityToAdd);
            productRepository.update(product);
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized boolean takeProducts(Long productId, int quantityToRemove) {
        try {
            Product product = getProductById(productId);
            Utils.validateNumber(quantityToRemove);
            if (product.getQuantity() < quantityToRemove) {
                return false;
            }
            product.setQuantity(product.getQuantity() - quantityToRemove);
            productRepository.update(product);
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
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
    public synchronized boolean setProductPrice(Long productId, double price) {
        try {
            Product product = getProductById(productId);
            Utils.validateNumber(price);
            product.setPrice(price);
            productRepository.update(product);
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized void deleteAllProducts() {
        productRepository.deleteAll();
    }

    private void validateNameIsUnique(String name) {
        if (productRepository.existsWithName(name)) {
            throw new NameNotUniqueException();
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
