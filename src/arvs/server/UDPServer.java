package arvs.server;

import static arvs.server.Service.parseMessage;
import java.net.*;
import java.io.*;
import static java.lang.Thread.NORM_PRIORITY;

public class UDPServer extends Thread {

    DatagramSocket udpSocket;
    int num;
    static Integer port = 8411;
    static String host = "localhost";
    DatagramPacket msg;

    public static void main(String[] args) {
        try {
            DatagramSocket uSock = new DatagramSocket(port);
            // write();
            int i = 0; //считаем подключения
            System.out.println("UDP Server is started on " + host + ":" + port.toString());
            while (true) {
                try {
                    byte[] buf = new byte[64 * 1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    uSock.receive(packet);
                    new UDPServer(i, packet);
                    i++;
                } catch (Exception e) {
                    System.out.println("Server init error: " + e);
                }
            }

        } catch (Exception e) {
            System.out.println("Server init error: " + e);
        }
    }

    public UDPServer(int num, DatagramPacket p) {
        this.num = num;
        try {
            this.udpSocket = new DatagramSocket();
        } catch (Exception e) {
            System.out.println("Client thread init error: " + e);
        }
        this.msg = p;
        //запускаем поток
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Hello, new udp client! ID=" + num);

            String result = new String(msg.getData());
            System.out.println(result);

//Обработка
            String answer = parseMessage(result);

            byte[] buf = answer.getBytes();
            DatagramPacket p = new DatagramPacket(buf, buf.length, msg.getSocketAddress());
            udpSocket.send(p);
            udpSocket.close();
        } catch (Exception e) {
            System.out.println("Thread init error: " + e);
        }
    }

}
