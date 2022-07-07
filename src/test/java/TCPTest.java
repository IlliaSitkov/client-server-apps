import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import message.Message;
import packet.Packet;
import processing.Mediator;
import server.tcp.StoreClientTCP;
import utils.Utils;

public class TCPTest {
	
	private static Mediator mediator;
	
	
	@BeforeAll
	public static void startServer() throws IOException {
		mediator = Mediator.getInstance();
		mediator.startTCPServer();
	}
	
	@Test
	public void sendPackets_whenSeveralClients_thenAllProcessed() throws InterruptedException {
		int threadNumber = 5;
		int packetsForEach = 5;
		int totalNumb = threadNumber * packetsForEach;
		AtomicInteger count = new AtomicInteger();
		ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
		
		for(int i = 0; i < totalNumb; i++) {
			executorService.execute(() -> {
				try {
					StoreClientTCP client = new StoreClientTCP();
					Packet res = client.fetch(new Packet((byte)132, Utils.generateId(), new Message(8,8,"{\"result\":\"Foo\"}")));
					if ("OK".equals(res.getBMsg().getValue("result"))) 
	                    count.getAndIncrement();
					client.close();
				} catch (IOException e) {
					throw new RuntimeException();
				}
			});
		}
		executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
        Utils.sleep(1000);
        Assertions.assertEquals(totalNumb, count.get());
	}
	
	@Test
	public void stopServer_whenClientTriesToFetch_thenConnectionRefused() throws InterruptedException {
		try {
			StoreClientTCP client = new StoreClientTCP();
			byte[] arr = new byte[1];
			arr[0] = 101;
			client.send(arr);
			mediator.stopTCPServer();
			Utils.sleep(100);
			Assertions.assertThrows(RuntimeException.class, () -> {
				client.fetch(new Packet((byte)132, Utils.generateId(), new Message(8,8,"{\"result\":\"Foo\"}")));
			});
//			server = new StoreServerTCP();
//			server.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@AfterAll
    public static void stopServer() throws IOException, InterruptedException {
        mediator.terminateAll();
    }
	
}
