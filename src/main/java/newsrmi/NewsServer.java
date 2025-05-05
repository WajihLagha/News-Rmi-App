package newsrmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NewsServer {
    public static void main(String[] args) {
        try {
            NewsInterface stub = new NewsServerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("NewsService", stub);
            System.out.println("🟢 News RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
