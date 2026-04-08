package smartenergy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomeFrame extends JFrame {

    private float hue = 0f;

    public HomeFrame() {

        setTitle("Enterprise Smart Energy AI System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ========= Animated Gradient Background =========
        JPanel background = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                int w = getWidth();
                int h = getHeight();

                Color c1 = Color.getHSBColor(hue, 0.6f, 0.15f);
                Color c2 = new Color(0, 60, 90);

                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
            }
        };

        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Animate background slowly
        Timer bgTimer = new Timer(40, e -> {
            hue += 0.001f;
            if (hue > 1) hue = 0;
            background.repaint();
        });
        bgTimer.start();

        // ========= HERO SECTION =========
        JPanel hero = new JPanel();
        hero.setOpaque(false);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(BorderFactory.createEmptyBorder(70, 20, 40, 20));

        JLabel title = new JLabel("SMART HOME ENERGY DASHBOARD");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 56));
        title.setForeground(new Color(0, 255, 200));

        JLabel subtitle = new JLabel("Intelligent • Predictive • Sustainable Energy Control");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitle.setForeground(Color.LIGHT_GRAY);

        hero.add(title);
        hero.add(Box.createRigidArea(new Dimension(0, 15)));
        hero.add(subtitle);

        background.add(hero, BorderLayout.NORTH);

        // ========= FEATURE CARDS =========
        JPanel cards = new JPanel(new GridLayout(1, 3, 40, 0));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(40, 120, 40, 120));

        cards.add(createPremiumCard(
                "⚡ Real-Time Monitoring",
                "Live appliance tracking\n\nAC • Fan • Cooler\nLED • Refrigerator"
        ));

        cards.add(createPremiumCard(
                "📊 Smart Analytics",
                "kWh Calculation\nBill Estimation\nHigh Usage Detection\nAI Recommendations"
        ));

        cards.add(createPremiumCard(
                "🚨 Intelligent Alerts",
                "Overconsumption Warnings\nRed Alert Indicators\nSmart Energy Optimization"
        ));

        background.add(cards, BorderLayout.CENTER);

        // ========= GLOW BUTTON =========
        JButton startBtn = new JButton("Launch Energy Dashboard");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        startBtn.setForeground(Color.BLACK);
        startBtn.setBackground(new Color(0, 255, 200));
        startBtn.setFocusPainted(false);
        startBtn.setBorder(BorderFactory.createEmptyBorder(18, 60, 18, 60));
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        startBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                startBtn.setBackground(new Color(0, 220, 180));
            }

            public void mouseExited(MouseEvent e) {
                startBtn.setBackground(new Color(0, 255, 200));
            }
        });

        // SAME DASHBOARD CALL (UNCHANGED)
        startBtn.addActionListener(e -> {
            new DashboardFrame();
            dispose();
        });

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 10, 70, 10));
        bottom.add(startBtn);

        background.add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ========= Premium Card =========
    private JPanel createPremiumCard(String titleText, String bodyText) {

        JPanel card = new JPanel() {

            boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                if (hover) {
                    g2.setColor(new Color(0, 255, 200, 60));
                } else {
                    g2.setColor(new Color(25, 30, 60, 230));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };

        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(35, 30, 35, 30));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 255, 200));

        JTextArea body = new JTextArea(bodyText);
        body.setEditable(false);
        body.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        body.setForeground(Color.WHITE);
        body.setBackground(new Color(25, 30, 60));
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setBorder(null);

        card.add(title, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        return card;
    }
}