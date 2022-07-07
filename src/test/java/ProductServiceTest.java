import model.Group;
import model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.group.GroupService;
import service.group.GroupServiceImpl;
import service.product.ProductService;
import service.product.ProductServiceImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProductServiceTest {


    private final GroupService groupService = GroupServiceImpl.getInstance();
    private final ProductService productService = ProductServiceImpl.getInstance();

    @AfterEach
    public void removeAll() {
        groupService.deleteAllGroups();
        productService.deleteAllProducts();
    }

    @Test
    public void getProdQuantity_whenOneThread_thenShouldBeEqualToNumber() {
        Group g = groupService.createGroup("Group", "New Group");
        int quant = 213;
        Product p = productService.createProduct("Name","desc","prod",quant,43,g.getId());
        Assertions.assertEquals(quant,productService.getProductQuantity(p.getId()));
    }

    @Test
    public void getProdQuantity_whenAddFromFewThreads_thenShouldBeEqualToNumber() throws InterruptedException {
        Group g = groupService.createGroup("Group", "New Group");
        Product p = productService.createProduct("Name","desc","prod",233,43,g.getId());
        int times = 10;
        int nThreads = 5;
        int addQ = 10;
        int initial = p.getQuantity();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> productService.addProducts(p.getId(),addQ)
            );
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(initial+times*addQ,productService.getProductQuantity(p.getId()));
    }


    @Test
    public void getProdQuantity_whenTakeFromFewThreads_thenShouldBeEqualToNumber() throws InterruptedException {
        Group g = groupService.createGroup("Group", "New Group");
        Product p = productService.createProduct("Name","desc","prod",233,43,g.getId());
        int times = 10;
        int nThreads = 5;
        int takeQ = 10;
        int initial = p.getQuantity();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> productService.takeProducts(p.getId(),takeQ));
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(initial-times*takeQ,productService.getProductQuantity(p.getId()));
    }



    @Test
    public void getProdQuantity_whenAddTakeFromFewThreads_thenShouldBeEqualToNumber() throws InterruptedException {
        Group g = groupService.createGroup("Group", "New Group");
        Product p = productService.createProduct("Name","desc","prod",233,43,g.getId());
        int times = 20;
        int nThreads = 5;
        int takeQ = 10;
        int initial = p.getQuantity();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            int finalI = i;
            executorService.execute(() -> {
                if (finalI % 2 == 0) {
                    productService.takeProducts(p.getId(),takeQ);
                } else {
                    productService.addProducts(p.getId(),takeQ);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

        Assertions.assertEquals(initial,productService.getProductQuantity(p.getId()));
    }


    @Test
    public void createProduct_whenOneThread_thenShouldExist() {
        Group g = groupService.createGroup("Group", "New Group");
        Product p = productService.createProduct("Name","desc","prod",233,43,g.getId());

        Assertions.assertDoesNotThrow(() -> {
            productService.getProductById(p.getId());
        });
    }

    @Test
    public void createDeleteProduct_whenOneThread_thenShouldThrow() {
        Group g = groupService.createGroup("Group", "New Group");
        Product p = productService.createProduct("Name","desc","prod",233,43,g.getId());
        productService.deleteProduct(p.getId());
        Assertions.assertThrows(Exception.class, () -> productService.getProductById(p.getId()));
    }


    @Test
    public void createEqualProducts_whenFewThreads_thenOnlyOneCreated() throws InterruptedException {
        Group g = groupService.createGroup("Group", "New Group");

        int times = 20;
        int nThreads = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                try {
                    productService.createProduct("Name","desc","prod",233,43,g.getId());
                } catch (RuntimeException e) {

                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    public void updateProductCreateEqualProducts_whenFewThreads_thenOnlyTwoExist() throws InterruptedException {
        Group g = groupService.createGroup("Group", "New Group");
        Product p = productService.createProduct("Name","desc","prod",233,43,g.getId());

        int times = 20;
        int nThreads = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        executorService.execute(() -> productService.updateProduct(p.getId(),"New Name", "NewDesc", "prod", 12,g.getId()));
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                try {
                    productService.createProduct("Name","desc","prod",233,43,g.getId());
                } catch (RuntimeException e) {

                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(2, productService.getAllProducts().size());
    }


}
