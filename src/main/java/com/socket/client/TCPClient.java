package com.socket.client;

import cn.hutool.core.util.StrUtil;
import com.socket.client.bean.ServerInfo;
import com.socket.utils.CloseUtil;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @author: xch
 * @create: 2019-07-02 16:13
 **/
public class TCPClient {

    public void linkwith(ServerInfo info) throws IOException {
        Socket socket = new Socket();
        // 超时时间
        socket.setSoTimeout(3000);

        // 超时时间3000ms
        socket.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()), 3000);

        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());
        ReadHandler readHandler = new ReadHandler(socket.getInputStream());
        readHandler.start();
        write(socket);
        readHandler.exit();
        socket.close();
    }

    private static void write(Socket client) throws IOException {
        // 构建键盘输入流
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // 得到Socket输出流，并转换为打印流
        PrintStream socketPrintStream = new PrintStream(client.getOutputStream());

        do {
            // 键盘读取一行
            String str = input.readLine();
            // 发送到服务器
            socketPrintStream.println(str);

            if ("00bye00".equalsIgnoreCase(str)) {
                break;
            }
        } while (true);

        // 资源释放
        socketPrintStream.close();
    }

    class ReadHandler extends Thread {
        private BufferedReader reader;

        private boolean done;

        private ReadHandler(InputStream inputStream) {
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            super.run();
            try {
                while (!done) {
                    String str = null;
                    try {
                        str = reader.readLine();
                        System.out.println(str);
                    } catch (SocketTimeoutException e) {
                        //连接超时则继续循环知道读出数据
                        continue;
                    }
                    if (StrUtil.isEmpty(str)){
                        System.out.println("服务器端没有数据可读,,已关闭");
                        done = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("连接异常断开" + e.getMessage());
            } finally {
                CloseUtil.closeAll(reader);
            }
        }

        public void exit() {
            this.done = true;
            CloseUtil.closeAll(reader);
        }
    }
}
