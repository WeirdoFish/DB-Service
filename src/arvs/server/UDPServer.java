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
            // write();
            int i = 0; //считаем подключения

            System.out.println("UDP Server is started on " + host + ":" + port.toString());
            while (true) {
                try{
                    DatagramSocket uSock = new DatagramSocket(port);
                    InetAddress address = InetAddress.getByName(host);
                    byte[] buf = new byte[64 * 1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    uSock.receive(packet);
                    new UDPServer(i, uSock, packet);
                    i++;
                } catch (BindException e){
                    Thread.sleep(1000);
                }
            }

        } catch (Exception e) {
            System.out.println("Server init error: " + e);
        }
    }

    public UDPServer(int num, DatagramSocket s, DatagramPacket p) {
        this.num = num;
        this.udpSocket = s;
        this.msg = p;
        //запускаем поток
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Hello, new udp client!");

            String result = new String(msg.getData());
            System.out.println(result);
            String answer = parseMessage(result);
            // output.write(data.getBytes());

            udpSocket.close();
        } catch (Exception e) {
            System.out.println("Thread init error: " + e);
        }
    }

}
