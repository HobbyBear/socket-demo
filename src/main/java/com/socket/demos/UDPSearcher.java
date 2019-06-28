package com.socket.demos;

import java.io.IOException;
import java.net.*;

/**
 * @author: xch
 * @create: 2019-06-28 16:30
 **/
public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        //发送消息
        DatagramSocket datagramSocket = new DatagramSocket();
        byte[] sendData = "Hello word".getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(sendData,sendData.length);
        datagramPacket.setAddress(InetAddress.getLocalHost());
        datagramPacket.setPort(20000);
        datagramSocket.send(datagramPacket);
        //接收消息
        byte[] buf = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(buf,buf.length);
        datagramSocket.receive(receivePacket);
        System.out.println("client host:"+
                receivePacket.getAddress().getHostAddress()+
                "\tdata:"+new String(receivePacket.getData(),0,receivePacket.getLength()));
    }
}
