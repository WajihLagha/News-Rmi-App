package newsrmi;

import javax.swing.*;
import java.util.regex.Pattern;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NewsServer {
    public static String getIPAddress() {
        String[] options = {"Use localhost", "Enter IP Address"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Select an option to connect:",
                "Connection Setup",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        String ipAddress;

        if (choice == 0) {
            ipAddress = "127.0.0.1";
        } else if (choice == 1) {
            ipAddress = JOptionPane.showInputDialog(null, "Enter IP Address:", "Input IP", JOptionPane.QUESTION_MESSAGE);
            if (ipAddress == null || ipAddress.trim().isEmpty()) {
                ipAddress = "127.0.0.1"; // fallback
            }
        } else {
            return null; // User canceled
        }

        return ipAddress;
    }
    public static String getPort() {
        String port;

        while (true) {
            port = JOptionPane.showInputDialog(null, "Enter port:", "Input Port", JOptionPane.QUESTION_MESSAGE);

            if (port == null || port.trim().isEmpty()) {
                return "1099"; // default fallback
            }

            try {
                int portNum = Integer.parseInt(port.trim());
                if (portNum >= 0 && portNum <= 65535) {
                    return String.valueOf(portNum);
                } else {
                    JOptionPane.showMessageDialog(null, "Port must be between 0 and 65535.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            }
        }
    }
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", getIPAddress());
            NewsInterface stub = new NewsServerImpl();
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(getPort()));
            registry.rebind("NewsService", stub);
            System.out.println("🟢 News RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
