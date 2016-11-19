package arvs.server;

import static arvs.server.Service.parseMessage;
import java.net.*;
import java.io.*;

public class TCPServer extends Thread {

    Socket tcpSocket;
    int num;
    static Integer port = 8411;
    static String host = "localhost";

    public static void main(String[] args) {
        try {
            // write();
            int i = 0; //считаем подключения
            
            //TCP
            ServerSocket server = new ServerSocket(port, 0, InetAddress.getByName(host));
            System.out.println("TCP Server is started on " + host + ":" + port.toString());
      
            
            while (true) {
                
                new  TCPServer(i, server.accept());
                i++;
            }

        } catch (Exception e) {
            System.out.println("Server init error: " + e);
        }
    }

    public  TCPServer(int num, Socket s) {
        this.num = num;
        this.tcpSocket = s;

        //запускаем поток
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Hello, new client!");
            InputStream input = tcpSocket.getInputStream();
            OutputStream output = tcpSocket.getOutputStream();

            // буффер данных в 64 килобайта
            byte buf[] = new byte[64 * 1024];
            int r = input.read(buf);
            String data = new String(buf, 0, r);

            String answer = parseMessage(data);
            output.write(data.getBytes());

            tcpSocket.close();
        } catch (Exception e) {
            System.out.println("Thread init error: " + e);
        }
    }

    

   
}
