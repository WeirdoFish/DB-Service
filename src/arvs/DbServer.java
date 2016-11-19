package arvs;

import arvs.database.MyHibernateUtil;
import arvs.database.Notes;
import arvs.database.Owners;
import arvs.database.Users;
import java.net.*;
import java.io.*;
import java.util.Date;
import java.sql.Timestamp;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DbServer extends Thread {

    Socket socket;
    int num;
    static Integer port = 8411;
    static String host = "localhost";

    public static void main(String[] args) {
        try {
            write();
            int i = 0; //считаем подключения
            ServerSocket server = new ServerSocket(port, 0, InetAddress.getByName(host));
            System.out.println("Server is started on " + host + ":" + port.toString());

            while (true) {
                new DbServer(i, server.accept());
                i++;
            }

        } catch (Exception e) {
            System.out.println("Server init error: " + e);
        }
    }

    public DbServer(int num, Socket s) {
        this.num = num;
        this.socket = s;

        //запускаем поток
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            // буффер данных в 64 килобайта
            byte buf[] = new byte[64 * 1024];
            int r = input.read(buf);
            String data = new String(buf, 0, r);

            // TODO разбирать запросы и данные
            //TODO после работы с бд слать их обратно
            output.write(data.getBytes());

            socket.close();
        } catch (Exception e) {
            System.out.println("Thread init error: " + e);
        }
    }

    static public void write() {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer resId = null;
        Integer itId = null;
        try {
            tx = session.beginTransaction();

            Users user = new Users("newuser","newpas");
            session.save(user);

            Owners owner = new Owners();
            owner.setUsername(user);
            session.save(owner);

            Notes note = new Notes("titilr","textxtxt",owner);
            session.save(note);

            owner.setNotes(note);
            session.save(owner);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

    }
}
