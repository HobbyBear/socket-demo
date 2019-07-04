package com.socket.server;

import cn.hutool.json.JSONUtil;
import com.socket.client.bean.ServerInfo;
import com.socket.constants.Constants;
import com.socket.utils.MsgCreaterUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Optional;

class UDPProvider extends Thread {
    /**
     * 是否结束标志
     */
    private boolean running;

    DatagramSocket datagramSocket;
    /**
     * 暗号唯一标志
     */
    private String sn;


    public UDPProvider(int port, String sn) throws SocketException {
        super();
        this.running = true;
        this.datagramSocket = new DatagramSocket(port);
        this.sn = sn;
    }

    public void run() {
        try {
            while (running) {
                /*----------------------监听消息---------------------------*/
                byte[] buf = new byte[512];
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(receivePacket);
                //客户端监听端口
                int searchListeningPort = MsgCreaterUtil.parsePort(
                        new String(receivePacket.getData(), 0, receivePacket.getLength()));

                /*----------------发送消息------------------------------*/
                ServerInfo serverInfo = ServerInfo
                        .builder()
                        .sn(this.sn)
                        .port(Constants.TCP.PORT_SERVER)
                        .address(datagramSocket.getLocalAddress().getHostAddress())
                        .build();
                //回送消息体
                byte[] sendData = MsgCreaterUtil.buildServerInfoStr(JSONUtil.toJsonStr(serverInfo)).getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length);
                datagramPacket.setAddress(receivePacket.getAddress());
                datagramPacket.setPort(searchListeningPort);
                datagramSocket.send(datagramPacket);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            Optional.ofNullable(datagramSocket).ifPresent(DatagramSocket::close);
            System.out.println("服务：" + this.sn + "结束");
        }
    }

    /**
     * 关闭服务
     */
    public void close() {
        this.running = false;
        Optional.ofNullable(datagramSocket).ifPresent(DatagramSocket::close);
    }
}