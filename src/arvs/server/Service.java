package arvs.server;

import arvs.database.MyHibernateUtil;
import arvs.database.Notes;
import arvs.database.Owners;
import arvs.database.Users;
import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Service {

    static String spl = "#-#";

    static protected String parseMessage(String data) {

        String[] parsedData = data.split(spl, 3);
        String operation = parsedData[0];
        System.out.println(parsedData[0]);
        System.out.println(parsedData[1]);
        //  System.out.println(parsedData[2]);
        switch (operation) {
            case "auth": {
                String username = parsedData[1];
                String password = parsedData[2];
                if (checkUser(username, password)) {
                    return "success";
                } else {
                    return "fail";
                }
            }
            case "reg": {
                String username = parsedData[1];
                String password = parsedData[2];
                if (addUser(username, password)) {
                    return "success";
                } else {
                    return "fail";
                }
            }
            case "titles": {
                String neededUser = parsedData[1];
                return getTitles(neededUser);
            }
            case "note": {
                String neededId = parsedData[1];
                return getNote(neededId);
            }
            case "del": {
                String delete = parsedData[1];
                return delNote(delete);
            }
            case "add": {
                String username = parsedData[1];
                String[] appendData = parsedData[2].split(spl, 2);
                String title = appendData[0];
                String text = appendData[1];
                if (addNote(username, title, text)) {
                    return "success";
                } else {
                    return "fail";
                }
            }
            case "upd": {
                String id = parsedData[1];
                String[] appendData = parsedData[2].split(spl, 2);
                String title = appendData[0];
                String text = appendData[1];
                return updNote(id,title,text);
            }
            default:
                break;
        }

        return "fail";
    }

    static public boolean addNote(String username, String title, String text) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer resId = null;
        Integer itId = null;
        try {
            tx = session.beginTransaction();
            List list = session.createQuery("FROM Users WHERE username='" + username + "'").list();
            Users user = (Users) list.get(0);

            Owners owner = new Owners();
            owner.setUsername(user);
            session.save(owner);

            Notes note = new Notes(title, text, owner);
            session.save(note);

            owner.setNotes(note);
            session.save(owner);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }

    }

    static public boolean addUser(String username, String pswrd) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List list = session.createQuery("FROM Users WHERE username='" + username + "'").list();
            if (!list.isEmpty()) {
                return false;
            }
            Users user = new Users(username, pswrd);
            session.save(user);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }

    }

    static public String getTitles(String username) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List list = session.createQuery("FROM Owners WHERE username='" + username + "'").list();
            if (list.isEmpty()) {
                return "fail";
            }
            String result = "";
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                Owners cur = (Owners) iter.next();
                List note = session.createQuery("FROM Notes WHERE id='" + cur.getId() + "'").list();
                Notes curNote = (Notes) note.get(0);

                result += cur.getId().toString();
                result += " ";
                result += curNote.getTitle();
                result += " ";
                result += curNote.getTime().toGMTString();
                if (iter.hasNext()) {
                    result += spl;
                }
            }

            tx.commit();
            return result;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return "fail";
        } finally {
            session.close();
        }

    }

    static public boolean checkUser(String username, String pswrd) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List list = session.createQuery("FROM Users WHERE username='" + username + "'").list();
            tx.commit();
            if (list.isEmpty()) {
                return false;
            }
            Users user = (Users) list.get(0);
            if (pswrd.equals(user.getPassword())) {
                return true;
            }

            return false;

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }

    }

    static public String getNote(String id) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List list = session.createQuery("FROM Notes WHERE id='" + id + "'").list();
            if (list.isEmpty()) {
                return null;
            }
            String result = "";
            //  for (Iterator iter = list.iterator(); iter.hasNext();) {
            Notes cur = (Notes) list.get(0);
            // result += cur.getId().toString();
            //  result += spl;
            result += cur.getTitle();
            result += spl;
            //result += cur.getTime().toGMTString();
            // result += spl;
            result += cur.getText();
            //  }

            tx.commit();
            return result;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }

    }

    static public String delNote(String id) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createQuery("DELETE FROM Owners WHERE id='" + id + "'").executeUpdate();;

            tx.commit();
            return "success";
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return "fail";
        } finally {
            session.close();
        }

    }
    
     static public String updNote(String id,String title, String text) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createQuery("UPDATE Notes SET title='"+title+"', text='"+text+"' WHERE id='" + id + "'").executeUpdate();;

            tx.commit();
            return "success";
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return "fail";
        } finally {
            session.close();
        }

    }

}
