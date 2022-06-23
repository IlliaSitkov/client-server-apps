import exceptions.CipherException;
import message.Message;
import model.Group;
import model.Product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import packet.Packet;
import packet.PacketEncryptor;
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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Base64;
import java.util.List;
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
    public void createEqualGroupsVarious_whenManyThreads_thenOnlyOneCreated() throws InterruptedException {

        int nThreads = 5;
        int expectedNameLength = "My Group".length();

        List<String> names = List.of("  MY   GrOuP   ", "my    GROUP   ", "  mY GROup    ");

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (String name: names) {
            executorService.execute(() -> {
                try {
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

        List<Group> groups = groupService.getAllGroups();
        Assertions.assertEquals(1, groups.size());
        Assertions.assertEquals(expectedNameLength, groups.get(0).getName().length());

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
    public void createEqualProductsVarious_whenManyThreads_thenOnlyOneCreated() throws InterruptedException {
        Group g = groupService.createGroup("Group", "New Group");

        int nThreads = 5;

        String description = "Description";
        String producer = "Producer";
        int quantity = 123;
        double price = 343;

        int expectedNameLength = "My Group".length();

        List<String> names = List.of("  MY   GrOuP   ", "my    GROUP   ", "  mY GROup    ");

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (String name: names) {
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

        List<Product> products = productRepository.getAll();
        Assertions.assertEquals(1, products.size());
        Assertions.assertEquals(expectedNameLength, products.get(0).getName().length());
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
    public void setPrice_whenIncorrectPriceAndManyThreads_thenOldPriceRemains() throws InterruptedException {

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
    
    
//--------------------------------------------------------------------------------------------
// Взнати кількість товару на складі
// Списати певну кількість товару
// Зарахувати певну кількість товару  
   
    
    @Test
    public void getProductQuantity_whenOneThread_thenQuantityCorrect() throws CipherException {
    	ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    	System.setOut(new PrintStream(outContent));
    	
    	int quantity = 123;
    	Group g = groupService.createGroup("Group", "New Group");
    	Product p1 = this.productService.createProduct("product1", "descr1", "producer1", quantity, 100.5, g.getId());
    	Message message = new Message(Commands.PRODUCT_GET_QUANTITY.ordinal(), 123);
    	message.putValue(JSONStrings.PRODUCT_ID, p1.getId());
    	Packet packet = new Packet((byte)20, 555L, message);
    	this.receiver.receiveMessage(PacketEncryptor.encryptPacket(packet));
    	Utils.sleep(500);
    	
    	String raw = outContent.toString();
    	String stringPacket = raw.substring(18, raw.length() - 2);
    	byte[] arr = Base64.getDecoder().decode(stringPacket);
    	Packet decrypted = PacketEncryptor.decryptPacket(arr).get(0);
    	System.setOut(System.out);
    	Assertions.assertEquals(quantity, decrypted.getBMsg().getMessageJSON().getInt(JSONStrings.RESULT));
    }
    
    @Test
    public void getProductQuantity_whenAnotherThreadAddsQuantity_thenQuantityCorrect() throws InterruptedException, CipherException {
    	ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    	System.setOut(new PrintStream(outContent));
    	
    	int quantity = 100;
    	Group g = groupService.createGroup("Group", "New Group");
    	Product p1 = this.productService.createProduct("product1", "descr1", "producer1", quantity, 100.5, g.getId());
    	
    	Runnable run1 = () -> {
    		Message message = new Message(Commands.PRODUCT_ADD_QUANTITY.ordinal(), 222);
    		message.putValue(JSONStrings.PRODUCT_ID, p1.getId());
    		message.putValue(JSONStrings.QUANTITY_TO_ADD, quantity);
    		Packet pack = new Packet((byte)22, 555L, message);
    		try {
				this.receiver.receiveMessage(PacketEncryptor.encryptPacket(pack));
			} catch (CipherException e) {
				e.printStackTrace();
			}
    	};
    	
    	Runnable run2 = () -> {
    		Utils.sleep(5);
    		Message message = new Message(Commands.PRODUCT_GET_QUANTITY.ordinal(), 123);
        	message.putValue(JSONStrings.PRODUCT_ID, p1.getId());
        	Packet pack = new Packet((byte)20, 555L, message);
        	try {
				this.receiver.receiveMessage(PacketEncryptor.encryptPacket(pack));
			} catch (CipherException e) {
				e.printStackTrace();
			}
    	};
    	
    	ExecutorService exec = Executors.newFixedThreadPool(2);
    	
    	exec.execute(run1);
    	exec.execute(run2);
    	
    	exec.shutdown();
    	exec.awaitTermination(500, TimeUnit.MILLISECONDS);
    	
    	Utils.sleep(100);
    	
    	String raw = outContent.toString();
    	String[] arr = raw.split("\r\n");
    	String strPacket1 = arr[0].substring(18);
    	String strPacket2 = arr[1].substring(18);
    	Packet pack1 = PacketEncryptor.decryptPacket(Base64.getDecoder().decode(strPacket1)).get(0);
    	Packet pack2 = PacketEncryptor.decryptPacket(Base64.getDecoder().decode(strPacket2)).get(0);
    	Packet packRes = (pack1.getBMsg().getMessage().contains("OK")) ? pack2 : pack1;
    	System.setOut(System.out);
    	Assertions.assertEquals(2 * quantity, packRes.getBMsg().getMessageJSON().getInt(JSONStrings.RESULT));
    }
    
    @Test
    public void takeProductQuantity_whenManyThreads_thenResultingQuantityIsCorrect() throws InterruptedException {
    	int initialQuantity = 1200;
    	
    	Group g = groupService.createGroup("Group", "New Group");
    	Product p1 = this.productService.createProduct("product1", "descr1", "producer1", initialQuantity, 100.5, g.getId());
    	int threadNumber = 5;
    	int quantityToRemove = 200;
    	ExecutorService exec = Executors.newFixedThreadPool(threadNumber);
    	for(int i = 0; i < threadNumber; i++) {
    		exec.execute(() -> {
    			Message message = new Message(Commands.PRODUCT_TAKE_QUANTITY.ordinal(), 123);
            	message.putValue(JSONStrings.PRODUCT_ID, p1.getId());
            	message.putValue(JSONStrings.QUANTITY_TO_REMOVE, quantityToRemove);
            	Packet pack = new Packet((byte)20, 555L, message);
            	try {
					this.receiver.receiveMessage(PacketEncryptor.encryptPacket(pack));
				} catch (CipherException e) {
					e.printStackTrace();
				}
    		});
    	}
    	
    	exec.shutdown();
    	exec.awaitTermination(500, TimeUnit.MILLISECONDS);
    	Utils.sleep(100);
    	int resultingQuantity = this.productRepository.getById(p1.getId()).getQuantity();
    	Assertions.assertEquals(initialQuantity - quantityToRemove * threadNumber, resultingQuantity);
    }
    
    @Test
    public void addProductQuantity_whenManyThreads_thenResultingQuantityIsCorrect() throws InterruptedException {
    	int initialQuantity = 0;
    	Group g = groupService.createGroup("Group", "New Group");
    	Product p1 = this.productService.createProduct("product1", "descr1", "producer1", initialQuantity, 100.5, g.getId());
    	int threadNumber = 5;
    	int quantityToAdd = 200;
    	ExecutorService exec = Executors.newFixedThreadPool(threadNumber);
    	for(int i = 0; i < threadNumber; i++) {
    		exec.execute(() -> {
    			Message message = new Message(Commands.PRODUCT_ADD_QUANTITY.ordinal(), 123);
            	message.putValue(JSONStrings.PRODUCT_ID, p1.getId());
            	message.putValue(JSONStrings.QUANTITY_TO_ADD, quantityToAdd);
            	Packet pack = new Packet((byte)20, 555L, message);
            	try {
					this.receiver.receiveMessage(PacketEncryptor.encryptPacket(pack));
				} catch (CipherException e) {
					e.printStackTrace();
				}
    		});
    	}
    	exec.shutdown();
    	exec.awaitTermination(500, TimeUnit.MILLISECONDS);
    	Utils.sleep(100);
    	int resultingQuantity = this.productRepository.getById(p1.getId()).getQuantity();
    	Assertions.assertEquals(initialQuantity + quantityToAdd * threadNumber, resultingQuantity);
    }
    
    @Test
    public void addAndTakeProductQunatity_whenTwoConcurrentThreads_thenResultingQuantityUnchanged() throws InterruptedException {
    	int initialQuantity = 100;
    	int quantityDiff = 50;
    	Group g = groupService.createGroup("Group", "New Group");
    	Product p1 = this.productService.createProduct("product1", "descr1", "producer1", initialQuantity, 100.5, g.getId());
    	Runnable run1 = () -> {
    		Message message = new Message(Commands.PRODUCT_ADD_QUANTITY.ordinal(), 222);
    		message.putValue(JSONStrings.PRODUCT_ID, p1.getId());
    		message.putValue(JSONStrings.QUANTITY_TO_ADD, quantityDiff);
    		Packet pack = new Packet((byte)22, 555L, message);
    		try {
				this.receiver.receiveMessage(PacketEncryptor.encryptPacket(pack));
			} catch (CipherException e) {
				e.printStackTrace();
			}
    	};
    	
    	Runnable run2 = () -> {
    		Message message = new Message(Commands.PRODUCT_TAKE_QUANTITY.ordinal(), 222);
    		message.putValue(JSONStrings.PRODUCT_ID, p1.getId());
    		message.putValue(JSONStrings.QUANTITY_TO_REMOVE, quantityDiff);
    		Packet pack = new Packet((byte)22, 555L, message);
    		try {
				this.receiver.receiveMessage(PacketEncryptor.encryptPacket(pack));
			} catch (CipherException e) {
				e.printStackTrace();
			}
    	};
    	ExecutorService exec = Executors.newFixedThreadPool(2);
    	exec.execute(run1);
    	exec.execute(run2);
    	exec.shutdown();
    	exec.awaitTermination(500, TimeUnit.MILLISECONDS);
    	Utils.sleep(100);
    	Assertions.assertEquals(initialQuantity, this.productRepository.getById(p1.getId()).getQuantity());
    }
    
    @Test
    public void takeProductQuantity_whenManyThreads_thenQuantityIsNotNegative() throws InterruptedException {
    	int initialQuantity = 300;
    	int threadNumber = 5;
    	int quantityDiff = 100;
    	Group g = groupService.createGroup("Group", "New Group");
    	Product p1 = this.productService.createProduct("product1", "descr1", "producer1", initialQuantity, 100.5, g.getId());
    	ExecutorService exec = Executors.newFixedThreadPool(threadNumber);
    	for(int i = 0; i < threadNumber; i++) {
    		exec.execute(() -> {
    			Message message = new Message(Commands.PRODUCT_TAKE_QUANTITY.ordinal(), 123);
            	message.putValue(JSONStrings.PRODUCT_ID, p1.getId());
            	message.putValue(JSONStrings.QUANTITY_TO_REMOVE, quantityDiff);
            	Packet pack = new Packet((byte)20, 555L, message);
            	try {
					this.receiver.receiveMessage(PacketEncryptor.encryptPacket(pack));
				} catch (CipherException e) {
					e.printStackTrace();
				}
    		});
    	}
    	
    	exec.shutdown();
    	exec.awaitTermination(500, TimeUnit.MILLISECONDS);
    	Utils.sleep(100);
    	Assertions.assertEquals(0, this.productRepository.getById(p1.getId()).getQuantity());
    }
    
    
   

}
