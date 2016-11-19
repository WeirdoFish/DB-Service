package arvs.server;

import arvs.database.MyHibernateUtil;
import arvs.database.Notes;
import arvs.database.Owners;
import arvs.database.Users;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Service {

    static protected String parseMessage(String data) {

        String[] parsedData = data.split("#-#", 3);
        String operation = parsedData[0];
        System.out.println(parsedData[0]);
        System.out.println(parsedData[1]);
        System.out.println(parsedData[2]);

        switch (operation) {
            case "auth": {
                String username = parsedData[1];
                String password = parsedData[2];
                //проверяем в бд, что данные верные и возвращаем ответ
                break;
            }
            case "print": {
                String neededUser = parsedData[1];
                //поиск записей и отправка их назад
                break;
            }
            case "del": {
                String delete = parsedData[1];
                //обращение к owners

                break;
            }
            case "add": {
                String username = parsedData[1];
                String[] appendData = parsedData[2].split("#-#", 2);
                String title = appendData[0];
                String text = appendData[1];

                //ну и уже вполне готовая операция
                break;
            }
            case "chng": {
                String username = parsedData[1];
                String[] appendData = parsedData[2].split("#-#", 2);
                String col = appendData[0];
                String text = appendData[1];
                //????
                break;
            }
            default:
                break;
        }

        return "smth";
    }

    static public void write() {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer resId = null;
        Integer itId = null;
        try {
            tx = session.beginTransaction();

            Users user = new Users("newuser2", "newpas");
            session.save(user);

            Owners owner = new Owners();
            owner.setUsername(user);
            session.save(owner);

            Notes note = new Notes("titilr", "textxtxt", owner);
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
