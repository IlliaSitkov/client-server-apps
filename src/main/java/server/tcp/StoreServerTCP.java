package server.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import exceptions.ServerAlreadyStoppedException;

public class StoreServerTCP {

	public static final int DEFAULT_SERVER_TCP_PORT = 8082;
	
	public static final InetAddress DEFAULT_SERVER_ADDRESS;
	
	public static final int RECEIVE_TIMEOUT = 3000;
	
	private static final int USER_THREADS_NUMBER = 5;
	
	private ServerSocket serverSocket;
	
	private Thread executionThread;
	
	private ExecutorService clientService;
	
	private volatile boolean isRunning;
	
	static {
        try {
            DEFAULT_SERVER_ADDRESS = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
	
	public StoreServerTCP() throws IOException {
		this.serverSocket = new ServerSocket(DEFAULT_SERVER_TCP_PORT);
		this.serverSocket.setSoTimeout(RECEIVE_TIMEOUT);
		this.clientService = Executors.newFixedThreadPool(USER_THREADS_NUMBER);
		this.isRunning = false;
	}
	
	public synchronized void start() {
		if (this.serverSocket.isClosed()) 
            throw new ServerAlreadyStoppedException();
		if(this.isRunning)
			return;
		this.isRunning = true;
		System.out.println("Starting TCP server...");
		this.executionThread = new Thread(() -> {
			while(this.isRunning) {
				try {
					this.clientService.execute(new TCPClientHandler(this.serverSocket.accept()));
				} catch(SocketTimeoutException e) {
					System.out.println(e.getMessage());
					continue;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.executionThread.start();
	}
	
	public synchronized void stop() throws IOException, InterruptedException {
		if(!this.isRunning)
			return;
		this.isRunning = false;
		this.executionThread.join();	
		this.clientService.shutdown();
        this.clientService.awaitTermination(10, TimeUnit.DAYS);
		this.serverSocket.close();
		System.out.println("TCP server stopped...");
	}
}
