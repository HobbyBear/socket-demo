package com.socket.constants;

/**
 * @author: xch
 * @create: 2019-07-02 14:25
 **/
public class Constants {

    public interface UDP{
        /**
         * 服务器端固定端口，
         */
        int SERVER_LISTEN_PORT = 32000;
        /**
         * 客户端固定端口
         */
        int CLIENT_LISTEN_PORT = 31000;
    }

    public interface TCP{

        // 服务器固化UDP接收端口
        int PORT_SERVER = 30401;
    }
}
