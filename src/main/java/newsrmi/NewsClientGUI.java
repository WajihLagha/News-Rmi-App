package newsrmi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.rmi.Naming;
import java.util.List;
import javax.swing.border.LineBorder;

public class NewsClientGUI {
    private static boolean darkMode = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create main frame with modern look
            JFrame frame = new JFrame("📰 News RMI Client");
            frame.setSize(600, 450);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setIconImage(new ImageIcon(createPlaceholderImage()).getImage());

            // Set system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Main panel with padding and gradient background
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    Paint oldPaint = g2d.getPaint();
                    if (darkMode) {
                        g2d.setPaint(new GradientPaint(0, 0, new Color(30, 30, 30), 0, getHeight(), new Color(50, 50, 50)));
                    } else {
                        g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(240, 240, 240)));
                    }
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setPaint(oldPaint);
                }
            };
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Input panel
            JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
            inputPanel.setOpaque(false);

            // Topic field with modern styling
            JTextField topicField = new JTextField();
            topicField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            topicField.setBackground(darkMode ? new Color(50, 50, 50) : Color.WHITE);
            topicField.setForeground(darkMode ? Color.WHITE : Color.BLACK);
            topicField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(darkMode ? new Color(70, 70, 70) : new Color(200, 200, 200), 1, true),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            topicField.setPreferredSize(new Dimension(200, 40));
            topicField.setToolTipText("Enter news topic (e.g., 'technology')");

            // Search button with hover effect
            JButton searchButton = new JButton("Search") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    if (getModel().isPressed()) {
                        g2.setColor(new Color(50, 150, 255));
                    } else {
                        g2.setColor(new Color(75, 192, 192));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    super.paintComponent(g);
                }
            };
            searchButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
            searchButton.setForeground(Color.WHITE);
            searchButton.setBorderPainted(false);
            searchButton.setFocusPainted(false);
            searchButton.setContentAreaFilled(false);
            searchButton.setPreferredSize(new Dimension(100, 40));

            // Add components to input panel
            inputPanel.add(new JLabel("🔍 Enter a topic:"), BorderLayout.WEST);
            inputPanel.add(topicField, BorderLayout.CENTER);
            inputPanel.add(searchButton, BorderLayout.EAST);

            // Results area with scrolling
            JTextArea resultArea = new JTextArea();
            resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);
            resultArea.setEditable(false);
            resultArea.setBackground(darkMode ? new Color(40, 40, 40) : Color.WHITE);
            resultArea.setForeground(darkMode ? Color.WHITE : Color.BLACK);
            resultArea.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(darkMode ? new Color(70, 70, 70) : new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder(
                    darkMode ? BorderFactory.createLineBorder(new Color(70, 70, 70)) : BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    "Latest News", 0, 0, new Font("Segoe UI", Font.BOLD, 14),
                    darkMode ? Color.WHITE : Color.BLACK
            ));

            // Add everything to main panel
            mainPanel.add(inputPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Add theme toggle button
            JButton themeButton = new JButton(darkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
            themeButton.addActionListener(e -> {
                darkMode = !darkMode;
                themeButton.setText(darkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
                SwingUtilities.updateComponentTreeUI(frame);
                mainPanel.repaint();
            });
            themeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            themeButton.setBackground(darkMode ? new Color(60, 60, 60) : Color.LIGHT_GRAY);
            themeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            mainPanel.add(themeButton, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setVisible(true);

            // Search action
            searchButton.addActionListener((ActionEvent e) -> {
                String topic = topicField.getText().trim();
                if (topic.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a topic!", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                resultArea.setText("🔄 Fetching news...");
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            NewsInterface stub = (NewsInterface) Naming.lookup("rmi://localhost:1099/NewsService");
                            List<String> headlines = stub.getNewsHeadlines(topic);

                            StringBuilder formatted = new StringBuilder("Top News Headlines for '" + topic + "':\n\n");
                            for (String headline : headlines) {
                                formatted.append("• ").append(headline).append("\n\n");
                            }

                            resultArea.setText(formatted.toString());
                        } catch (Exception ex) {
                            resultArea.setText("❌ Error: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            });
        });
    }

    // Helper method to create placeholder icon
    private static Image createPlaceholderImage() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(75, 192, 192));
        g.fillOval(0, 0, 16, 16);
        g.dispose();
        return image;
    }
}