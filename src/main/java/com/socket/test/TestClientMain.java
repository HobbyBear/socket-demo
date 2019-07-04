package com.socket.test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author: xch
 * @create: 2019-07-03 16:38
 **/
public class TestClientMain {
    public static void main(String[] args) throws InterruptedException, IOException {
        Socket client = null;
        client = new Socket();
        client.bind(new InetSocketAddress(38081));
        client.connect(new InetSocketAddress(38080));
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                PrintStream printStream = new PrintStream(client.getOutputStream());
                String string = scanner.next();
                if (string.equals("bye")){
                    printStream.close();
                    client.close();
                    break;
                }
                printStream.print(string);
                printStream.flush();
                Thread.sleep(1000);
            } catch (IOException e) {
                System.out.println("连接异常" + e.getMessage());
            }
        }

    }
}
