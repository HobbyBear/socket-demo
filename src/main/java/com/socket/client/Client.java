package com.socket.client;

import com.socket.client.bean.ServerInfo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author: xch
 * @create: 2019-06-28 16:30
 **/
public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerInfo serverInfo = UDPSearcher.searchServer(10,TimeUnit.SECONDS);
        System.out.println(serverInfo);
        TCPClient tcpClient = new TCPClient();
        tcpClient.linkwith(serverInfo);
    }


}
