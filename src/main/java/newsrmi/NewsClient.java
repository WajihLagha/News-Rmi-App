package newsrmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class NewsClient {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            // Ask for server IP
            System.out.print("Enter server IP [default: localhost]: ");
            String ip = scanner.nextLine().trim();
            if (ip.isEmpty()) ip = "localhost";

            // Ask for port
            System.out.print("Enter server port [default: 1099]: ");
            String portInput = scanner.nextLine().trim();
            int port = portInput.isEmpty() ? 1099 : Integer.parseInt(portInput);

            // Ask for topic
            System.out.print("Enter a topic to fetch news about: ");
            String topic = scanner.nextLine().trim();

            // Connect to RMI Registry
            Registry registry = LocateRegistry.getRegistry(ip, port);
            NewsInterface stub = (NewsInterface) registry.lookup("NewsService");

            // Get and print results
            List<String> headlines = stub.getNewsHeadlines(topic);
            System.out.println("\nTop News Headlines for '" + topic + "':");
            for (String headline : headlines) {
                System.out.println("- " + headline);
            }

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
