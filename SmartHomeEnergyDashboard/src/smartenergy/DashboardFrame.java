package smartenergy;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

public class DashboardFrame extends JFrame {

    private JPanel reportPanel;
    private GraphPanel graph;
    private JLabel clockLabel;
    private double totalEnergy = 0;

    private float titleGlow = 0;
    private float glowDir = 0.03f;

    private int[] px = new int[50];
    private int[] py = new int[50];
    private Random rand = new Random();

    public DashboardFrame() {

        LogGenerator.start();

        setTitle("Enterprise Smart Energy AI System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        for(int i=0;i<px.length;i++){
            px[i]=rand.nextInt(1200);
            py[i]=rand.nextInt(800);
        }

        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                Color baseTop = new Color(8,8,15);
                Color baseBottom = new Color(0,60,55);

                if(totalEnergy > 5) baseBottom = new Color(80,0,0);
                if(totalEnergy > 8) baseBottom = new Color(120,0,0);

                GradientPaint gp = new GradientPaint(
                        0,0,baseTop,
                        getWidth(),getHeight(),
                        baseBottom
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());

                g2.setColor(new Color(0,255,200,80));
                for(int i=0;i<px.length;i++){
                    g2.fillOval(px[i],py[i],4,4);
                    py[i]+=1;
                    if(py[i]>getHeight()){
                        py[i]=0;
                        px[i]=rand.nextInt(getWidth());
                    }
                }
            }
        };

        bgPanel.setLayout(new BorderLayout());
        setContentPane(bgPanel);

        JLabel title = new JLabel("SMART HOME ENERGY DASHBOARD");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,30));
        title.setForeground(new Color(0,255,200));

        clockLabel = new JLabel();
        clockLabel.setForeground(Color.CYAN);
        clockLabel.setFont(new Font("Consolas",Font.BOLD,16));
        clockLabel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title,BorderLayout.CENTER);
        top.add(clockLabel,BorderLayout.EAST);

        add(top,BorderLayout.NORTH);

        // ===== ENERGY INTELLIGENCE BUTTON =====

        JButton aiButton = new JButton("Energy Intelligence Lab");
        aiButton.setFocusPainted(false);
        aiButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        aiButton.setBackground(new Color(0,255,200));
        aiButton.setForeground(Color.BLACK);
        aiButton.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));

        aiButton.addActionListener(e -> {
            new EnergyIntelligenceLab();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(aiButton);

        add(buttonPanel, BorderLayout.WEST);

        new Timer(1000,e->{
            clockLabel.setText(
                    LocalTime.now().format(
                            DateTimeFormatter.ofPattern("HH:mm:ss")
                    )
            );
        }).start();

        new Timer(40,e->{
            titleGlow+=glowDir;
            if(titleGlow>1 || titleGlow<0) glowDir*=-1;
            int glowValue=(int)(150+100*titleGlow);
            title.setForeground(new Color(0,glowValue,200));
            repaint();
        }).start();

        // ================= LEFT SIDE PANEL =================

        reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
        reportPanel.setBackground(new Color(18,18,28));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JScrollPane scrollPane = new JScrollPane(reportPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(18,18,28));

        // ================= RIGHT SIDE =================

        graph = new GraphPanel();

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(graph,BorderLayout.CENTER);
        rightPanel.add(createEnergyMeterPanel(),BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scrollPane,
                rightPanel
        );
        split.setDividerLocation(420);
        split.setOpaque(false);
        split.setBorder(null);

        add(split,BorderLayout.CENTER);

        refresh();
        new Timer(3000,e->refresh()).start();

        setVisible(true);
    }

    private JPanel createEnergyMeterPanel(){

        JPanel meter = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int size=160;
                int x=(getWidth()-size)/2;
                int y=10;

                g2.setColor(new Color(30,30,45));
                g2.fillOval(x,y,size,size);

                double percent=Math.min(totalEnergy/10.0,1.0);

                g2.setColor(percent>0.7?Color.RED:new Color(0,255,200));
                g2.setStroke(new BasicStroke(12));
                g2.drawArc(x,y,size,size,90,-(int)(360*percent));

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI",Font.BOLD,16));
                g2.drawString("Total kWh",(getWidth()/2)-40,y+size+25);
                g2.drawString(String.format("%.2f",totalEnergy),
                        (getWidth()/2)-30,y+size+45);
            }
        };

        meter.setPreferredSize(new Dimension(350,230));
        meter.setOpaque(false);
        return meter;
    }

    private void refresh(){

        Map<String,Double> data = EnergyCalculator.calculateEnergy();
        totalEnergy = 0;

        reportPanel.removeAll();

        JLabel header = new JLabel("ENERGY ANALYTICS REPORT");
        header.setFont(new Font("Segoe UI", Font.BOLD, 17));
        header.setForeground(new Color(0,255,200));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        reportPanel.add(header);
        reportPanel.add(Box.createRigidArea(new Dimension(0,15)));

        double highest = 0;
        String highestAppliance = "";

        for(String appliance : data.keySet()){

            double value = data.get(appliance);
            totalEnergy += value;

            boolean highUsage = value > 1.5;

            JPanel card = new JPanel(new BorderLayout());
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE,55));
            card.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
            card.setBackground(highUsage ?
                    new Color(70,20,20) :
                    new Color(30,30,45));

            JLabel name = new JLabel(appliance);
            name.setFont(new Font("Segoe UI",Font.PLAIN,13));
            name.setForeground(Color.WHITE);

            JLabel usage = new JLabel(String.format("%.2f kWh",value));
            usage.setFont(new Font("Segoe UI",Font.BOLD,13));
            usage.setForeground(highUsage ? Color.RED : new Color(0,255,200));

            card.add(name,BorderLayout.WEST);
            card.add(usage,BorderLayout.EAST);

            reportPanel.add(card);
            reportPanel.add(Box.createRigidArea(new Dimension(0,6)));

            if(value > highest){
                highest = value;
                highestAppliance = appliance;
            }
        }

        reportPanel.add(Box.createRigidArea(new Dimension(0,15)));

        JPanel summary = new JPanel(new GridLayout(2,2,10,10));
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE,120));
        summary.setBackground(new Color(35,35,55));
        summary.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        summary.add(createStatBox("Total kWh",
                String.format("%.2f", totalEnergy)));

        summary.add(createStatBox("Total Bill",
                "₹"+String.format("%.2f",
                        EnergyCalculator.totalBill())));

        summary.add(createStatBox("Highest Usage",
                highestAppliance));
        summary.add(createStatBox("Est. Saving",
                "₹"+String.format("%.2f", highest*30)));

        reportPanel.add(summary);

        reportPanel.revalidate();
        reportPanel.repaint();

        graph.updateData(data);
    }

    private JPanel createStatBox(String title, String value){

        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(new Color(50,50,75));
        box.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI",Font.PLAIN,12));
        t.setForeground(Color.LIGHT_GRAY);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI",Font.BOLD,15));
        v.setForeground(new Color(0,255,200));

        box.add(t,BorderLayout.NORTH);
        box.add(v,BorderLayout.CENTER);

        return box;
    }
}