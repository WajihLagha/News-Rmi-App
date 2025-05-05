package newsrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NewsInterface extends Remote {
    List<String> getNewsHeadlines(String topic) throws RemoteException;
}
