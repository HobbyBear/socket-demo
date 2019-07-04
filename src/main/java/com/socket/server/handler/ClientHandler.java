package com.socket.server.handler;

import cn.hutool.core.util.StrUtil;
import com.socket.utils.CloseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {

    private Socket client;

    private WriteHandler writeHandler;

    private ReadHandler readHandler;

    private ClientHandlerCallback clientHandlerCallback;

    public ClientHandler(Socket client, ClientHandlerCallback clientHandlerCallback) throws IOException {
        this.client = client;
        this.writeHandler = new WriteHandler(new PrintStream(client.getOutputStream()));
        this.readHandler = new ReadHandler();
        this.clientHandlerCallback = clientHandlerCallback;
    }

    public void readAndPrint(){
        this.readHandler.start();
    }


    public interface ClientHandlerCallback {
        // 收到消息时通知
        void onNewMessageArrived(ClientHandler handler, String msg);

        // 自身关闭通知
        void onSelfClosed(ClientHandler handler);

    }


    /**
     * 往客户端发送消息
     *
     * @param msg
     */
    public void send(String msg) {
        this.writeHandler.write(msg);
    }

    /**
     * 写处理器
     */
    private class WriteHandler {

        private boolean done;

        private PrintStream printStream;

        private ExecutorService executorService;

        public WriteHandler(PrintStream printStream) {
            this.done = false;
            this.printStream = printStream;
            this.executorService = Executors.newSingleThreadExecutor();
        }

        public void exit() {
            this.done = true;
            CloseUtil.closeAll(printStream);
            executorService.shutdownNow();
        }

        /**
         * 往客户端写入一条数据
         *
         * @param msg
         */
        public void write(String msg) {
            executorService.execute(new WriteRunnable(msg));
        }


        private class WriteRunnable implements Runnable {

            private String msg;


            public WriteRunnable(String msg) {
                this.msg = msg;
            }

            @Override
            public void run() {
                if (WriteHandler.this.done) {
                    return;
                }
                WriteHandler.this.printStream.println(msg);
            }
        }

    }

    /**
     * 读处理器
     */
    private class ReadHandler extends Thread {
        private boolean done;

        public void exit() {
            this.done = true;
        }

        @Override
        public void run() {
            super.run();
            BufferedReader reader = null;
            PrintStream printStream = null;
            try {
                while (!done) {
                    //接收客户端消息
                    reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String string = reader.readLine();
                    if (StrUtil.isEmpty(string)) {
                        System.out.println("客户端没有数据可读....已关闭");
                        //关闭客户端
                        ClientHandler.this.exit();
                        break;
                    } else {
                        System.out.println(string);
                        //通知tcp server消息到达了
                        clientHandlerCallback.onNewMessageArrived(ClientHandler.this, string);
                    }
                }
            } catch (IOException e) {
                System.out.println("客户端连接异常：" + e.getMessage());
                //关闭客户端
                ClientHandler.this.exit();
            } finally {
                CloseUtil.closeAll(reader, printStream, client);
            }
        }
    }

    /**
     * clientHandler退出
     */
    public void exit() {
        this.writeHandler.exit();
        this.readHandler.exit();
        CloseUtil.closeAll(this.client);
        //通知tcp server 自己已经关闭
        clientHandlerCallback.onSelfClosed(this);
    }


}