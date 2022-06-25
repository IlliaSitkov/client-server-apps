package server.udp;

import message.Message;
import packet.Packet;

import java.io.IOException;

public class MainClient {




    public static void main(String[] args) throws IOException, InterruptedException {

        StoreClientUDP udpClient = new StoreClientUDP();
        Packet p = udpClient.fetch(new Packet((byte)132, 123, new Message(8,8,"{\"result\":\"Foo\"}")));
        udpClient.fetch(new Packet((byte)132, 123, new Message(8,8,"{\"result\":\"Foo\"}")));
        udpClient.fetch(new Packet((byte)132, 123, new Message(8,8,"{\"result\":\"Foo\"}")));
        udpClient.fetch(new Packet((byte)132, 123, new Message(8,8,"{\"result\":\"Foo\"}")));
        udpClient.fetch(new Packet((byte)132, 123, new Message(8,8,"{\"result\":\"Foo\"}")));
        udpClient.fetch(new Packet((byte)132, 123, new Message(8,8,"{\"result\":\"Foo\"}")));
        udpClient.fetch(new Packet((byte)132, 123, new Message(8,8,"{\"result\":\"Foo\"}")));


        StoreClientUDP udpClient1 = new StoreClientUDP();
        Packet p1 = udpClient1.fetch(new Packet((byte)132, 129, new Message(8,8,"{\"result\":\"Foo\"}")));


        udpClient.close();
        udpClient1.close();
    }


}
