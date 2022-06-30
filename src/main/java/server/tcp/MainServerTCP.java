package server.tcp;

import java.io.IOException;

import utils.Utils;

public class MainServerTCP {

	public static void main(String[] args) throws IOException, InterruptedException {
		StoreServerTCP server = new StoreServerTCP();
		server.start();
		
		Utils.sleep(10000);
		
		server.stop();
	}

}
