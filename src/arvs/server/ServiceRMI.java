package arvs.server;

import arvs.database.MyHibernateUtil;
import arvs.database.Notes;
import arvs.database.Owners;
import arvs.database.Users;
import arvs.dbreq.DBNote;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ServiceRMI {

    static public boolean addNote(DBNote req) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer resId = null;
        Integer itId = null;
        try {
            tx = session.beginTransaction();
            List list = session.createQuery("FROM Users WHERE username='" + req.getName() + "'").list();
            Users user = (Users) list.get(0);

            Owners owner = new Owners();
            owner.setUsername(user);
            session.save(owner);

            Notes note = new Notes(req.getTitle(), req.getText(), owner);
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

    static public String[] getTitles(String username) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List list = session.createQuery("FROM Owners WHERE username='" + username + "'").list();
            if (list.isEmpty()) {

                String[] resp = new String[1];
                resp[0] = "fail";
                return resp;
            }
            int len = 0;
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                len++;
                Owners cur = (Owners) iter.next();
            }
            String[] resp = new String[len];
            
            int i = 0;
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                Owners cur = (Owners) iter.next();
                List note = session.createQuery("FROM Notes WHERE id='" + cur.getId() + "'").list();
                Notes curNote = (Notes) note.get(0);
                String tmp = "";
                tmp += cur.getId().toString();
                tmp += " ";
                tmp += curNote.getTitle();
                tmp += " ";
                tmp += curNote.getTime().toGMTString();
                resp[i] = (tmp);
                i++;
            }

            tx.commit();
            return resp;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            
                String[] resp = new String[1];
                resp[0] = "fail";
                return resp;
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

    static public DBNote getNote(String id) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        DBNote note = new DBNote();
        try {
            tx = session.beginTransaction();
            List list = session.createQuery("FROM Notes WHERE id='" + id + "'").list();
            if (list.isEmpty()) {
                note.setIsFailed(true);
                return note;
            }
            String result = "";

            Notes cur = (Notes) list.get(0);
            note.setTitle(cur.getTitle());
            note.setText(cur.getText());
            note.setIsFailed(false);
            tx.commit();
            return note;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            note.setIsFailed(true);
            return note;
        } finally {
            session.close();
        }

    }

    static public boolean delNote(String id) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createQuery("DELETE FROM Owners WHERE id='" + id + "'").executeUpdate();;

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

    static public boolean updNote(String id, String title, String text) {
        Session session = MyHibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createQuery("UPDATE Notes SET title='" + title + "', text='" + text + "' WHERE id='" + id + "'").executeUpdate();;

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

}
