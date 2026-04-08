package smartenergy;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnergyIntelligenceLab extends JFrame {

    private Map<String, Double> data;
    private double totalEnergy;
    private double carbonEmission;
    private double treeEquivalent;
    private double efficiencyScore;
    private double riskScore;

    private Map<String, Double> healthIndex = new LinkedHashMap<>();
    private List<Double> trend = new ArrayList<>();

    // Smooth gauge animation variable
    private double gaugeAnimation = 0;

    public EnergyIntelligenceLab() {

        setTitle("AI Energy Intelligence Laboratory");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        updateData();

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0,0,new Color(5,5,20),
                        getWidth(),getHeight(),
                        new Color(0,80,110)
                );
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());

                drawGauge(g2);
                drawHealthBars(g2);
                drawAIInsights(g2);
                drawTrendGraph(g2);
            }
        };

        panel.setPreferredSize(new Dimension(1600, 1500));
        JScrollPane scrollPane = new JScrollPane(panel);
        setContentPane(scrollPane);

        // 🔵 Real-time data update (same as dashboard source)
        new javax.swing.Timer(3000, e -> {
            updateData();
        }).start();

        // 🔵 Smooth gauge animation (follows real risk value)
        new javax.swing.Timer(30, e -> {

            if (gaugeAnimation < riskScore) {
                gaugeAnimation += 1;
            } 
            else if (gaugeAnimation > riskScore) {
                gaugeAnimation -= 1;
            }

            repaint();

        }).start();

        setVisible(true);
    }

    private void updateData() {

        data = EnergyCalculator.calculateEnergy();

        totalEnergy = 0;
        for(double v : data.values()) {
            totalEnergy += v;
        }

        // Real AI calculations
        riskScore = Math.min(100, totalEnergy * 5);
        carbonEmission = totalEnergy * 0.82;
        treeEquivalent = carbonEmission / 21;
        efficiencyScore = Math.max(30, 100 - (totalEnergy * 5));

        healthIndex.clear();
        for(String key : data.keySet()) {
            double health = Math.max(40, 100 - (data.get(key) * 5));
            healthIndex.put(key, health);
        }

        trend.add(totalEnergy);
        if(trend.size() > 40) {
            trend.remove(0);
        }
    }

    private void drawGauge(Graphics2D g2){

        int size = 250;
        int x = 150;
        int y = 150;

        g2.setColor(new Color(30,30,45));
        g2.fillOval(x,y,size,size);

        g2.setStroke(new BasicStroke(18));
        g2.setColor(Color.GREEN);

        g2.drawArc(x,y,size,size,90,
                -(int)(gaugeAnimation/100*360));

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI",Font.BOLD,24));
        g2.drawString("AI Risk Score", x, y-20);
        g2.drawString((int)gaugeAnimation+"%", x+80, y+140);
    }

    private void drawHealthBars(Graphics2D g2){

        int x = 500;
        int y = 150;
        int barWidth = 300;
        int height = 25;

        g2.setFont(new Font("Segoe UI",Font.BOLD,20));
        g2.setColor(Color.WHITE);
        g2.drawString("Appliance Health Index", x, y-20);

        for(String key : healthIndex.keySet()){

            double value = healthIndex.get(key);

            g2.setColor(new Color(40,40,60));
            g2.fillRect(x,y,barWidth,height);

            g2.setColor(value>70?Color.GREEN:
                        value>40?Color.ORANGE:Color.RED);

            g2.fillRect(x,y,(int)(barWidth*(value/100)),height);

            g2.setColor(Color.WHITE);
            g2.drawString(key+"  "+(int)value+"%", x+10,y+18);

            y+=45;
        }
    }

    private void drawAIInsights(Graphics2D g2){

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI",Font.BOLD,22));

        g2.drawString("Carbon Emission: "+
                String.format("%.2f kg CO2",carbonEmission),150,480);

        g2.drawString("Trees Needed to Offset: "+
                String.format("%.1f",treeEquivalent)+" 🌳",150,520);

        g2.drawString("Energy Efficiency Score: "+
                (int)efficiencyScore+"%",150,560);
    }

    private void drawTrendGraph(Graphics2D g2){

        int startX = 150;
        int startY = 650;
        int width = 1100;
        int height = 300;

        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        g2.drawString("Live AI Consumption Forecast", startX, startY - 30);

        g2.setColor(new Color(20, 30, 50, 200));
        g2.fillRoundRect(startX, startY, width, height, 30, 30);

        g2.setColor(new Color(0, 255, 200));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(startX, startY, width, height, 30, 30);

        if(trend.size() < 2) return;

        double max = Collections.max(trend);
        double min = Collections.min(trend);

        int points = trend.size();
        int gap = width / 40;

        int prevX = startX;
        int prevY = startY + height;

        g2.setStroke(new BasicStroke(3f));

        for(int i = 0; i < points; i++){

            int x = startX + i * gap;
            double normalized = (trend.get(i) - min) / (max - min + 0.001);
            int y = startY + height - (int)(normalized * height);

            g2.fillOval(x-3, y-3, 6, 6);

            if(i > 0){
                g2.drawLine(prevX, prevY, x, y);
            }

            prevX = x;
            prevY = y;
        }
    }
}