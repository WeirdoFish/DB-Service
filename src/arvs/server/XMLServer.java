package arvs.server;

import static arvs.server.Service.parseMessage;
import helma.xmlrpc.WebServer;

public class XMLServer {

  public String workWithDB(String str){
      System.out.println("yay");
      return parseMessage(str);
   }

   public static void main (String [] args){
   
      try {
        
         WebServer server = new WebServer(8841);
         server.addHandler("service", new XMLServer());
         server.start();
         System.out.println("XML-RPC Server Started successfully.");
         
      } catch (Exception exception){
         System.err.println("Server init error: " + exception);
      }
   }
}