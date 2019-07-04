package com.socket.server;

import com.socket.server.handler.ClientHandler;
import com.socket.utils.CloseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: xch
 * @create: 2019-07-02 15:49
 **/
public class TCPServer implements ClientHandler.ClientHandlerCallback {

    private int port;

    private ClientListener clientListener;

    private List<ClientHandler> clientHandlers = new ArrayList<>();

    public TCPServer(int port) throws IOException {
        this.port = port;
        this.clientListener = new ClientListener(port);
    }

    public void start() {
        this.clientListener.start();
    }

    public void stop() {
        this.clientListener.exit();
    }

    @Override
    public synchronized void onNewMessageArrived(ClientHandler handler, String msg) {
        clientHandlers
                .stream()
                .filter(h -> !h.equals(handler))
                .forEach(h -> h.send(msg));
    }

    @Override
    public synchronized void onSelfClosed(ClientHandler handler) {
        clientHandlers.remove(handler);
    }

    class ClientListener extends Thread {

        private ServerSocket server;
        private boolean done = false;

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
            System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort());
        }

        @Override
        public void run() {
            super.run();
            System.out.println("~服务器准备就绪");
            try {
                while (!done) {
                    Socket client = server.accept();
                    System.out.println("新客户端连接进来" + client.getInetAddress());
                    ClientHandler clientHandler = new ClientHandler(client, TCPServer.this);
                    synchronized (TCPServer.this) {
                        clientHandlers.add(clientHandler);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void exit() {
            this.done = true;
            CloseUtil.closeAll(this.server);
        }
    }


    public void exit() {
        synchronized (this) {
            clientHandlers.forEach(ClientHandler::exit);
            clientHandlers.clear();
        }
        clientListener.exit();
    }
}
