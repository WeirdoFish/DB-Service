package arvs.server;

import arvs.dbreq.DBNote;
import static arvs.server.ServiceRMI.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.ArrayList;

public class RMIServer extends UnicastRemoteObject implements RMIServerIntf {


    public RMIServer() throws RemoteException {
        super(8841);    // required to avoid the 'rmic' step, see below
    }

    public static void main(String args[]) throws Exception {
        System.out.println("RMI server started");

        System.setProperty("java.security.policy", "server.policy");
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");

        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        //Instantiate RmiServer
        RMIServer obj = new RMIServer();

        // Bind this object instance to the name "RmiServer"
        Naming.rebind("rmi://localhost/RMIServer", obj);
        System.out.println("PeerServer bound in registry");
    }

    @Override
    public boolean add(DBNote req) throws RemoteException {
        return addNote(req);
    }

    @Override
    public boolean reg(String username, String pswrd) throws RemoteException {
        return addUser(username,pswrd);
    }

    @Override
    public String[] titles(String username) throws RemoteException {
        return getTitles(username);
    }

    @Override
    public boolean check(String username, String pswrd) throws RemoteException {
        return checkUser(username,pswrd);
    }

    @Override
    public DBNote note(String id) throws RemoteException {
        return getNote(id);
    }

    @Override
    public boolean del(String id) throws RemoteException {
        return delNote(id);
    }

    @Override
    public boolean upd(String id, String title, String text) throws RemoteException { 
        return updNote(id,title,text);
    }
}
