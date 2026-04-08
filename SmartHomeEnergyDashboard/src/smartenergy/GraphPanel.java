package smartenergy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class GraphPanel extends JPanel {

    private Map<String, Double> targetData = new LinkedHashMap<>();
    private Map<String, Double> displayData = new LinkedHashMap<>();
    private Map<String, Boolean> alertState = new LinkedHashMap<>();

    private String hoveredKey = null;

    private float popupAlpha = 0f;
    private float popupScale = 0.85f;
    private String popupText = "";
    private int popupX = 0;
    private int popupY = 0;
    

    private long lastSoundTime = 0; // 2-second cooldown for alert sound
    private float pulseAlpha = 0.3f;

    public GraphPanel() {

        setOpaque(false);

        // Animation Timer
        new javax.swing.Timer(20, e -> {

            for (String key : targetData.keySet()) {
                double current = displayData.getOrDefault(key, 0.0);
                double target = targetData.get(key);

                // Smooth animation toward target
                current += (target - current) * 0.45;
                if (Math.abs(target - current) < 0.0005) current = target;
                displayData.put(key, current);

                // Play alert sound for high usage (cooldown 2 sec)
                boolean wasAlerting = alertState.getOrDefault(key, false);

                if (current > 1.5 && !wasAlerting) {
                    alertState.put(key, true);
                    playAlertSound(5000);
                }

                if (current <= 1.5) {
                    alertState.put(key, false);
                }
            }

            // Popup animation
            if (hoveredKey != null) {
                popupAlpha = Math.min(1f, popupAlpha + 0.08f);
                popupScale = Math.min(1f, popupScale + 0.05f);
            } else {
                popupAlpha = Math.max(0f, popupAlpha - 0.08f);
                popupScale = Math.max(0.85f, popupScale - 0.05f);
            }

            // Pulse animation
            if (!displayData.isEmpty()) {
                if (pulseAlpha >= 0.7f) pulseAlpha = 0.3f;
                else pulseAlpha += 0.02f;
            }

            repaint();
        }).start();

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                detectHover(e.getX(), e.getY());
            }
        });
    }

    public void updateData(Map<String, Double> newData) {
        targetData = new LinkedHashMap<>(newData);
        for (String k : newData.keySet()) displayData.putIfAbsent(k, 0.0);
    }

    private void detectHover(int mx, int my) {

        if (targetData.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int count = displayData.size();
        int margin = 40;
        int gap = 30;

        int barWidth = (width - (2 * margin) - (gap * (count - 1))) / count;
        int x = margin;

        double max = Math.max(Collections.max(targetData.values()), 5.0);
        int maxHeight = height - 150;

        hoveredKey = null;

        for (String key : displayData.keySet()) {

            double val = displayData.get(key);
            int h = (int) ((val / max) * maxHeight);
            int y = height - h - 60;

            Rectangle r = new Rectangle(x, y, barWidth, h);

            if (r.contains(mx, my)) {
                hoveredKey = key;
                popupText = key + "\nUsage: " +
                        String.format("%.2f kWh", val) +
                        (val > 1.5 ? "\n⚠ HIGH ENERGY ALERT\nReduce usage immediately!"
                                : "\n✔ Normal Usage");

                int boxWidth = 340;
                int boxHeight = 180;

                popupX = mx + 20;
                popupY = my - boxHeight / 2;

                if (popupX + boxWidth > getWidth()) popupX = mx - boxWidth - 20;
                if (popupY + boxHeight > getHeight()) popupY = getHeight() - boxHeight - 10;
                if (popupY < 10) popupY = 10;

                return;
            }

            x += barWidth + gap;
        }
    }

    // Sound logic from first code
    private void playAlertSound(int totalMilliseconds) {
        new Thread(() -> {
            try {
                float frequency = 900f;
                float sampleRate = 44100;
                int beepDuration = 300;
                int pauseDuration = 300;

                AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();

                long startTime = System.currentTimeMillis();

                while (System.currentTimeMillis() - startTime < totalMilliseconds) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    long remaining = totalMilliseconds - elapsed;

                    int currentBeep = (int)Math.min(beepDuration, remaining);
                    byte[] buf = new byte[(int)(currentBeep * sampleRate / 1000)];
                    for (int i = 0; i < buf.length; i++) {
                        double angle = 2.0 * Math.PI * frequency * i / sampleRate;
                        buf[i] = (byte)(Math.sin(angle) * 127);
                    }
                    sdl.write(buf, 0, buf.length);

                    elapsed = System.currentTimeMillis() - startTime;
                    remaining = totalMilliseconds - elapsed;
                    int currentPause = (int)Math.min(pauseDuration, remaining);
                    byte[] pauseBuf = new byte[(int)(currentPause * sampleRate / 1000)];
                    sdl.write(pauseBuf, 0, pauseBuf.length);
                }

                sdl.drain();
                sdl.close();
              

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        setBackground(new Color(245, 245, 255));

        if (displayData.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.BOLD, 14));

        int width = getWidth();
        int height = getHeight();
        int count = displayData.size();
        int sideMargin = 40;
        int gap = 30;

        int availableWidth = width - 2 * sideMargin - gap * (count - 1);
        int barWidth = availableWidth / count;
        int x = sideMargin;

        int maxHeight = height - 150;
        double maxValue = Math.max(Collections.max(targetData.values()), 5.0);

        for (String key : displayData.keySet()) {
            double value = displayData.get(key);
            int barHeight = (int)((value / maxValue) * maxHeight);
            int y = height - barHeight - 60;

            boolean isAlert = value > 1.5;

            // Gradient colors by appliance
            Color barColor;
            switch(key) {
                case "AC": barColor = new Color(0, 120, 255); break;
                case "Cooler": barColor = new Color(0, 200, 170); break;
                case "Fan": barColor = new Color(255, 150, 0); break;
                case "Refrigerator": barColor = new Color(150, 0, 255); break;
                case "LED": barColor = new Color(255, 80, 150); break;
                default: barColor = new Color(100,100,255);
            }

            GradientPaint gp = new GradientPaint(
                    x, y, barColor.brighter(),
                    x, y + barHeight, barColor.darker()
            );

            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barWidth, barHeight, 25, 25);

            // 3D side shadow
            g2.setColor(new Color(0,0,0,30));
            g2.fillRoundRect(x + 5, y + 5, barWidth, barHeight,25,25);

            // Highlight top edge
            g2.setColor(new Color(255,255,255,80));
            g2.drawLine(x, y, x + barWidth, y);

            // Pulsing alert glow
            if (isAlert) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha));
                g2.setColor(Color.RED);
                g2.fillRect(x - 5, y - 5, barWidth + 10, barHeight + 10);
                g2.setComposite(AlphaComposite.SrcOver);
            }

            // Draw value & appliance name
            g2.setColor(Color.WHITE);
            g2.drawString(String.format("%.2f kWh", value), x, y - 10);
            g2.drawString(key, x, height - 25);

            x += barWidth + gap;
        }

        // Draw hover popup
        if (popupAlpha > 0f) {
            Graphics2D gPopup = (Graphics2D) g.create();
            gPopup.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, popupAlpha));

            int boxWidth = 340;
            int boxHeight = 180;

            gPopup.translate(popupX + boxWidth/2, popupY + boxHeight/2);
            gPopup.scale(popupScale, popupScale);
            gPopup.translate(-(popupX + boxWidth/2), -(popupY + boxHeight/2));

            gPopup.setColor(new Color(20,20,30,240));
            gPopup.fillRoundRect(popupX,popupY,boxWidth,boxHeight,25,25);

            gPopup.setColor(new Color(0,255,200));
            gPopup.setStroke(new BasicStroke(3));
            gPopup.drawRoundRect(popupX,popupY,boxWidth,boxHeight,25,25);

            gPopup.setColor(Color.WHITE);
            gPopup.setFont(new Font("Segoe UI",Font.BOLD,15));

            int textY = popupY + 35;
            for(String line : popupText.split("\n")){
                gPopup.drawString(line, popupX + 20, textY);
                textY += 25;
            }

            gPopup.dispose();
        }
    }
}