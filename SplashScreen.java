package hrms;
import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        JPanel p = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(7, 17, 31));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setBorder(new javax.swing.border.EmptyBorder(50, 60, 40, 60));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // ── HR Icon Box ──
        JPanel iconBox = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(26, 58, 110));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g.setColor(new Color(99, 153, 255, 80));
                g.setStroke(new java.awt.BasicStroke(1f));
                g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(64, 64));
        iconBox.setMaximumSize(new Dimension(64, 64));
        iconBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel iconLbl = new JLabel("HR");
        iconLbl.setFont(new Font("Arial", Font.BOLD, 20));
        iconLbl.setForeground(new Color(99, 153, 255));
        iconBox.add(iconLbl);

        // ── Main Title ──
        JLabel title = new JLabel("HRMS");
        title.setFont(new Font("Arial", Font.BOLD, 52));
        title.setForeground(new Color(99, 153, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Subtitle ──
        JLabel sub = new JLabel("Human Resource Management System");
        sub.setFont(new Font("Arial", Font.PLAIN, 14));
        sub.setForeground(new Color(130, 153, 200));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Version ──
        JLabel ver = new JLabel("v2.0  —  DBMS Project  —  May 2026");
        ver.setFont(new Font("Arial", Font.ITALIC, 11));
        ver.setForeground(new Color(70, 95, 140));
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Divider ──
        JPanel div = new JPanel();
        div.setBackground(new Color(30, 50, 90));
        div.setMaximumSize(new Dimension(300, 1));
        div.setPreferredSize(new Dimension(300, 1));
        div.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Progress Bar ──
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(0);
        bar.setBorderPainted(false);
        bar.setBackground(new Color(20, 35, 65));
        bar.setForeground(new Color(30, 77, 153));
        bar.setMaximumSize(new Dimension(300, 5));
        bar.setPreferredSize(new Dimension(300, 5));
        bar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Loading Message ──
        JLabel loading = new JLabel("Connecting to database...");
        loading.setFont(new Font("Arial", Font.PLAIN, 11));
        loading.setForeground(new Color(70, 95, 140));
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Developer Name ──
        JLabel dev = new JLabel("Hafsa Ashraf  —  01-132232-052");
        dev.setFont(new Font("Arial", Font.PLAIN, 10));
        dev.setForeground(new Color(50, 75, 110));
        dev.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Assemble ──
        center.add(iconBox);
        center.add(Box.createVerticalStrut(16));
        center.add(title);
        center.add(Box.createVerticalStrut(8));
        center.add(sub);
        center.add(Box.createVerticalStrut(4));
        center.add(ver);
        center.add(Box.createVerticalStrut(20));
        center.add(div);
        center.add(Box.createVerticalStrut(16));
        center.add(bar);
        center.add(Box.createVerticalStrut(8));
        center.add(loading);
        center.add(Box.createVerticalStrut(20));
        center.add(dev);

        p.add(center, BorderLayout.CENTER);

        // ── Border ──
        p.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(
                new Color(30, 60, 110), 1),
            new javax.swing.border.EmptyBorder(50, 60, 40, 60)));

        add(p);
        setSize(460, 320);
        setLocationRelativeTo(null);

        // ── Animate ──
        setVisible(true);
        String[] msgs = {
            "Connecting to database...",
            "Loading modules...",
            "Setting up UI...",
            "Almost ready..."
        };
        int[] v = {0};
        Timer t = new Timer(25, null);
        t.addActionListener(e -> {
            v[0] += 2;
            if (v[0] > 100) v[0] = 100;
            bar.setValue(v[0]);

            // Color change as fills
            float ratio = v[0] / 100f;
            int r2 = (int)(30  + ratio * 63);
            int g2 = (int)(77  + ratio * 125);
            int b2 = (int)(153 + ratio * 12);
            bar.setForeground(new Color(r2, g2, b2));

            // Message update
            loading.setText(msgs[Math.min(v[0] / 25, 3)]);

            if (v[0] >= 100) {
                t.stop();
                loading.setText("Ready!");
                loading.setForeground(new Color(93, 202, 165));

                // 500ms baad login open karo
                Timer open = new Timer(500, ev -> {
                    dispose();
                    new LoginDialog();
                });
                open.setRepeats(false);
                open.start();
            }
        });
        t.start();
    }
}   