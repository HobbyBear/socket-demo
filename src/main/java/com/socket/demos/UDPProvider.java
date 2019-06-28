package com.socket.demos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author: xch
 * @create: 2019-06-28 16:30
 **/
public class UDPProvider {

    public static void main(String[] args) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(20000);
        //接收消息
        byte[] buf = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(buf,buf.length);
        datagramSocket.receive(receivePacket);
        System.out.println("client host:"+
                receivePacket.getAddress().getHostAddress()+"\tdata:"+
                new String(receivePacket.getData(),0,receivePacket.getLength()));
        //发送消息
        byte[] sendData = "我是空调".getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(sendData,sendData.length);
        datagramPacket.setAddress(receivePacket.getAddress());
        datagramPacket.setPort(receivePacket.getPort());
        datagramSocket.send(datagramPacket);

    }
}
