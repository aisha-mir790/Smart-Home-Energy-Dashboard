package smartenergy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage {

    private static float glow = 0f;
    private static float direction = 0.02f;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Smart Home Energy");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ===== Background Gradient =====
        JPanel background = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(8, 10, 25),
                        getWidth(), getHeight(),
                        new Color(0, 140, 120)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        frame.setContentPane(background);

        // ================= TOP SECTION =================
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(70, 0, 20, 0));

        JLabel title = new JLabel("WELCOME TO SMART HOME ENERGY DASHBOARD");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setForeground(new Color(0, 255, 220));

        JLabel tagline = new JLabel("Power Intelligence for Modern Homes");
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setFont(new Font("Segoe UI Light", Font.PLAIN, 22));
        tagline.setForeground(new Color(200, 255, 240));

        JLabel description = new JLabel("");
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        description.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        description.setForeground(Color.WHITE);

        topPanel.add(title);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        topPanel.add(tagline);
        topPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        topPanel.add(description);

        background.add(topPanel, BorderLayout.NORTH);

        // ===== Typing Effect =====
        String fullText = "Monitor real-time energy consumption  |  Detect high usage patterns  |  Receive AI-driven optimization insights";

        Timer typingTimer = new Timer(30, null);
        final int[] index = {0};

        typingTimer.addActionListener(e -> {
            if (index[0] < fullText.length()) {
                description.setText(fullText.substring(0, index[0]));
                index[0]++;
            } else {
                typingTimer.stop();
            }
        });
        typingTimer.start();

        // ===== Title Glow Animation =====
        new Timer(40, e -> {
            glow += direction;
            if (glow > 1 || glow < 0) direction *= -1;
            int green = (int) (170 + 85 * glow);
            title.setForeground(new Color(0, green, 255));
        }).start();

        // ================= CENTER GLASS LOGIN =================
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        background.add(centerWrapper, BorderLayout.CENTER);

        JPanel glassCard = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.setColor(new Color(0, 255, 220, 180));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };

        glassCard.setPreferredSize(new Dimension(450, 300));
        glassCard.setOpaque(false);
        glassCard.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== USERNAME =====
        JLabel nameLabel = new JLabel("Username");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridy = 0;
        glassCard.add(nameLabel, gbc);

        JTextField nameField = new JTextField();
        styleField(nameField);
        gbc.gridy = 1;
        glassCard.add(nameField, gbc);

        // ===== PIN =====
        JLabel pinLabel = new JLabel("PIN");
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridy = 2;
        glassCard.add(pinLabel, gbc);

        JPanel pinPanel = new JPanel(new BorderLayout());
        pinPanel.setOpaque(false);

        JPasswordField pinField = new JPasswordField();
        styleField(pinField);
        pinField.setEchoChar('\u2022');

        JButton eyeButton = new JButton("👁");
        eyeButton.setFocusPainted(false);
        eyeButton.setBorder(null);
        eyeButton.setBackground(new Color(0, 255, 220));
        eyeButton.setPreferredSize(new Dimension(45, 30));

        eyeButton.addActionListener(e -> {
            if (pinField.getEchoChar() == '\u2022') {
                pinField.setEchoChar((char) 0);
            } else {
                pinField.setEchoChar('\u2022');
            }
        });

        pinPanel.add(pinField, BorderLayout.CENTER);
        pinPanel.add(eyeButton, BorderLayout.EAST);

        gbc.gridy = 3;
        glassCard.add(pinPanel, gbc);

        // ===== LOGIN BUTTON =====
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 255, 220));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        gbc.gridy = 4;
        glassCard.add(loginButton, gbc);

        centerWrapper.add(glassCard);

        // ===== LOGIN LOGIC (UPDATED FLOW) =====
        loginButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();

            if (name.equalsIgnoreCase("Admin") && pin.equals("1234")) {
                JOptionPane.showMessageDialog(frame, "Access Granted");

                frame.dispose();     // Close login
                new HomeFrame();     // Open Home Page

            } else {
                JOptionPane.showMessageDialog(frame,
                        "Invalid Username or PIN",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    // ===== Styling Method =====
    private static void styleField(JTextField field) {
        field.setBackground(new Color(0, 0, 0, 140));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.CYAN);
        field.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 220)));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    }
}