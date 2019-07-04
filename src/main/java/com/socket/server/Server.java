package com.socket.server;

import com.socket.constants.Constants;

import java.io.IOException;
import java.util.UUID;

/**
 * @author: xch
 * @create: 2019-06-28 16:30
 **/
public class Server {


    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer(Constants.TCP.PORT_SERVER);
        tcpServer.start();
        UDPProvider provider = new UDPProvider(Constants.UDP.SERVER_LISTEN_PORT,UUID.randomUUID().toString());
        provider.start();
        System.in.read();
        provider.close();
        tcpServer.stop();
    }

}
