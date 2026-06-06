package hrms;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginDialog extends JDialog {

    static String ADMIN_USER = "admin";
    static String ADMIN_PASS = "admin123";

    // ── Color palette ──────────────────────────────────
    static final Color C_BG_DEEP    = new Color(7,  17, 31);
    static final Color C_BG_LEFT    = new Color(12, 28, 52);
    static final Color C_BG_CARD    = new Color(15, 29, 54);
    static final Color C_ACCENT1    = new Color(30, 77,153);
    static final Color C_ACCENT2    = new Color(39, 98,192);
    static final Color C_BORDER_DIM = new Color(40, 65,110);
    static final Color C_BLUE_GLOW  = new Color(99,153,255);
    static final Color C_WHITE      = Color.WHITE;
    static final Color C_TEXT_DIM   = new Color(130,155,200);
    static final Color C_TEXT_MUTED = new Color(70, 95,140);
    static final Color C_ERROR      = new Color(220, 80, 80);
    static final Color C_SUCCESS    = new Color(93, 202,165);
    static final Color C_DOT_BLUE   = new Color(126,179,255);
    static final Color C_DOT_TEAL   = new Color(93, 202,165);
    static final Color C_DOT_AMBER  = new Color(239,159, 39);
    static final Color C_DOT_PURPLE = new Color(175,169,236);

    // ── Widgets ─────────────────────────────────────────
    JTextField     txtUser;
    JPasswordField txtPass;
    JButton        eyeBtn;
    JLabel         lblError;
    JButton        btnLogin;
    JProgressBar   progressBar;
    JPanel         formPanel;
    JPanel         successPanel;
    boolean        passVisible = false;

    // ════════════════════════════════════════════════════
    public LoginDialog() {
        setTitle("HRMS \u2014 Sign In");
        setSize(720, 470);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        buildUI();
        setVisible(true);
    }

    void buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0,0,C_BG_DEEP, getWidth(),getHeight(),new Color(13,30,56));
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        root.add(buildLeft(),   BorderLayout.WEST);
        root.add(buildRight(),  BorderLayout.CENTER);
        add(root);
    }

    // ─────────────── LEFT PANEL ──────────────────────────
    JPanel buildLeft() {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,new Color(12,28,52), 0,getHeight(),new Color(8,20,38));
                g.setPaint(gp); g.fillRect(0,0,getWidth(),getHeight());
                // dot grid
                g.setColor(new Color(255,255,255,10));
                for(int x=18;x<getWidth();x+=26)
                    for(int y=18;y<getHeight();y+=26)
                        g.fillOval(x,y,2,2);
                // glow top-right
                g.setColor(new Color(30,77,153,25));
                g.fillOval(160,-60,200,200);
                // glow bottom-left
                g.setColor(new Color(93,202,165,15));
                g.fillOval(-60,300,200,200);
                // right border
                g.setColor(new Color(40,65,110,120));
                g.drawLine(getWidth()-1,0,getWidth()-1,getHeight());
            }
        };
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(285,470));
        p.setBorder(new EmptyBorder(30,26,22,22));

        // Icon box
        p.add(makeIconBox());
        p.add(Box.createVerticalStrut(14));

        // Brand
        JLabel brand = new JLabel("HRMS Portal");
        brand.setFont(new Font("Arial",Font.BOLD,20));
        brand.setForeground(C_WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(brand);
        p.add(Box.createVerticalStrut(4));

        JLabel bsub = new JLabel("<html><font color='#46607f' size='2'>Human Resource Management System</font></html>");
        bsub.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(bsub);
        p.add(Box.createVerticalStrut(18));

        // Stats row
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT,7,0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(265,55));
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.add(makeStatPill("6",   "Modules"));
        stats.add(makeStatPill("5",   "Tables"));
        stats.add(makeStatPill("SQL", "Live Log"));
        p.add(stats);
        p.add(Box.createVerticalStrut(16));

        // Divider
        JPanel div = new JPanel(); div.setOpaque(false);
        div.setBackground(new Color(40,65,110));
        div.setMaximumSize(new Dimension(233,1));
        div.setPreferredSize(new Dimension(233,1));
        div.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(40,65,110)));
        div.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(div);
        p.add(Box.createVerticalStrut(14));

        // Modules
        String[] names  = {"Employee Management","Department & Salaries","Leave & Attendance","Reports & Analytics"};
        Color[]  dots   = {C_DOT_BLUE,C_DOT_TEAL,C_DOT_AMBER,C_DOT_PURPLE};
        for(int i=0;i<names.length;i++){
            p.add(makeModRow(names[i],dots[i]));
            p.add(Box.createVerticalStrut(7));
        }

        p.add(Box.createVerticalGlue());

        // Version badge
        JLabel ver = new JLabel("  v2.0  \u00B7  Java JDBC + SQL Server  ") {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(255,255,255,12));
                g.fillRoundRect(0,0,getWidth(),getHeight(),5,5);
                g.setColor(new Color(40,65,110)); g.setStroke(new BasicStroke(0.6f));
                g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,5,5);
                super.paintComponent(g);
            }
        };
        ver.setFont(new Font("Courier New",Font.PLAIN,10));
        ver.setForeground(C_TEXT_MUTED);
        ver.setOpaque(false);
        ver.setBorder(new EmptyBorder(3,0,3,0));
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(ver);
        return p;
    }

    JPanel makeIconBox() {
        JPanel box = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,new Color(26,58,110),getWidth(),getHeight(),new Color(20,44,90));
                g.setPaint(gp); g.fillRoundRect(0,0,getWidth(),getHeight(),13,13);
                g.setColor(new Color(99,153,255,70)); g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,13,13);
                // inner glow
                g.setColor(new Color(99,153,255,18));
                g.fillRoundRect(4,4,getWidth()-8,getHeight()/2,9,9);
            }
        };
        box.setOpaque(false);
        box.setPreferredSize(new Dimension(52,52));
        box.setMaximumSize(new Dimension(52,52));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ic = new JLabel("HR");
        ic.setFont(new Font("Arial",Font.BOLD,16));
        ic.setForeground(C_DOT_BLUE);
        box.add(ic);
        return box;
    }

    JPanel makeStatPill(String num, String lbl) {
        JPanel pill = new JPanel() {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(255,255,255,12));
                g.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g.setColor(new Color(255,255,255,20)); g.setStroke(new BasicStroke(0.6f));
                g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
            }
        };
        pill.setOpaque(false);
        pill.setLayout(new BoxLayout(pill,BoxLayout.Y_AXIS));
        pill.setBorder(new EmptyBorder(7,10,7,10));
        pill.setPreferredSize(new Dimension(68,50));
        JLabel n=new JLabel(num); n.setFont(new Font("Arial",Font.BOLD,16)); n.setForeground(C_DOT_BLUE);
        JLabel l=new JLabel(lbl); l.setFont(new Font("Arial",Font.PLAIN,9)); l.setForeground(C_TEXT_MUTED);
        pill.add(n); pill.add(l);
        return pill;
    }

    JPanel makeModRow(String name, Color dot) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(250,17));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel d = new JPanel() {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(dot); g.fillOval(0,3,7,7);
            }
        };
        d.setOpaque(false); d.setPreferredSize(new Dimension(7,13));
        JLabel t=new JLabel(name); t.setFont(new Font("Arial",Font.PLAIN,11)); t.setForeground(C_TEXT_DIM);
        row.add(d); row.add(t);
        return row;
    }

    // ─────────────── RIGHT PANEL ─────────────────────────
    JPanel buildRight() {
        JPanel outer = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                GradientPaint gp=new GradientPaint(0,0,new Color(8,15,30),0,getHeight(),new Color(11,22,40));
                g.setPaint(gp); g.fillRect(0,0,getWidth(),getHeight());
            }
        };

        // Card
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,new Color(15,29,54),getWidth(),getHeight(),new Color(11,21,42));
                g.setPaint(gp); g.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                g.setColor(new Color(99,153,255,55)); g.setStroke(new BasicStroke(0.8f));
                g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,16,16);
                // top glow strip
                GradientPaint strip=new GradientPaint(0,0,new Color(30,77,153,60),getWidth(),0,new Color(93,202,165,20));
                g.setPaint(strip); g.fillRoundRect(0,0,getWidth(),4,16,16);
                g.fillRect(0,2,getWidth(),2);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(26,26,22,26));
        card.setPreferredSize(new Dimension(300,360));

        // Form state
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel,BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel heading = new JLabel("Welcome back");
        heading.setFont(new Font("Arial",Font.BOLD,19));
        heading.setForeground(C_WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(heading);
        formPanel.add(Box.createVerticalStrut(3));

        JLabel subh = new JLabel("Sign in to your HRMS account");
        subh.setFont(new Font("Arial",Font.PLAIN,11));
        subh.setForeground(C_TEXT_MUTED);
        subh.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(subh);
        formPanel.add(Box.createVerticalStrut(20));

        // Username
        formPanel.add(fieldLbl("USERNAME"));
        formPanel.add(Box.createVerticalStrut(5));
        txtUser = new JTextField();
        styleField(txtUser);
        formPanel.add(txtUser);
        formPanel.add(Box.createVerticalStrut(12));

        // Password
        formPanel.add(fieldLbl("PASSWORD"));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(buildPassRow());

        // Error
        lblError = new JLabel("  Incorrect username or password");
        lblError.setFont(new Font("Arial",Font.PLAIN,11));
        lblError.setForeground(C_ERROR);
        lblError.setVisible(false);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(lblError);

        // Progress bar (loading animation)
        progressBar = new JProgressBar(0,100);
        progressBar.setVisible(false);
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(20,35,65));
        progressBar.setForeground(C_ACCENT1);
        progressBar.setMaximumSize(new Dimension(248,3));
        progressBar.setPreferredSize(new Dimension(248,3));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(progressBar);
        formPanel.add(Box.createVerticalStrut(6));

        // Login button
        btnLogin = new JButton("  Sign In  \u2192") {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1=getModel().isRollover()?new Color(39,98,192):C_ACCENT1;
                Color c2=getModel().isRollover()?new Color(50,120,220):new Color(39,98,192);
                GradientPaint gp=new GradientPaint(0,0,c1,getWidth(),0,c2);
                g.setPaint(gp); g.fillRoundRect(0,0,getWidth(),getHeight(),9,9);
                // shine
                g.setColor(new Color(255,255,255,18));
                g.fillRoundRect(0,0,getWidth(),getHeight()/2,9,9);
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Arial",Font.BOLD,13));
        btnLogin.setForeground(C_WHITE);
        btnLogin.setOpaque(false); btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false); btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(248,38));
        btnLogin.setPreferredSize(new Dimension(248,38));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalStrut(12));

        // Divider
        formPanel.add(makeDivider());
        formPanel.add(Box.createVerticalStrut(10));

        // Change password
        JButton btnChange = makeSecBtn("  Change Password");
        formPanel.add(btnChange);
        formPanel.add(Box.createVerticalStrut(12));

        JLabel hint = new JLabel("  Contact administrator for credentials");
        hint.setFont(new Font("Arial",Font.ITALIC,10));
        hint.setForeground(new Color(45,70,105));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(hint);

        card.add(formPanel);

        // Success state (hidden initially)
        successPanel = buildSuccessPanel();
        successPanel.setVisible(false);
        card.add(successPanel);

        outer.add(card);

        // Actions
        btnLogin.addActionListener(e -> doLogin());
        txtPass.addActionListener(e -> doLogin());
        txtUser.addActionListener(e -> txtPass.requestFocus());
        btnChange.addActionListener(e -> showChangePassword());

        return outer;
    }

    JPanel buildSuccessPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20,0,20,0));

        // Success circle
        JPanel circle = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,new Color(15,110,86),getWidth(),getHeight(),new Color(29,158,117));
                g.setPaint(gp); g.fillOval(0,0,getWidth()-1,getHeight()-1);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(60,60));
        circle.setMaximumSize(new Dimension(60,60));
        circle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel tick = new JLabel("\u2713");
        tick.setFont(new Font("Arial",Font.BOLD,26));
        tick.setForeground(Color.WHITE);
        circle.add(tick);
        p.add(circle);
        p.add(Box.createVerticalStrut(14));

        JLabel ok = new JLabel("Authenticated!");
        ok.setFont(new Font("Arial",Font.BOLD,16));
        ok.setForeground(C_SUCCESS);
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(ok);
        p.add(Box.createVerticalStrut(5));

        JLabel opening = new JLabel("Opening HRMS Portal...");
        opening.setFont(new Font("Arial",Font.PLAIN,11));
        opening.setForeground(C_TEXT_MUTED);
        opening.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(opening);

        return p;
    }

    JLabel fieldLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial",Font.BOLD,10));
        l.setForeground(C_TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    void styleField(JTextField tf) {
        tf.setFont(new Font("Arial",Font.PLAIN,13));
        tf.setForeground(C_WHITE);
        tf.setBackground(new Color(25,45,80));
        tf.setCaretColor(C_DOT_BLUE);
        tf.setOpaque(true);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER_DIM,1,true),
            new EmptyBorder(7,12,7,12)));
        tf.setMaximumSize(new Dimension(248,36));
        tf.setPreferredSize(new Dimension(248,36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.addFocusListener(new FocusAdapter(){
            Border focus = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99,153,255,180),1,true),
                new EmptyBorder(7,12,7,12));
            Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER_DIM,1,true),
                new EmptyBorder(7,12,7,12));
            public void focusGained(FocusEvent e){tf.setBorder(focus);tf.setBackground(new Color(20,38,72));}
            public void focusLost(FocusEvent e) {tf.setBorder(normal);tf.setBackground(new Color(25,45,80));}
        });
    }

    JPanel buildPassRow() {
        txtPass = new JPasswordField();
        txtPass.setEchoChar('\u2022');
        styleField(txtPass);
        txtPass.setMaximumSize(new Dimension(248,36));
        txtPass.setPreferredSize(new Dimension(248,36));

        JPanel wrap = new JPanel(null);
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(248,36));
        wrap.setPreferredSize(new Dimension(248,36));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPass.setBounds(0,0,248,36);
        wrap.add(txtPass);

        eyeBtn = new JButton(passVisible?"hide":"show");
        eyeBtn.setFont(new Font("Arial",Font.PLAIN,9));
        eyeBtn.setForeground(C_TEXT_MUTED);
        eyeBtn.setOpaque(false); eyeBtn.setContentAreaFilled(false);
        eyeBtn.setBorderPainted(false); eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeBtn.setBounds(212,10,34,16);
        eyeBtn.setToolTipText("Toggle password visibility");
        eyeBtn.addActionListener(e->{
            passVisible=!passVisible;
            txtPass.setEchoChar(passVisible?(char)0:'\u2022');
            eyeBtn.setText(passVisible?"hide":"show");
        });
        wrap.add(eyeBtn);
        return wrap;
    }

    JPanel makeDivider() {
        JPanel d = new JPanel(new BorderLayout(8,0));
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(248,14));
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSeparator l=new JSeparator(); l.setForeground(C_BORDER_DIM); l.setPreferredSize(new Dimension(95,1));
        JSeparator r=new JSeparator(); r.setForeground(C_BORDER_DIM); r.setPreferredSize(new Dimension(95,1));
        JLabel mid=new JLabel("or",SwingConstants.CENTER);
        mid.setFont(new Font("Arial",Font.PLAIN,10)); mid.setForeground(C_TEXT_MUTED);
        d.add(l,BorderLayout.WEST); d.add(mid,BorderLayout.CENTER); d.add(r,BorderLayout.EAST);
        return d;
    }

    JButton makeSecBtn(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(getModel().isRollover()){g.setColor(new Color(255,255,255,7));g.fillRoundRect(0,0,getWidth(),getHeight(),9,9);}
                g.setColor(C_BORDER_DIM); g.setStroke(new BasicStroke(0.7f));
                g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,9,9);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial",Font.PLAIN,12));
        btn.setForeground(C_TEXT_DIM);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(248,34));
        btn.setPreferredSize(new Dimension(248,34));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){btn.setForeground(C_WHITE);}
            public void mouseExited(MouseEvent e) {btn.setForeground(C_TEXT_DIM);}
        });
        return btn;
    }

    // ─────────────── LOGIN LOGIC ─────────────────────────
    void doLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();
        lblError.setVisible(false);

        if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
            btnLogin.setEnabled(false);
            progressBar.setValue(0);
            progressBar.setVisible(true);

            // Animated progress bar
            Timer loader = new Timer(20, null);
            loader.addActionListener(new ActionListener(){
                int v=0;
                public void actionPerformed(ActionEvent e){
                    v+=3;
                    // gradient color change as it fills
                    float ratio = v/100f;
                    int r=(int)(30+ratio*(93-30));
                    int g2=(int)(77+ratio*(202-77));
                    int b=(int)(153+ratio*(165-153));
                    progressBar.setForeground(new Color(r,g2,b));
                    progressBar.setValue(Math.min(v,100));
                    if(v>=100){
                        loader.stop();
                        // show success screen
                        formPanel.setVisible(false);
                        successPanel.setVisible(true);
                        Timer open = new Timer(800, ev->{dispose(); new HRMSMain();});
                        open.setRepeats(false); open.start();
                    }
                }
            });
            loader.start();

        } else {
            lblError.setVisible(true);
            // Red border flash on password
            Border redBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_ERROR,1,true),
                new EmptyBorder(7,12,7,12));
            txtPass.setBorder(redBorder);
            txtPass.setBackground(new Color(60,15,15));

            Timer reset = new Timer(1500, ev->{
                lblError.setVisible(false);
                styleField(txtPass);
            });
            reset.setRepeats(false); reset.start();
            txtPass.setText("");
            txtPass.requestFocus();
        }
    }

    // ─────────────── CHANGE PASSWORD ─────────────────────
    void showChangePassword() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10,14,10,14));
        GridBagConstraints g = new GridBagConstraints();
        g.insets=new Insets(5,5,5,5); g.fill=GridBagConstraints.HORIZONTAL;

        JPasswordField fOld  = new JPasswordField(15);
        JPasswordField fNew1 = new JPasswordField(15);
        JPasswordField fNew2 = new JPasswordField(15);
        String[]        lbls  = {"Current password:","New password:","Confirm new password:"};
        JPasswordField[] flds  = {fOld,fNew1,fNew2};

        for(int i=0;i<3;i++){
            g.gridx=0;g.gridy=i;g.weightx=0.45;
            JLabel l=new JLabel(lbls[i]);l.setFont(new Font("Arial",Font.PLAIN,12));panel.add(l,g);
            g.gridx=1;g.weightx=0.55; panel.add(flds[i],g);
        }

        int res=JOptionPane.showConfirmDialog(this,panel,"Change Password",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(res!=JOptionPane.OK_OPTION) return;

        String oldP =new String(fOld.getPassword());
        String newP1=new String(fNew1.getPassword());
        String newP2=new String(fNew2.getPassword());

        if(!oldP.equals(ADMIN_PASS))
            JOptionPane.showMessageDialog(this,"Current password incorrect!","Error",JOptionPane.ERROR_MESSAGE);
        else if(newP1.isEmpty())
            JOptionPane.showMessageDialog(this,"New password cannot be empty!","Error",JOptionPane.ERROR_MESSAGE);
        else if(!newP1.equals(newP2))
            JOptionPane.showMessageDialog(this,"Passwords do not match!","Error",JOptionPane.ERROR_MESSAGE);
        else{
            ADMIN_PASS=newP1;
            JOptionPane.showMessageDialog(this,"Password changed successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}