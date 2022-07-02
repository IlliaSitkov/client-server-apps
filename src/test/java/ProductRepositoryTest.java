import exceptions.SQLExceptionRuntime;
import model.Group;
import model.Product;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import repository.group.GroupRepository;
import repository.group.GroupRepositoryImpl;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import utils.FilterCriteria;
import utils.DBUtils;
import utils.SortCriteria;
import utils.SortOrder;

import static org.assertj.core.api.Assertions.*;


import java.util.*;

public class ProductRepositoryTest {


    private final ProductRepository productRepository = ProductRepositoryImpl.getInstance(DBUtils.TEST_DB);
    private final GroupRepository groupRepository = GroupRepositoryImpl.getInstance(DBUtils.TEST_DB);

    @BeforeClass
    public static void setEnv() {
        System.setProperty("ENV", "DEV");
    }
    @Before
    public void clearDB() {
        groupRepository.deleteAll();
    }

    @Test
    public void createProducts_whenCorrectProducts_thenAllSaved() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        int times = 100;
        for (int i = 0; i < times; i++) {
            Product p = new Product("P"+i, "D1", "Pr1", 21, 34,1L);
            productRepository.save(p);
        }
        Assertions.assertEquals(times, productRepository.getAll().size());
    }

    @Test
    public void createProducts_whenNotUniqueNames_thenOneSaved() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        int times = 100;
        for (int i = 0; i < times; i++) {
            try {
                Product p = new Product("Product", "D1", "Pr1", 21, 34, 1L);
                productRepository.save(p);
            } catch (SQLExceptionRuntime ignored) {

            }
        }
        Assertions.assertEquals(1, productRepository.getAll().size());
    }


    @Test
    public void createProducts_whenNotUniqueId_thenOneSaved() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        int times = 100;
        for (int i = 0; i < times; i++) {
            try {
                Product p = new Product(10L,"Product"+i, "D1", "Pr1", 21, 34, 1L);
                productRepository.save(p);
            } catch (SQLExceptionRuntime ignored) {

            }
        }
        Assertions.assertEquals(1, productRepository.getAll().size());
    }

    @Test
    public void createProduct_whenIncorrectPrice_thenNotSaved() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        try {
            Product p = new Product(10L,"Product", "D1", "Pr1", 21, -34, 1L);
            productRepository.save(p);
        } catch (SQLExceptionRuntime ignored) {

        }
        Assertions.assertEquals(0, productRepository.getAll().size());
    }

    @Test
    public void createProduct_whenIncorrectQuantity_thenNotSaved() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        try {
            Product p = new Product(10L,"Product", "D1", "Pr1", -21, 34, 1L);
            productRepository.save(p);
        } catch (SQLExceptionRuntime ignored) {

        }
        Assertions.assertEquals(0, productRepository.getAll().size());
    }

    @Test
    public void createProduct_whenIncorrectGroupId_thenNotSaved() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        try {
            Product p = new Product(10L,"Product", "D1", "Pr1", 21, 34, 10L);
            productRepository.save(p);
        } catch (SQLExceptionRuntime ignored) {

        }
        Assertions.assertEquals(0, productRepository.getAll().size());
    }

    @Test
    public void createProduct_whenEmptyName_thenNotSaved() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        try {
            Product p = new Product(10L,"", "D1", "Pr1", 21, 34, 1L);
            productRepository.save(p);
        } catch (SQLExceptionRuntime ignored) {
        }
        Assertions.assertEquals(0, productRepository.getAll().size());
    }



    @Test
    public void findByCriteria_whenQuantityFilter_thenAllRelevantReturned() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        int times = 100;
        for (int i = 0; i < times; i++) {
            Product p = new Product("P"+i, "D1", "Pr1", i, 23,1L);
            productRepository.save(p);
        }

        Assertions.assertEquals(times, productRepository.findByCriteria(Map.of(FilterCriteria.MIN_QUANTITY, 0, FilterCriteria.MAX_QUANTITY, times)).size());
        Assertions.assertEquals(times/2, productRepository.findByCriteria(Map.of(FilterCriteria.MIN_QUANTITY, times/2, FilterCriteria.MAX_QUANTITY, times)).size());
    }

    @Test
    public void findByCriteria_whenSearchString_thenAllRelevantReturned() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        Product p = new Product("Product", "D1", "Pr1", 34, 23,1L);
        Product p1 = new Product("My Product", "D1", "Pr1", 46, 23,1L);
        Product p2 = new Product("Banana", "Fruit", "producer", 34, 23,1L);
        Product p3 = new Product("Apple", "Fruit", "Company", 34, 23,1L);

        productRepository.save(p);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);

        Assertions.assertEquals(3, productRepository.findByCriteria(Map.of(FilterCriteria.SEARCH_STRING, "prod")).size());
    }


    @Test
    public void findByCriteria_whenPriceFilter_thenAllRelevantReturned() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        Product p = new Product("Product", "D1", "Pr1", 34, 23.1,1L);
        Product p1 = new Product("My Product", "D1", "Pr1", 46, 105.5,1L);
        Product p2 = new Product("Banana", "Fruit", "producer", 34, 23,1L);
        Product p3 = new Product("Apple", "Fruit", "Company", 34, 56,1L);
        Product p4 = new Product("Pineapple", "Fruit", "Company", 34, 105.7,1L);

        productRepository.save(p);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
        productRepository.save(p4);

        Assertions.assertEquals(3, productRepository.findByCriteria(Map.of(FilterCriteria.MIN_PRICE, 23.1, FilterCriteria.MAX_PRICE, 105.5)).size());
    }


    @Test
    public void findByCriteria_whenGroupIdFilter_thenAllRelevantReturned() {
        Group g = new Group(1L,"g1", "d1");
        Group g1 = new Group(2L,"g2", "d1");
        groupRepository.save(g);
        groupRepository.save(g1);
        Product p = new Product("Product", "D1", "Pr1", 34, 23.1,1L);
        Product p1 = new Product("My Product", "D1", "Pr1", 46, 105.5,1L);
        Product p2 = new Product("Banana", "Fruit", "producer", 34, 23,2L);
        Product p3 = new Product("Apple", "Fruit", "Company", 34, 56,1L);
        Product p4 = new Product("Pineapple", "Fruit", "Company", 34, 105.7,1L);

        productRepository.save(p);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
        productRepository.save(p4);

        productRepository.findByCriteria(Map.of(FilterCriteria.GROUP_ID, 1L)).forEach(System.out::println);

        Assertions.assertEquals(4, productRepository.findByCriteria(Map.of(FilterCriteria.GROUP_ID, 1L)).size());
    }

    @Test
    public void findByCriteria_whenIncorrectFilter_thenNoneReturned() {
        Group g = new Group(1L,"g1", "d1");
        Group g1 = new Group(2L,"g2", "d1");
        groupRepository.save(g);
        groupRepository.save(g1);
        Product p = new Product("Product", "D1", "Pr1", 34, 23.1,1L);
        Product p1 = new Product("My Product", "D1", "Pr1", 46, 105.5,1L);
        Product p2 = new Product("Banana", "Fruit", "producer", 34, 23,2L);
        Product p3 = new Product("Apple", "Fruit", "Company", 34, 56,1L);
        Product p4 = new Product("Pineapple", "Fruit", "Company", 34, 105.7,1L);

        productRepository.save(p);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
        productRepository.save(p4);

        Assertions.assertEquals(0, productRepository.findByCriteria(Map.of(FilterCriteria.MAX_QUANTITY, -10)).size());
        Assertions.assertEquals(0, productRepository.findByCriteria(Map.of(FilterCriteria.MAX_PRICE, -10)).size());
        Assertions.assertEquals(0, productRepository.findByCriteria(Map.of(FilterCriteria.GROUP_ID, -10L)).size());
    }



    @Test
    public void sortByCriteria_whenNameCriteria_thenProperlySorted() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        Product p = new Product("Product", "D1", "Pr1", 34, 23.1,1L);
        Product p1 = new Product("My Product", "D1", "Pr1", 46, 105.5,1L);
        Product p2 = new Product("Banana", "Fruit", "producer", 46, 23,1L);
        Product p3 = new Product("Apple", "Fruit", "Company", 33, 56,1L);
        Product p4 = new Product("Pineapple", "Fruit", "Company", 90, 105.7,1L);

        productRepository.save(p);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
        productRepository.save(p4);

        List<Product> listAsc = productRepository.listByCriteria(Map.of(SortCriteria.BY_NAME, SortOrder.ASCENDING));
        List<Product> listDesc = productRepository.listByCriteria(Map.of(SortCriteria.BY_NAME, SortOrder.DESCENDING));

        assertThat(listAsc).isSortedAccordingTo(Comparator.comparing(Product::getName));
        assertThat(listDesc).isSortedAccordingTo(Comparator.comparing(Product::getName).reversed());

    }


    @Test
    public void sortByCriteria_whenQuantityCriteria_thenProperlySorted() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        Product p = new Product("Product", "D1", "Pr1", 34, 23.1,1L);
        Product p1 = new Product("My Product", "D1", "Pr1", 46, 105.5,1L);
        Product p2 = new Product("Banana", "Fruit", "producer", 46, 23,1L);
        Product p3 = new Product("Apple", "Fruit", "Company", 33, 56,1L);
        Product p4 = new Product("Pineapple", "Fruit", "Company", 90, 105.7,1L);

        productRepository.save(p);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
        productRepository.save(p4);

        List<Product> listAsc = productRepository.listByCriteria(Map.of(SortCriteria.BY_QUANTITY, SortOrder.ASCENDING));
        List<Product> listDesc = productRepository.listByCriteria(Map.of(SortCriteria.BY_QUANTITY, SortOrder.DESCENDING));

        assertThat(listAsc).isSortedAccordingTo(Comparator.comparing(Product::getQuantity));
        assertThat(listDesc).isSortedAccordingTo(Comparator.comparing(Product::getQuantity).reversed());

    }

    @Test
    public void sortByCriteria_whenPriceCriteria_thenProperlySorted() {
        Group g = new Group(1L,"g1", "d1");
        groupRepository.save(g);
        Product p = new Product("Product", "D1", "Pr1", 34, 23.1,1L);
        Product p1 = new Product("My Product", "D1", "Pr1", 46, 105.5,1L);
        Product p2 = new Product("Banana", "Fruit", "producer", 46, 56,1L);
        Product p3 = new Product("Apple", "Fruit", "Company", 33, 56,1L);
        Product p4 = new Product("Pineapple", "Fruit", "Company", 90, 105.7,1L);

        productRepository.save(p);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
        productRepository.save(p4);

        List<Product> listAsc = productRepository.listByCriteria(Map.of(SortCriteria.BY_PRICE, SortOrder.ASCENDING));
        List<Product> listDesc = productRepository.listByCriteria(Map.of(SortCriteria.BY_PRICE, SortOrder.DESCENDING));

        assertThat(listAsc).isSortedAccordingTo(Comparator.comparing(Product::getPrice));
        assertThat(listDesc).isSortedAccordingTo(Comparator.comparing(Product::getPrice).reversed());

    }



}
