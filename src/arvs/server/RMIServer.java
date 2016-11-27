package arvs.server;

import arvs.dbreq.DBNote;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.Properties;

public class RMIServer extends UnicastRemoteObject implements RMIServerIntf {

    public static final String MESSAGE = "Hello World";

    public RMIServer() throws RemoteException {
        super(8841);    // required to avoid the 'rmic' step, see below
    }

    public String getMessage() {
        return MESSAGE;
    }
    
    public DBNote getObj(DBNote note)  {
        return note;
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
}
