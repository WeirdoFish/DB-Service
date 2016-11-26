package arvs.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerIntf extends Remote {
    public String getMessage() throws RemoteException;
}