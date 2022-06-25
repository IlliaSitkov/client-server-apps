import exceptions.PacketNoResponseException;
import message.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import packet.Packet;
import server.udp.StoreClientUDP;
import server.udp.StoreServerUDP;
import utils.Utils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UDPTest {

    private static StoreServerUDP server;


    @BeforeAll
    public static void startServer() throws IOException {
        server = new StoreServerUDP();
        server.receive();
    }


    @Test
    public void sendPackets_whenUniqueIds_thenAllProcessed() throws InterruptedException {

        int times = 300;
        int nThreads = 5;
        AtomicInteger count = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        for (int i = 0; i < times; i++) {
            int finalI = i;
            executorService.execute(() -> {
                StoreClientUDP udpClient;
                try {
                    udpClient = new StoreClientUDP(10000+ finalI);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Packet p = udpClient.fetch(new Packet((byte)132, Utils.generateId(), new Message(8,8,"{\"result\":\"Foo\"}")));

                if ("OK".equals(p.getBMsg().getValue("result"))) {
                    count.getAndIncrement();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        Utils.sleep(1000);

        Assertions.assertEquals(times, count.get());
    }




    @Test
    public void sendPackets_whenDuplicatesPresent_thenOnlyOneProcessed() throws InterruptedException {

        int times = 5;
        int nThreads = 5;
        AtomicInteger count = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        for (int i = 0; i < times; i++) {
            int finalI = i;
            executorService.execute(() -> {
                StoreClientUDP udpClient;
                try {
                    udpClient = new StoreClientUDP(9090+ finalI);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Packet p;
                try {
                    p = udpClient.fetch(new Packet((byte) 132, 345, new Message(8, 8, "{\"result\":\"Foo\"}")));
                    if ("OK".equals(p.getBMsg().getValue("result"))) {
                        count.getAndIncrement();
                    }
                } catch (PacketNoResponseException e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        Utils.sleep(1000);

        Assertions.assertEquals(1, count.get());
    }



    @AfterAll
    public static void stopServer() throws InterruptedException {
        server.stop();
    }



}
