package arvs.server;

import static arvs.server.Service.*;
import static arvs.server.Service.parseMessage;
import helma.xmlrpc.WebServer;
import helma.xmlrpc.XmlRpc;

public class XMLServer {

    public String workWithDB(String str) {
        //System.out.println("yay");
        return parseMessage(str);
    }

    public String add(String username, String title, String text) {
        if (addNote(username, title, text)) {
            return "success";
        } else {
            return "fail";
        }
    }

    public String reg(String username, String pswrd) {
        if (addUser(username, pswrd)) {
            return "success";
        } else {
            return "fail";
        }
    }

    public String titles(String user) {
        return getTitles(user);
    }

    public String del(String id) {
        return delNote(id);
    }

    public String note(String id) {
        return getNote(id);
    }

    public String check(String username, String pswrd) {
        System.out.println(username + pswrd);
        if (checkUser(username, pswrd)) {
            return "success";
        } else {
            return "fail";
        }
    }

    public String upd(String id, String title, String text) {
        return updNote(id, title, text);
    }

    public static void main(String[] args) {

        try {

            WebServer server = new WebServer(8841);
            XmlRpc.setEncoding("UTF8");
            server.addHandler("service", new XMLServer());
            server.start();
            System.out.println("XML-RPC Server Started successfully.");

        } catch (Exception exception) {
            System.err.println("Server init error: " + exception);
        }
    }
}
