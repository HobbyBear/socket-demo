package com.socket.utils;

/**
 * @author: xch
 * @create: 2019-07-02 11:06
 **/
public class MsgCreaterUtil {
    private static final String SERVER_INFO_HEAD = "收到消息,这是我得服务器信息：";

    private static final String PORT_HEAD = "收到回电端口，这是port：";


    public static String buildPort(int port) {
        return PORT_HEAD + port;
    }

    public static String buildServerInfoStr(String serverInfoStr) {
        return SERVER_INFO_HEAD + serverInfoStr;
    }

    public static int parsePort(String portMsg) {
        return Integer.valueOf(portMsg.substring(PORT_HEAD.length()));
    }

    public static String parseServerInfoStr(String snMsg) {
        return snMsg.substring(SERVER_INFO_HEAD.length());
    }
}
