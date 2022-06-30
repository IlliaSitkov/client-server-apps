package server;

import java.net.InetAddress;

public interface StoreServer {


    void stop() throws InterruptedException;

    void receive();

    void send(byte[] bytes, InetAddress inetAddress, int port);


    void send(byte[] bytes);


}
