package com.socket.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.socket.client.bean.ServerInfo;
import com.socket.constants.Constants;
import com.socket.utils.CloseUtil;
import com.socket.utils.MsgCreaterUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: xch
 * @create: 2019-07-02 15:37
 **/
public class UDPSearcher {
    /**
     * 搜索服务器信息
     *
     * @param timeout 搜索的过期时间
     * @return
     * @throws SocketException
     */
    public static ServerInfo searchServer(long timeout, TimeUnit timeUnit) throws SocketException {
        CountDownLatch receiveLatch = new CountDownLatch(1);
        Listener listener = listen(receiveLatch);
        sendBrodcast();
        try {
            receiveLatch.await(timeout, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<ServerInfo> serverInfoList = listener.getServerInfoAndThenClose();
        if (CollUtil.isEmpty(serverInfoList)) {
            return null;
        }
        //如果局域网内有多个服务器返回其中一个服务器的信息
        return serverInfoList.get(0);

    }


    private static void sendBrodcast() {
        DatagramSocket datagramSocket = null;
        try {
            //发送消息
            datagramSocket = new DatagramSocket();
            byte[] sendData = MsgCreaterUtil.buildPort(Constants.UDP.CLIENT_LISTEN_PORT).getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length);
            //todo 广播地址
            datagramPacket.setAddress(InetAddress.getByName("255.255.255.255"));
            //服务器监听端口
            datagramPacket.setPort(Constants.UDP.SERVER_LISTEN_PORT);
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtil.closeAll(datagramSocket);
            System.out.println("发送广播已完成");
        }
    }

    /**
     * 监听服务器返回的信息
     */
    private static Listener listen(CountDownLatch receiveLatch) throws SocketException {
        CountDownLatch startLatch = new CountDownLatch(1);
        Listener listener = new Listener(Constants.UDP.CLIENT_LISTEN_PORT, startLatch, receiveLatch);
        listener.start();
        return listener;
    }

    private static class Listener extends Thread {

        private DatagramSocket datagramSocket;

        private boolean running;

        private List<ServerInfo> serverInfoList = Lists.newArrayList();

        private CountDownLatch startLatch;

        private CountDownLatch receiveLatch;

        public Listener(int port, CountDownLatch startLatch, CountDownLatch receiveLatch) throws SocketException {
            super();
            this.datagramSocket = new DatagramSocket(port);
            this.running = true;
            this.startLatch = startLatch;
            this.receiveLatch = receiveLatch;
        }

        @Override
        public void run() {
            startLatch.countDown();
            try {
                while (running) {
                    //接收消息
                    byte[] buf = new byte[512];
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                    datagramSocket.receive(receivePacket);
                    //接收到的sn
                    String serverInfoStr = MsgCreaterUtil.parseServerInfoStr(new String(receivePacket.getData(), 0, receivePacket.getLength()));
                    serverInfoList.add(JSONUtil.toBean(serverInfoStr, ServerInfo.class));
                    receiveLatch.countDown();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            } finally {
                CloseUtil.closeAll(datagramSocket);
            }

        }

        public List<ServerInfo> getServerInfoAndThenClose() {
            CloseUtil.closeAll(datagramSocket);
            running = false;
            return this.serverInfoList;
        }
    }

}
