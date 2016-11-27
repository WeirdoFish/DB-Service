package arvs.server;

import arvs.dbreq.DBNote;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIServerIntf extends Remote {

    public boolean add(DBNote req) throws RemoteException;

    public boolean reg(String username, String pswrd) throws RemoteException;

    public String[] titles(String username) throws RemoteException;

    public boolean check(String username, String pswrd) throws RemoteException;

    public DBNote note(String id) throws RemoteException;

    public boolean del(String id) throws RemoteException;

    public boolean upd(String id, String title, String text) throws RemoteException;
}
