package server.tcp;

import java.io.IOException;

import processing.Mediator;
import utils.Utils;

public class MainServerTCP {

	public static void main(String[] args) throws IOException, InterruptedException {
		Mediator mediator = Mediator.getInstance();
		mediator.startTCPServer();
			
		Utils.sleep(10000);
		
		mediator.terminateAll();
	}

}
