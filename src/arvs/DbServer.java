package arvs;
import java.net.*;
import java.io.*;

public class DbServer extends Thread {

    Socket  socket;
    int num;
    
    public static void main(String[] args) {
       try {
           int i = 0; //считаем подключения
           ServerSocket server = new ServerSocket (1488,0,InetAddress.getByName("localhost"));
            System.out.println("Server is started on localhost:1488");
            
            while(true){
                new DbServer(i,server.accept());
                i++;
            }
           
       } catch (Exception e){
           System.out.println("Server init error: " + e);
       }
    }
    
    public DbServer(int num, Socket s){
        this.num=num;
        this.socket=s;
        
        //запускаем поток
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }
    
    public void run(){
        try{
            InputStream input =  socket.getInputStream();
            OutputStream output =  socket.getOutputStream();

            // буффер данных в 64 килобайта
            byte buf[] = new byte[64*1024];
            int r = input.read(buf);
            String data = new String(buf, 0, r);

            // TODO разбирать запросы и данные
            
            //TODO после работы с бд слать их обратно

            output.write(data.getBytes());

            
            socket.close();
        } catch(Exception e){
            System.out.println("Thread init error: "+e);
        }
    }
    
}
