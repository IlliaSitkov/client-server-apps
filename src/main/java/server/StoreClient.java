package server;

import packet.Packet;

import java.io.IOException;

public interface StoreClient {

    void close();

    void send(byte[] bytes) throws IOException;

    Packet receive() throws IOException;

    Packet fetch(Packet packet);



}
