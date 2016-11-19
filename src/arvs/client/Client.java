package arvs.client;

import java.io.*;
import java.net.*;

class Client extends Thread {

    static Integer port = 8411;
    static String host = "localhost";
    private String user;
    private Boolean connected = false;
    private Boolean isTcp = true;
    static private String spl = "#-#";

    public static void main(String args[]) {
        String dataSend = "1" + spl + "user" + spl + "passw";
       //sendTCP(dataSend);
       sendUDP(dataSend);
    }

    static public void sendUDP(String str) {
        try {
            byte[] data = str.getBytes();

            InetAddress addr = InetAddress.getByName(host);
            DatagramPacket pack
                    = new DatagramPacket(data, data.length, addr, port);
            DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.send(pack);
            udpSocket.close();
        } catch (Exception e) {
            System.out.println("init error: " + e);
        } // вывод исключений
    }

   static public void sendTCP(String str) {
        try {
            Socket tcpSocket = new Socket(host, port);
            tcpSocket.getOutputStream().write(str.getBytes());

            // читаем ответ
            byte buf[] = new byte[64 * 1024];
            int r = tcpSocket.getInputStream().read(buf);
            String data = new String(buf, 0, r);

            // выводим ответ в консоль
            System.out.println(data);
        } catch (Exception e) {
            System.out.println("init error: " + e);
        } // вывод исключений
    }
}
