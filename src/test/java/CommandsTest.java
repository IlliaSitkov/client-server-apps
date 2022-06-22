import exceptions.CipherException;
import message.Message;
import model.Group;
import model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import packet.Packet;
import packet.PacketEncryptor;
import processing.Mediator;
import processing.Receiver;
import processing.ReceiverFakeImpl;
import repository.group.GroupRepository;
import repository.group.GroupRepositoryImpl;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import service.group.GroupService;
import service.group.GroupServiceImpl;
import service.product.ProductService;
import service.product.ProductServiceImpl;
import utils.Commands;
import utils.JSONStrings;
import utils.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandsTest {

    //Додати групу товарів
    //Додати назву товару до групи
    //Встановити ціну на конкретний товар

    private final Receiver receiver = ReceiverFakeImpl.getInstance();
    private final GroupService groupService = GroupServiceImpl.getInstance();

    private final GroupRepository groupRepository = GroupRepositoryImpl.getInstance();

    private final ProductService productService = ProductServiceImpl.getInstance();

    private final ProductRepository productRepository = ProductRepositoryImpl.getInstance();

    private final Mediator mediator = Mediator.getInstance();


    @AfterEach
    public void removeAll() {
        groupService.deleteAllGroups();
    }

    @Test
    public void createGroup_whenOneThread_thenShouldExist() throws CipherException {
        String name = "My group";
        String description = "Description";
        Message message = new Message(Commands.GROUP_CREATE.ordinal(), 123);
        message.putValue(JSONStrings.NAME,name);
        message.putValue(JSONStrings.DESCRIPTION,description);
        Packet p = new Packet((byte)12, 678L, message);
        receiver.receiveMessage(PacketEncryptor.encryptPacket(p));
//        mediator.terminateAll(); // impossible to use as executor service can not be started again and thus other tests will fail
        Utils.sleep(1000); // wait some time for threads to finish their work

        Assertions.assertTrue(groupRepository.existsWithName(name));
    }



    @Test
    public void createEqualGroups_whenManyThreads_thenOnlyOneCreated() throws InterruptedException {

        int times = 20;
        int nThreads = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                try {
                    String name = "My group";
                    String description = "Description";
                    Message message = new Message(Commands.GROUP_CREATE.ordinal(), 123);
                    message.putValue(JSONStrings.NAME,name);
                    message.putValue(JSONStrings.DESCRIPTION,description);
                    Packet p = new Packet((byte)12, 678L, message);
                    receiver.receiveMessage(PacketEncryptor.encryptPacket(p));
                } catch (RuntimeException e) {

                } catch (CipherException e) {

                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        Utils.sleep(1500);

        Assertions.assertEquals(1, groupService.getAllGroups().size());
    }


    @Test
    public void createGroups_whenManyThreads_thenAllSaved() throws InterruptedException {

        int quant = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < quant; i++) {
            int finalI = i;
            executorService.execute(() -> {

                String name = "My group "+ finalI;
                String description = "Description";
                Message message = new Message(Commands.GROUP_CREATE.ordinal(), 123);
                message.putValue(JSONStrings.NAME,name);
                message.putValue(JSONStrings.DESCRIPTION,description);
                Packet p = new Packet((byte)12, 678L, message);
                try {
                    receiver.receiveMessage(PacketEncryptor.encryptPacket(p));
                } catch (CipherException e) {
                    throw new RuntimeException(e);
                }

            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        Utils.sleep(1500);

        Assertions.assertEquals(quant, groupService.getAllGroups().size());
    }

//////////////////////////////////////////////////////////////////////////////////////


    @Test
    public void createProduct_whenOneThread_thenShouldExist() throws CipherException {

        Group g = groupService.createGroup("Group", "New Group");

        String name = "Product";
        String description = "Description";
        String producer = "Producer";
        int quantity = 123;
        double price = 343;

        Message message = new Message(Commands.PRODUCT_CREATE.ordinal(), 123);
        message.putValue(JSONStrings.NAME,name);
        message.putValue(JSONStrings.DESCRIPTION,description);
        message.putValue(JSONStrings.PRODUCER, producer);
        message.putValue(JSONStrings.QUANTITY, quantity);
        message.putValue(JSONStrings.PRICE, price);
        message.putValue(JSONStrings.GROUP_ID, g.getId());

        Packet p = new Packet((byte)12, 678L, message);
        receiver.receiveMessage(PacketEncryptor.encryptPacket(p));

        Utils.sleep(1000);

        Assertions.assertTrue(productRepository.existsWithName(name));
    }


    @Test
    public void createEqualProducts_whenManyThreads_thenOnlyOneCreated() throws InterruptedException {
        Group g = groupService.createGroup("Group", "New Group");

        int times = 1000;
        int nThreads = 5;

        String name = "Product";
        String description = "Description";
        String producer = "Producer";
        int quantity = 123;
        double price = 343;

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                try {
                    Message message = new Message(Commands.PRODUCT_CREATE.ordinal(), 123);
                    message.putValue(JSONStrings.NAME,name);
                    message.putValue(JSONStrings.DESCRIPTION,description);
                    message.putValue(JSONStrings.PRODUCER, producer);
                    message.putValue(JSONStrings.QUANTITY, quantity);
                    message.putValue(JSONStrings.PRICE, price);
                    message.putValue(JSONStrings.GROUP_ID, g.getId());

                    Packet p = new Packet((byte)12, 678L, message);
                    receiver.receiveMessage(PacketEncryptor.encryptPacket(p));
                } catch (RuntimeException | CipherException ignored) {

                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

        Utils.sleep(1000);

        Assertions.assertEquals(1, productRepository.getAll().size());
    }


    @Test
    public void createDifferentProducts_whenManyThreads_thenAllCreated() throws InterruptedException {
        Group g = groupService.createGroup("Group", "New Group");

        int times = 1000;
        int nThreads = 5;

        String name = "Product";
        String description = "Description";
        String producer = "Producer";
        int quantity = 123;
        double price = 343;

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    Message message = new Message(Commands.PRODUCT_CREATE.ordinal(), 123);
                    message.putValue(JSONStrings.NAME,name + finalI);
                    message.putValue(JSONStrings.DESCRIPTION,description);
                    message.putValue(JSONStrings.PRODUCER, producer);
                    message.putValue(JSONStrings.QUANTITY, quantity);
                    message.putValue(JSONStrings.PRICE, price);
                    message.putValue(JSONStrings.GROUP_ID, g.getId());

                    Packet p = new Packet((byte)12, 678L, message);
                    receiver.receiveMessage(PacketEncryptor.encryptPacket(p));
                } catch (RuntimeException | CipherException ignored) {

                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

        Utils.sleep(1000);

        Assertions.assertEquals(times, productRepository.getAll().size());
    }


//////////////////////////////////////////////////////////////////////////////////


    // - set correct - check new present
    // - set incorrect - check old present
    // - set multiple - old not present


    @Test
    public void setPrice_whenCorrectPrice_thenNewPriceSaved() throws CipherException {

        double oldPrice = 456.7;
        double newPrice = 234.5;

        Group g = groupService.createGroup("Group", "New Group");
        Product product = productService.createProduct("Name","desc","prod",233,oldPrice,g.getId());

        Message message = new Message(Commands.PRODUCT_SET_PRICE.ordinal(), 123);
        message.putValue(JSONStrings.PRODUCT_ID,product.getId());
        message.putValue(JSONStrings.PRICE,newPrice);

        Packet p = new Packet((byte)12, 678L, message);
        receiver.receiveMessage(PacketEncryptor.encryptPacket(p));

        Utils.sleep(1000);

        Assertions.assertEquals(newPrice,productService.getProductById(product.getId()).getPrice());
    }



    @Test
    public void setPrice_whenInorrectPriceAndManyThreads_thenOldPriceRemains() throws InterruptedException {

        int times = 30;
        int nThreads = 5;

        double oldPrice = 456.7;
        double incorrectPrice = -234.5;

        Group g = groupService.createGroup("Group", "New Group");
        Product product = productService.createProduct("Name", "desc", "prod", 233, oldPrice, g.getId());

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                Message message = new Message(Commands.PRODUCT_SET_PRICE.ordinal(), 123);
                message.putValue(JSONStrings.PRODUCT_ID, product.getId());
                message.putValue(JSONStrings.PRICE, incorrectPrice);

                Packet p = new Packet((byte) 12, 678L, message);
                try {
                    receiver.receiveMessage(PacketEncryptor.encryptPacket(p));
                } catch (CipherException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

        Utils.sleep(1000);

        Assertions.assertEquals(oldPrice,productService.getProductById(product.getId()).getPrice());
    }



    @Test
    public void setPrice_whenRandomCorrectPriceAndManyThreads_thenOldPriceNotPresent() throws CipherException, InterruptedException {

        int times = 30;
        int nThreads = 5;

        double oldPrice = 3.4;

        Group g = groupService.createGroup("Group", "New Group");
        Product product = productService.createProduct("Name", "desc", "prod", 233, oldPrice, g.getId());

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            int finalI = i;
            executorService.execute(() -> {
                Message message = new Message(Commands.PRODUCT_SET_PRICE.ordinal(), 123);
                message.putValue(JSONStrings.PRODUCT_ID, product.getId());
                message.putValue(JSONStrings.PRICE, finalI *100+1);

                Packet p = new Packet((byte) 12, 678L, message);
                try {
                    receiver.receiveMessage(PacketEncryptor.encryptPacket(p));
                } catch (CipherException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

        Utils.sleep(1000);

        Assertions.assertNotEquals(oldPrice,productService.getProductById(product.getId()).getPrice());
    }








}
