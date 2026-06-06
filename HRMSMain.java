package hrms;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.sql.*;
import java.time.*;
import java.time.format.*;

public class HRMSMain extends JFrame {
    // ==================== COLORS ====================
    static Color C_SIDEBAR  = new Color(25, 42, 86);
    static Color C_HEADER   = new Color(35, 55, 110);
    static Color C_ACTIVE   = new Color(46, 117, 182);
    static Color C_BG       = new Color(240, 244, 252);
    static Color C_WHITE    = Color.WHITE;
    static Color C_TEXT     = new Color(30, 40, 70);
    static Color C_GREEN    = new Color(0, 140, 90);
    static Color C_RED      = new Color(190, 40, 40);
    static Color C_ORANGE   = new Color(180, 90, 0);
    static Color C_SQL_BG   = new Color(18, 24, 40);
    static Color C_SQL_TEXT = new Color(100, 220, 120);

    static final Font F_LABEL = new Font("Arial", Font.PLAIN, 12);
    static final Font F_BTN   = new Font("Arial", Font.BOLD, 12);
    static final Font F_SQL   = new Font("Courier New", Font.PLAIN, 11);

    boolean darkMode = false;

    JPanel   pContent;
    JLabel   lblPageTitle, lblDateTime;
    JButton[] sideButtons;
    JButton  btnDarkToggle;
    JPanel   headerPanel, sidebarPanel;
    Timer    clockTimer;

    JTextArea sqlLogArea;
    JPanel    sqlLogPanel;

    String[] modules = {
        "   Home","   Employees","   Departments",
        "   Salaries","   Leave","   Attendance","   Reports"
    };

    // ==================== ENTRY POINT ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SplashScreen());
    }

    public HRMSMain() {
        setTitle("HRMS - Human Resource Management System");
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { confirmExit(); }
        });
        buildUI();
        showHome();
        setVisible(true);
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
    }

    void confirmExit() {
        int c = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit HRMS?",
            "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (c == JOptionPane.YES_OPTION) { if (clockTimer!=null) clockTimer.stop(); System.exit(0); }
    }

    void updateClock() {
        if (lblDateTime != null)
            lblDateTime.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy  hh:mm:ss a")) + "  ");
    }

    void toggleDarkMode() {
        darkMode = !darkMode;
        if (darkMode) {
            C_SIDEBAR=new Color(15,15,20); C_HEADER=new Color(20,20,30);
            C_BG=new Color(28,32,42); C_WHITE=new Color(35,40,55); C_TEXT=new Color(210,220,240);
            btnDarkToggle.setText("  Light Mode");
        } else {
            C_SIDEBAR=new Color(25,42,86); C_HEADER=new Color(35,55,110);
            C_BG=new Color(240,244,252); C_WHITE=Color.WHITE; C_TEXT=new Color(30,40,70);
            btnDarkToggle.setText("  Dark Mode");
        }
        headerPanel.setBackground(C_HEADER);
        sidebarPanel.setBackground(C_SIDEBAR);
        for (JButton b : sideButtons) if (!b.getBackground().equals(C_ACTIVE)) b.setBackground(C_SIDEBAR);
        pContent.setBackground(C_BG);
        int ai=0; for (int i=0;i<sideButtons.length;i++) if (sideButtons[i].getBackground().equals(C_ACTIVE)){ai=i;break;}
        sqlLogPanel.setBackground(C_SQL_BG);
        sqlLogArea.setBackground(C_SQL_BG);
        pContent.setBackground(C_BG);
        pContent.repaint();
        sideButtons[ai].doClick();
    }

    // ==================== BUILD UI ====================
    void buildUI() {
        setLayout(new BorderLayout());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(C_HEADER);
        headerPanel.setPreferredSize(new Dimension(1080,50));
        headerPanel.setBorder(new EmptyBorder(0,20,0,10));
        JLabel appTitle = new JLabel("HRMS - Human Resource Management System");
        appTitle.setFont(new Font("Arial",Font.BOLD,16)); appTitle.setForeground(Color.WHITE);
        headerPanel.add(appTitle, BorderLayout.WEST);

        JPanel hRight = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,10));
        hRight.setOpaque(false);
        lblDateTime = new JLabel(); lblDateTime.setFont(new Font("Arial",Font.PLAIN,12));
        lblDateTime.setForeground(new Color(180,210,255)); updateClock();
        hRight.add(lblDateTime);

        btnDarkToggle = new JButton("  Dark Mode");
        btnDarkToggle.setFont(new Font("Arial",Font.BOLD,11));
        btnDarkToggle.setBackground(new Color(60,80,150)); btnDarkToggle.setForeground(Color.WHITE);
        btnDarkToggle.setBorderPainted(false); btnDarkToggle.setFocusPainted(false);
        btnDarkToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDarkToggle.setPreferredSize(new Dimension(130,30));
        btnDarkToggle.addActionListener(e -> toggleDarkMode());
        hRight.add(btnDarkToggle);

        lblPageTitle = new JLabel("Home  ");
        lblPageTitle.setFont(new Font("Arial",Font.PLAIN,13));
        lblPageTitle.setForeground(new Color(160,190,255));
        hRight.add(lblPageTitle);
        headerPanel.add(hRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel,BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(C_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(185,630));

        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.CENTER,0,14));
        logoArea.setBackground(new Color(18,30,65)); logoArea.setMaximumSize(new Dimension(185,55));
        JLabel logo = new JLabel("HRMS Portal");
        logo.setFont(new Font("Arial",Font.BOLD,14)); logo.setForeground(Color.WHITE);
        logoArea.add(logo); sidebarPanel.add(logoArea); sidebarPanel.add(Box.createVerticalStrut(8));

        sideButtons = new JButton[modules.length];
        for (int i=0;i<modules.length;i++) {
            final int idx=i;
            JButton btn = new JButton(modules[i]);
            btn.setFont(F_BTN); btn.setForeground(new Color(180,200,240)); btn.setBackground(C_SIDEBAR);
            btn.setBorderPainted(false); btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(185,42)); btn.setPreferredSize(new Dimension(185,42));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setBorder(new EmptyBorder(0,18,0,0));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){if(!btn.getBackground().equals(C_ACTIVE))btn.setBackground(new Color(40,65,130));}
                public void mouseExited(MouseEvent e) {if(!btn.getBackground().equals(C_ACTIVE))btn.setBackground(C_SIDEBAR);}
            });
            btn.addActionListener(e -> {
                setActive(idx);
                switch(idx){case 0:showHome();break;case 1:showEmployees();break;case 2:showDepartments();break;
                    case 3:showSalaries();break;case 4:showLeave();break;case 5:showAttendance();break;case 6:showReports();break;}
            });
            sideButtons[i]=btn; sidebarPanel.add(btn); sidebarPanel.add(Box.createVerticalStrut(2));
        }
        sidebarPanel.add(Box.createVerticalGlue());

        JButton btnAbout = makeSideBtn("   About");
        btnAbout.addActionListener(e -> showAboutDialog());
        sidebarPanel.add(btnAbout); sidebarPanel.add(Box.createVerticalStrut(2));

        JButton btnExit = makeSideBtn("   Exit");
        btnExit.setForeground(new Color(255,120,120));
        btnExit.addActionListener(e -> confirmExit());
        sidebarPanel.add(btnExit); sidebarPanel.add(Box.createVerticalStrut(10));
        add(sidebarPanel, BorderLayout.WEST);

        pContent = new JPanel(new BorderLayout());
        pContent.setBackground(C_BG);
        add(pContent, BorderLayout.CENTER);
        add(buildSqlLogPanel(), BorderLayout.SOUTH);
    }

    JButton makeSideBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(F_BTN); btn.setForeground(new Color(180,200,240)); btn.setBackground(C_SIDEBAR);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(185,42)); btn.setBorder(new EmptyBorder(0,18,0,0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); return btn;
    }

    void showAboutDialog() {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10,20,10,20)); panel.setBackground(C_WHITE);
        String[][] info = {
            {"Project","HRMS - Human Resource Management System"},
            {"Version","2.0"},
            {"Platform","Java Swing + SQL Server"},
            {"Modules","Employees, Departments, Salaries, Leave, Attendance"},
            {"Course","Database Management Systems"},
            {"Developer","Hafsa Ashraf — 01-132232-052"},
            {"Submitted To","Sir Daniyal"},
            {"Date","May 2026"}
        };
        for (String[] row : info) {
            JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT,5,3)); line.setBackground(C_WHITE);
            JLabel k=new JLabel(row[0]+":  "); k.setFont(new Font("Arial",Font.BOLD,12)); k.setForeground(C_ACTIVE);
            JLabel v=new JLabel(row[1]); v.setFont(new Font("Arial",Font.PLAIN,12)); v.setForeground(C_TEXT);
            line.add(k); line.add(v); panel.add(line);
        }
        JOptionPane.showMessageDialog(this,panel,"About HRMS",JOptionPane.INFORMATION_MESSAGE);
    }

    void setActive(int idx) {
        for (int i = 0; i < sideButtons.length; i++) {
            sideButtons[i].setBackground(i==idx ? C_ACTIVE : C_SIDEBAR);
            sideButtons[i].setForeground(i==idx ? Color.WHITE : new Color(180,200,240));
            String moduleName = modules[i].trim();
            if (i == idx) {
                sideButtons[i].setText("  *  " + moduleName);
            } else {
                sideButtons[i].setText("     " + moduleName);
            }
        }
        lblPageTitle.setText(modules[idx].trim() + "  ");
    }

    // ==================== HELPERS ====================
    JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g2) {
                Graphics2D g = (Graphics2D) g2;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isRollover() ? bg.brighter() : bg;
                g.setColor(base);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g.setColor(new Color(255,255,255,30));
                g.fillRoundRect(0, 0, getWidth(), getHeight()/2, 18, 18);
                super.paintComponent(g);
            }
        };
        b.setFont(F_BTN); b.setForeground(Color.WHITE); b.setOpaque(false);
        b.setContentAreaFilled(false); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setPreferredSize(new Dimension(130,32));
        return b;
    }

    JTextField makeTxt(int w) {
        JTextField t=new JTextField(); t.setFont(F_LABEL); t.setPreferredSize(new Dimension(w,30));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150,180,220),1,true),
            new EmptyBorder(3,8,3,8)));
        t.setBackground(darkMode ? new Color(40,45,65) : new Color(250,252,255));
        t.setForeground(darkMode ? new Color(210,220,240) : Color.BLACK);
        return t;
    }

    JLabel makeLbl(String text) { JLabel l=new JLabel(text); l.setFont(F_LABEL); l.setForeground(C_TEXT); return l; }

    JScrollPane makeTable(String[] cols) {
        DefaultTableModel m=new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable t = new JTable(m) {
            int hoveredRow = -1;
            {
                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoveredRow) { hoveredRow = row; repaint(); }
                    }
                });
                addMouseListener(new MouseAdapter() {
                    public void mouseExited(MouseEvent e) { hoveredRow = -1; repaint(); }
                });
            }
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    if (row == hoveredRow) {
                        c.setBackground(darkMode ? new Color(40,60,100) : new Color(210,230,255));
                    } else {
                        c.setBackground(row % 2 == 0 ? C_WHITE : (darkMode ? new Color(40,44,58) : new Color(236,242,252)));
                    }
                    c.setForeground(C_TEXT);
                }
                return c;
            }
        };
        t.setFont(F_LABEL); t.setRowHeight(30);
        t.setGridColor(new Color(220,228,245)); t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(46,117,182,80)); t.setSelectionForeground(C_TEXT);
        t.getTableHeader().setFont(new Font("Arial",Font.BOLD,12));
        t.getTableHeader().setBackground(new Color(35,55,110));
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setPreferredSize(new Dimension(0,34));
        t.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(new Color(190,210,240)));
        sp.getViewport().setBackground(C_WHITE);
        return sp;
    }

    DefaultTableModel getModel(JScrollPane sp){return (DefaultTableModel)((JTable)sp.getViewport().getView()).getModel();}
    JTable getTable(JScrollPane sp){return (JTable)sp.getViewport().getView();}

    JPanel makeSQLPanel(String query) {
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(C_SQL_BG);
        p.setBorder(new EmptyBorder(5,10,5,10)); p.setPreferredSize(new Dimension(800,55));
        JLabel lbl=new JLabel("  \u25BA Last Executed SQL:"); lbl.setFont(new Font("Consolas",Font.BOLD,10)); lbl.setForeground(new Color(80,160,255));
        p.add(lbl,BorderLayout.NORTH);
        JTextArea ta=new JTextArea(query); ta.setFont(F_SQL); ta.setForeground(C_SQL_TEXT);
        ta.setBackground(C_SQL_BG); ta.setEditable(false); ta.setBorder(new EmptyBorder(1,4,1,4));
        p.add(ta,BorderLayout.CENTER); return p;
    }

    JPanel buildSqlLogPanel() {
        sqlLogPanel = new JPanel(new BorderLayout());
        sqlLogPanel.setBackground(C_SQL_BG);
        sqlLogPanel.setPreferredSize(new Dimension(1080, 62));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT,10,3));
        header.setBackground(new Color(12,18,32));
        JLabel dot = new JLabel("\u25CF"); dot.setForeground(new Color(0,220,100)); dot.setFont(new Font("Arial",Font.BOLD,10));
        JLabel title = new JLabel("LIVE SQL LOG — Backend Queries"); title.setFont(new Font("Consolas",Font.BOLD,11)); title.setForeground(new Color(80,160,255));
        header.add(dot); header.add(title);
        sqlLogPanel.add(header, BorderLayout.NORTH);
        sqlLogArea = new JTextArea("  Waiting for query...");
        sqlLogArea.setFont(new Font("Consolas",Font.PLAIN,11));
        sqlLogArea.setForeground(new Color(100,220,120));
        sqlLogArea.setBackground(C_SQL_BG);
        sqlLogArea.setEditable(false);
        sqlLogArea.setBorder(new EmptyBorder(2,10,2,10));
        sqlLogPanel.add(sqlLogArea, BorderLayout.CENTER);
        return sqlLogPanel;
    }

    void logSQL(String sql) {
        if(sqlLogArea==null) return;
        sqlLogArea.setForeground(new Color(100,220,120));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String[] chars = ("["+timestamp+"]  "+sql).split("");
        final int[] i = {0};
        Timer t = new Timer(18, null);
        t.addActionListener(e2 -> {
            if(i[0] < chars.length){
                if(i[0]==0) sqlLogArea.setText("");
                sqlLogArea.append(chars[i[0]++]);
            } else { t.stop(); }
        });
        t.start();
    }

    JPanel makeTwoRowForm(JPanel f,JPanel b) {
        JPanel w=new JPanel(); w.setLayout(new BoxLayout(w,BoxLayout.Y_AXIS)); w.setBackground(C_WHITE);
        w.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200,215,240)),new EmptyBorder(6,8,6,8)));
        f.setBackground(C_WHITE); b.setBackground(C_WHITE);
        f.setAlignmentX(Component.LEFT_ALIGNMENT); b.setAlignmentX(Component.LEFT_ALIGNMENT);
        w.add(f); w.add(Box.createVerticalStrut(4)); w.add(b); return w;
    }

    JLabel makeStatusBar(){
        JLabel l=new JLabel("  Total Records: 0"); l.setFont(new Font("Arial",Font.PLAIN,11));
        l.setBorder(new EmptyBorder(4,6,4,6));
        l.setOpaque(true);
        l.setBackground(darkMode ? new Color(30,35,50) : new Color(225,232,248));
        l.setForeground(darkMode ? new Color(180,190,220) : new Color(100,120,160));
        return l;
    }

    void updateStatus(JLabel l,JScrollPane sp){l.setText("  Total Records: "+getModel(sp).getRowCount());}

    JPanel makeSouth(String sql,JLabel s){
        JPanel p=new JPanel(new BorderLayout()); p.add(makeSQLPanel(sql),BorderLayout.CENTER); p.add(s,BorderLayout.SOUTH); return p;
    }

    DefaultTableCellRenderer makeStatusRenderer(){
        return new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                if(!sel){String val=String.valueOf(v);
                    if(val.equals("Approved")||val.equals("Present"))setForeground(new Color(0,140,90));
                    else if(val.equals("Rejected")||val.equals("Absent"))setForeground(new Color(190,40,40));
                    else setForeground(new Color(180,90,0));
                    setFont(new Font("Arial",Font.BOLD,12));}
                return this;
            }
        };
    }

    void addTooltip(JButton btn, String tip){ btn.setToolTipText(tip); }

    void exportToCSV(JScrollPane sp, String filename) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(filename+".csv"));
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files","csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        if (!file.getName().endsWith(".csv")) file = new File(file.getAbsolutePath()+".csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            DefaultTableModel m = getModel(sp);
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<m.getColumnCount();i++){
                if(i>0)sb.append(",");
                sb.append("\"").append(m.getColumnName(i)).append("\"");
            }
            pw.println(sb.toString());
            for (int r=0;r<m.getRowCount();r++){
                sb = new StringBuilder();
                for (int c=0;c<m.getColumnCount();c++){
                    if(c>0)sb.append(",");
                    Object val = m.getValueAt(r,c);
                    sb.append("\"").append(val!=null?val.toString():"").append("\"");
                }
                pw.println(sb.toString());
            }
            JOptionPane.showMessageDialog(this,"Data exported successfully!\nFile saved to: "+file.getAbsolutePath(),"Export Successful",JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Export failed: "+ex.getMessage(),"Export Error",JOptionPane.ERROR_MESSAGE); }
    }

    // ==================== HOME ====================
    void showHome() {
        pContent.removeAll();
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(C_BG);

        JPanel banner = new JPanel() {
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0,0, new Color(35,55,110), getWidth(),0, new Color(46,100,160));
                g.setPaint(gp); g.fillRect(0,0,getWidth(),getHeight());
                g.setColor(new Color(255,255,255,18));
                for(int x=10;x<getWidth();x+=22)
                    for(int y=6;y<getHeight();y+=22)
                        g.fillOval(x,y,2,2);
            }
        };
        banner.setLayout(new BorderLayout());
        banner.setPreferredSize(new Dimension(800, 78));
        banner.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel bannerLeft = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        bannerLeft.setOpaque(false);

        JPanel iconBox = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(255,255,255,30)); g.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g.setColor(new Color(255,255,255,60)); g.setStroke(new java.awt.BasicStroke(1f));
                g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(46,46));
        JLabel iconLbl = new JLabel("HR");
        iconLbl.setFont(new Font("Arial",Font.BOLD,15));
        iconLbl.setForeground(Color.WHITE);
        iconBox.add(iconLbl);

        JPanel textArea = new JPanel();
        textArea.setLayout(new BoxLayout(textArea, BoxLayout.Y_AXIS));
        textArea.setOpaque(false);
        textArea.setBorder(new EmptyBorder(0,14,0,0));
        JLabel welcome = new JLabel("Welcome to HRMS Portal");
        welcome.setFont(new Font("Arial",Font.BOLD,20));
        welcome.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Human Resource Management System  —  "+
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        sub.setFont(new Font("Arial",Font.PLAIN,11));
        sub.setForeground(new Color(180,210,255));
        textArea.add(welcome);
        textArea.add(Box.createVerticalStrut(3));
        textArea.add(sub);

        bannerLeft.add(iconBox);
        bannerLeft.add(textArea);
        banner.add(bannerLeft, BorderLayout.WEST);

        JLabel liveTime = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
        liveTime.setFont(new Font("Arial",Font.BOLD,22));
        liveTime.setForeground(new Color(180,210,255));
        liveTime.setHorizontalAlignment(SwingConstants.RIGHT);
        banner.add(liveTime, BorderLayout.EAST);
        Timer bannerClock = new Timer(1000, ev ->
            liveTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))));
        bannerClock.start();

        p.add(banner, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(14, 18, 8, 18));
        content.add(buildMetricCards(), BorderLayout.NORTH);

        JPanel bottomRow = new JPanel(new BorderLayout(12, 0));
        bottomRow.setBackground(C_BG);

        JPanel chartCard = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(C_WHITE); g.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g.setColor(new Color(190,210,240)); g.setStroke(new java.awt.BasicStroke(0.7f));
                g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            }
        };
        chartCard.setOpaque(false);
        chartCard.setBorder(new EmptyBorder(10,10,10,10));

        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        JLabel chartTitle = new JLabel("  Employees per Department");
        chartTitle.setFont(new Font("Arial",Font.BOLD,12));
        chartTitle.setForeground(C_HEADER);
        JLabel chartSub = new JLabel("Live from DB  ");
        chartSub.setFont(new Font("Arial",Font.ITALIC,10));
        chartSub.setForeground(new Color(150,170,210));
        chartHeader.add(chartTitle, BorderLayout.WEST);
        chartHeader.add(chartSub, BorderLayout.EAST);
        chartCard.add(chartHeader, BorderLayout.NORTH);
        chartCard.add(buildBarChartPanel(), BorderLayout.CENTER);
        bottomRow.add(chartCard, BorderLayout.CENTER);

        JPanel sideInfo = new JPanel() {
            protected void paintComponent(Graphics g0) {
                Graphics2D g=(Graphics2D)g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(C_WHITE); g.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g.setColor(new Color(190,210,240)); g.setStroke(new java.awt.BasicStroke(0.7f));
                g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            }
        };
        sideInfo.setOpaque(false);
        sideInfo.setLayout(new BoxLayout(sideInfo, BoxLayout.Y_AXIS));
        sideInfo.setBorder(new EmptyBorder(14,16,14,16));
        sideInfo.setPreferredSize(new Dimension(200,180));

        JLabel infoTitle = new JLabel("Quick Info");
        infoTitle.setFont(new Font("Arial",Font.BOLD,13));
        infoTitle.setForeground(C_HEADER);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideInfo.add(infoTitle);
        sideInfo.add(Box.createVerticalStrut(10));

        String[] qLabels = {"Total Departments", "Pending Leaves", "Total Salary Bill"};
        String[] qVals   = {"--","--","--"};
        try(Connection con = DBConnection.getConnection()){
            ResultSet d=con.prepareStatement("SELECT COUNT(*) FROM Departments").executeQuery();
            if(d.next()) qVals[0]=String.valueOf(d.getInt(1));
            ResultSet l=con.prepareStatement("SELECT COUNT(*) FROM Leave_Mgmt WHERE Status='Pending'").executeQuery();
            if(l.next()) qVals[1]=String.valueOf(l.getInt(1));
            ResultSet s=con.prepareStatement("SELECT ISNULL(SUM(Basic_Salary+Bonus),0) FROM Salaries").executeQuery();
            if(s.next()) qVals[2]=String.format("%,.0f PKR",s.getDouble(1));
        }catch(Exception ignored){}

        Color[] qColors = {new Color(120,50,160), C_ORANGE, C_GREEN};
        for(int i=0;i<3;i++){
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(200,30));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel k=new JLabel("  "+qLabels[i]);
            k.setFont(new Font("Arial",Font.PLAIN,11));
            k.setForeground(C_TEXT);
            JLabel v=new JLabel(qVals[i]+"  ");
            v.setFont(new Font("Arial",Font.BOLD,12));
            v.setForeground(qColors[i]);
            v.setHorizontalAlignment(SwingConstants.RIGHT);
            row.add(k,BorderLayout.WEST);
            row.add(v,BorderLayout.EAST);
            sideInfo.add(row);
            sideInfo.add(Box.createVerticalStrut(6));
            JPanel div=new JPanel();
            div.setBackground(new Color(220,228,245));
            div.setMaximumSize(new Dimension(180,1));
            div.setPreferredSize(new Dimension(180,1));
            div.setAlignmentX(Component.LEFT_ALIGNMENT);
            sideInfo.add(div);
            sideInfo.add(Box.createVerticalStrut(6));
        }

        sideInfo.add(Box.createVerticalGlue());
        JLabel verLbl = new JLabel("  HRMS v2.0 — DBMS Project");
        verLbl.setFont(new Font("Arial",Font.ITALIC,10));
        verLbl.setForeground(new Color(160,175,210));
        verLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideInfo.add(verLbl);

        bottomRow.add(sideInfo, BorderLayout.EAST);
        content.add(bottomRow, BorderLayout.CENTER);
        p.add(content, BorderLayout.CENTER);

        pContent.add(p, BorderLayout.CENTER);
        pContent.revalidate();
        pContent.repaint();
    }

    JPanel buildMetricCards() {
        JPanel row = new JPanel(new java.awt.GridLayout(1,3,14,0));
        row.setBackground(C_BG);
        row.setMaximumSize(new Dimension(900,90));

        String[] titles = {"Total Employees","Avg Monthly Salary","Present Today"};
        String[] values = {"--","--","--"};
        String[] subs   = {"All registered staff","Base salary average","Marked present today"};
        Color[]  colors = {C_ACTIVE, C_GREEN, C_ORANGE};

        try(Connection con = DBConnection.getConnection()){
            ResultSet r1=con.prepareStatement("SELECT COUNT(*) FROM Employees").executeQuery();
            if(r1.next()) values[0]=String.valueOf(r1.getInt(1));
            ResultSet r2=con.prepareStatement("SELECT ISNULL(AVG(Basic_Salary),0) FROM Salaries").executeQuery();
            if(r2.next()) values[1]=String.format("%,.0f PKR",r2.getDouble(1));
            PreparedStatement ps3=con.prepareStatement("SELECT COUNT(*) FROM Attendance WHERE Att_Date=? AND Status='Present'");
            ps3.setString(1, LocalDate.now().toString());
            ResultSet r3=ps3.executeQuery();
            if(r3.next()) values[2]=String.valueOf(r3.getInt(1));
        }catch(Exception ignored){
            values[0]="N/A"; values[1]="N/A"; values[2]="N/A";
        }

        for(int i=0;i<3;i++){
            final Color cc=colors[i];
            final String cv=values[i];
            final String ct=titles[i];
            final String cs=subs[i];

            JPanel card = new JPanel() {
                protected void paintComponent(Graphics g0) {
                    Graphics2D g=(Graphics2D)g0;
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(C_WHITE); g.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                    g.setColor(cc); g.fillRoundRect(0,0,5,getHeight(),4,4);
                    g.setColor(cc); g.fillRoundRect(0,0,getWidth(),5,14,14);
                    g.fillRect(0,3,getWidth(),2);
                    g.setColor(new Color(cc.getRed(),cc.getGreen(),cc.getBlue(),60));
                    g.setStroke(new java.awt.BasicStroke(1f));
                    g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                }
            };
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(C_WHITE);
            card.setBorder(new EmptyBorder(12,18,10,14));

            JLabel lblT=new JLabel(ct); lblT.setFont(new Font("Arial",Font.BOLD,11)); lblT.setForeground(cc); lblT.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel lblV=new JLabel(cv); lblV.setFont(new Font("Arial",Font.BOLD,26)); lblV.setForeground(C_HEADER); lblV.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel lblS=new JLabel(cs); lblS.setFont(new Font("Arial",Font.PLAIN,10)); lblS.setForeground(new Color(140,155,185)); lblS.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(lblT); card.add(Box.createVerticalStrut(3)); card.add(lblV); card.add(Box.createVerticalStrut(2)); card.add(lblS);
            row.add(card);
        }
        return row;
    }

    JPanel buildBarChartPanel() {
        JPanel chart = new JPanel() {
            protected void paintComponent(Graphics g2) {
                super.paintComponent(g2);
                Graphics2D g=(Graphics2D)g2;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(C_WHITE); g.fillRect(0,0,getWidth(),getHeight());
                java.util.List<String> depts=new java.util.ArrayList<>();
                java.util.List<Integer> counts=new java.util.ArrayList<>();
                try(Connection con=DBConnection.getConnection();
                    PreparedStatement ps=con.prepareStatement(
                        "SELECT d.Dept_Name, COUNT(e.Emp_ID)\n" +
                        "FROM Departments d\n" +
                        "LEFT JOIN Employees e ON d.Dept_ID = e.Dept_ID\n" +
                        "GROUP BY d.Dept_Name");
                    ResultSet rs=ps.executeQuery()){
                    while(rs.next()){depts.add(rs.getString(1));counts.add(rs.getInt(2));}
                }catch(Exception ignored){}
                if(depts.isEmpty()){
                    g.setColor(new Color(150,160,180));g.setFont(new Font("Arial",Font.ITALIC,12));
                    g.drawString("No data available — please connect to database",50,70);return;
                }
                int maxVal=counts.stream().mapToInt(ii->ii).max().orElse(1);
                int barW=52,gap=22,startX=30,baseY=getHeight()-30;
                Color[] bc={new Color(46,117,182),new Color(0,140,90),new Color(180,90,0),new Color(120,50,160),new Color(30,120,160)};
                for(int i=0;i<depts.size();i++){
                    int barH=(int)((double)counts.get(i)/maxVal*(getHeight()-55));
                    int x=startX+i*(barW+gap);
                    g.setColor(new Color(0,0,0,18));
                    g.fillRoundRect(x+3,baseY-barH+3,barW,barH,8,8);
                    g.setColor(bc[i%bc.length]);
                    g.fillRoundRect(x,baseY-barH,barW,barH,8,8);
                    g.setColor(new Color(255,255,255,40));
                    g.fillRoundRect(x,baseY-barH,barW,barH/3,8,8);
                    g.setColor(C_TEXT);g.setFont(new Font("Arial",Font.BOLD,11));
                    g.drawString(String.valueOf(counts.get(i)),x+barW/2-5,baseY-barH-5);
                    g.setFont(new Font("Arial",Font.PLAIN,9));
                    String dept=depts.get(i).length()>7?depts.get(i).substring(0,6)+"..":depts.get(i);
                    g.drawString(dept,x+2,baseY+13);
                }
            }
        };
        chart.setBackground(C_WHITE);
        chart.setPreferredSize(new Dimension(500,140));
        return chart;
    }

    JPanel buildBarChart() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(C_BG);
        wrapper.setPreferredSize(new Dimension(750,185));
        JLabel title = new JLabel("  Employees per Department", SwingConstants.LEFT);
        title.setFont(new Font("Arial",Font.BOLD,12)); title.setForeground(C_HEADER);
        wrapper.add(title,BorderLayout.NORTH);
        JPanel chart = buildBarChartPanel();
        chart.setPreferredSize(new Dimension(750,155));
        wrapper.add(chart,BorderLayout.CENTER);
        return wrapper;
    }

    // ==================== EMPLOYEES ====================
    void showEmployees() {
        pContent.removeAll();
        JPanel p=new JPanel(new BorderLayout(0,8)); p.setBackground(C_BG); p.setBorder(new EmptyBorder(12,12,8,12));
        JPanel fr=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JTextField txtName=makeTxt(120),txtDept=makeTxt(50),txtEmpId=makeTxt(50),txtSearch=makeTxt(130);
        DatePickerField txtDate = new DatePickerField(100);
        fr.add(makeLbl("Name:"));fr.add(txtName);fr.add(makeLbl("Dept ID:"));fr.add(txtDept);
        fr.add(makeLbl("Joining Date:"));fr.add(txtDate);fr.add(makeLbl("Employee ID:"));fr.add(txtEmpId);
        fr.add(makeLbl("Search:"));fr.add(txtSearch);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JButton btnAdd=makeBtn("Add",C_GREEN),btnUpd=makeBtn("Update",C_ORANGE),btnDel=makeBtn("Delete",C_RED);
        JButton btnView=makeBtn("View All",C_ACTIVE),btnClear=makeBtn("Clear",new Color(100,100,120));
        JButton btnSearch=makeBtn("Search",new Color(60,60,130)),btnCSV=makeBtn("Export CSV",new Color(20,110,70));
        br.add(btnAdd);br.add(btnUpd);br.add(btnDel);br.add(btnView);br.add(btnClear);br.add(btnSearch);br.add(btnCSV);
        addTooltip(btnAdd,"Add a new employee record (all fields required)");
        addTooltip(btnUpd,"Update employee details (Employee ID required)");
        addTooltip(btnDel,"Permanently delete employee by ID");
        addTooltip(btnView,"Load all employee records from database");
        addTooltip(btnClear,"Clear all input fields");
        addTooltip(btnSearch,"Search employees by name using LIKE query");
        addTooltip(btnCSV,"Export current table data to CSV file");
        p.add(makeTwoRowForm(fr,br),BorderLayout.NORTH);
        JScrollPane sp=makeTable(new String[]{"ID","Name","Department","Joining Date"});
        getTable(sp).getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                JPanel cell=new JPanel(new FlowLayout(FlowLayout.LEFT,5,2));
                cell.setBackground(sel?new Color(46,117,182,60):Color.WHITE);
                String name=v!=null?v.toString():"?";
                String initials=(name.length()>=2?name.substring(0,2):name).toUpperCase();
                JLabel avatar=new JLabel(initials,SwingConstants.CENTER);
                avatar.setFont(new Font("Arial",Font.BOLD,9));
                avatar.setForeground(Color.WHITE);
                avatar.setOpaque(true);
                avatar.setBackground(new Color(46,117,182));
                avatar.setPreferredSize(new Dimension(24,18));
                avatar.setBorder(new EmptyBorder(0,2,0,2));
                JLabel nameLbl=new JLabel(name); nameLbl.setFont(F_LABEL); nameLbl.setForeground(C_TEXT);
                cell.add(avatar); cell.add(nameLbl); return cell;
            }
        });
        p.add(sp,BorderLayout.CENTER);
        JLabel lblSt=makeStatusBar();
        p.add(makeSouth(
            "SELECT e.Emp_ID, e.Emp_Name, d.Dept_Name, e.Joining_Date\n" +
            "FROM Employees e\n" +
            "INNER JOIN Departments d ON e.Dept_ID = d.Dept_ID",
            lblSt),BorderLayout.SOUTH);
        InputMap im = p.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = p.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_DOWN_MASK),"search");
        am.put("search",new AbstractAction(){public void actionPerformed(ActionEvent e){txtSearch.requestFocus();}});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK),"save");
        am.put("save",new AbstractAction(){public void actionPerformed(ActionEvent e){btnAdd.doClick();}});
        btnClear.addActionListener(e->{txtName.setText("");txtDept.setText("");txtDate.clear();txtEmpId.setText("");txtSearch.setText("");txtName.requestFocus();});
        btnView.addActionListener(e->{
            DefaultTableModel m=getModel(sp);m.setRowCount(0);
            String sql="SELECT e.Emp_ID, e.Emp_Name, d.Dept_Name, e.Joining_Date\n" +
                       "FROM Employees e\n" +
                       "LEFT JOIN Departments d ON e.Dept_ID = d.Dept_ID";
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement(sql);ResultSet rs=ps.executeQuery()){
                while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4)});
                updateStatus(lblSt,sp); logSQL(sql);
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnSearch.addActionListener(e->{
            DefaultTableModel m=getModel(sp);m.setRowCount(0);
            String keyword = txtSearch.getText().trim();
            String sql="SELECT e.Emp_ID, e.Emp_Name, d.Dept_Name, e.Joining_Date\n" +
                       "FROM Employees e\n" +
                       "LEFT JOIN Departments d ON e.Dept_ID = d.Dept_ID\n" +
                       "WHERE e.Emp_Name LIKE '%" + keyword + "%'";
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement(
                "SELECT e.Emp_ID, e.Emp_Name, d.Dept_Name, e.Joining_Date " +
                "FROM Employees e LEFT JOIN Departments d ON e.Dept_ID=d.Dept_ID WHERE e.Emp_Name LIKE ?")){
                ps.setString(1,"%"+keyword+"%");ResultSet rs=ps.executeQuery();
                while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4)});
                updateStatus(lblSt,sp); logSQL(sql);
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnAdd.addActionListener(e->{
           if(txtName.getText().trim().isEmpty()||
   txtDept.getText().trim().isEmpty()){
    JOptionPane.showMessageDialog(null,
    "Please fill in Name and Department ID.",
    "Validation Error",JOptionPane.WARNING_MESSAGE);
    return;
}
            String sql="INSERT INTO Employees (Emp_Name, Dept_ID, Joining_Date)\n" +
                       "VALUES ('" + txtName.getText().trim() + "', " + txtDept.getText().trim() + ", '" + txtDate.getDate() + "')";
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement("INSERT INTO Employees (Emp_Name,Dept_ID,Joining_Date) VALUES (?,?,?)")){
                ps.setString(1,txtName.getText().trim());ps.setInt(2,Integer.parseInt(txtDept.getText().trim()));String dateVal = txtDate.getDate().isEmpty() ? 
    LocalDate.now().toString() : txtDate.getDate();
ps.setString(3, dateVal);
                ps.executeUpdate(); logSQL(sql);
                JOptionPane.showMessageDialog(null,"Employee record added successfully.","Success",JOptionPane.INFORMATION_MESSAGE);
                btnClear.doClick();btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnUpd.addActionListener(e->{
            if(txtEmpId.getText().trim().isEmpty()||txtName.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please provide Employee ID and Name to update.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            String sql="UPDATE Employees\n" +
                       "SET Emp_Name = '" + txtName.getText().trim() + "',\n" +
                       "    Dept_ID  = " + txtDept.getText().trim() + ",\n" +
                       "    Joining_Date = '" + txtDate.getDate() + "'\n" +
                       "WHERE Emp_ID = " + txtEmpId.getText().trim();
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement("UPDATE Employees SET Emp_Name=?,Dept_ID=?,Joining_Date=? WHERE Emp_ID=?")){
                ps.setString(1,txtName.getText().trim());ps.setInt(2,Integer.parseInt(txtDept.getText().trim()));ps.setString(3,txtDate.getDate());ps.setInt(4,Integer.parseInt(txtEmpId.getText().trim()));
                int r=ps.executeUpdate(); logSQL(sql);
                JOptionPane.showMessageDialog(null,r>0?"Record updated successfully.":"No record found with the given ID.","Update",JOptionPane.INFORMATION_MESSAGE);
                btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnDel.addActionListener(e->{
            if(txtEmpId.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter an Employee ID to delete.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            if(JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this employee?\nThis action cannot be undone.",
                "Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION)return;
            String sql="-- ON DELETE CASCADE removes related Salaries, Attendance, Leave records\n" +
                       "DELETE FROM Employees\n" +
                       "WHERE Emp_ID = " + txtEmpId.getText().trim() + "\n\n" +
                       "-- FOREIGN KEY (Emp_ID) REFERENCES Employees(Emp_ID) ON DELETE CASCADE";
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement("DELETE FROM Employees WHERE Emp_ID=?")){
                ps.setInt(1,Integer.parseInt(txtEmpId.getText().trim()));
                int r=ps.executeUpdate();
                logSQL(sql);
                JOptionPane.showMessageDialog(null,r>0?"Employee deleted successfully.":"No record found with the given ID.","Delete",JOptionPane.INFORMATION_MESSAGE);
                btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnCSV.addActionListener(e->exportToCSV(sp,"Employees"));
        getTable(sp).addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int row=getTable(sp).getSelectedRow();
                if(row>=0){
                    txtEmpId.setText(getTable(sp).getValueAt(row,0).toString());
                    txtName.setText(getTable(sp).getValueAt(row,1).toString());
                    txtDate.setDate(getTable(sp).getValueAt(row,3).toString());
                    try(Connection con=DBConnection.getConnection();PreparedStatement ps2=con.prepareStatement("SELECT Dept_ID FROM Employees WHERE Emp_ID=?")){
                        ps2.setInt(1,Integer.parseInt(getTable(sp).getValueAt(row,0).toString()));
                        ResultSet rs2=ps2.executeQuery();
                        if(rs2.next()) txtDept.setText(String.valueOf(rs2.getInt(1)));
                    }catch(Exception ex){ txtDept.setText(""); }
                }
            }
        });
        pContent.add(p,BorderLayout.CENTER);pContent.revalidate();pContent.repaint();btnView.doClick();
    }

    // ==================== DEPARTMENTS ====================
    void showDepartments(){
        pContent.removeAll();
        JPanel p=new JPanel(new BorderLayout(0,8));p.setBackground(C_BG);p.setBorder(new EmptyBorder(12,12,8,12));
        JPanel fr=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JTextField txtId=makeTxt(70),txtName=makeTxt(160),txtMgr=makeTxt(160);
        fr.add(makeLbl("Dept ID:"));fr.add(txtId);fr.add(makeLbl("Name:"));fr.add(txtName);fr.add(makeLbl("Manager:"));fr.add(txtMgr);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JButton btnAdd=makeBtn("Add",C_GREEN),btnUpd=makeBtn("Update Mgr",C_ORANGE),btnDel=makeBtn("Delete",C_RED);
        JButton btnView=makeBtn("View All",C_ACTIVE),btnClear=makeBtn("Clear",new Color(100,100,120)),btnCSV=makeBtn("Export CSV",new Color(20,110,70));
        br.add(btnAdd);br.add(btnUpd);br.add(btnDel);br.add(btnView);br.add(btnClear);br.add(btnCSV);
        addTooltip(btnAdd,"Add a new department record");
        addTooltip(btnUpd,"Update the manager name for selected department");
        addTooltip(btnDel,"Delete department by ID");
        addTooltip(btnCSV,"Export department data to CSV file");
        p.add(makeTwoRowForm(fr,br),BorderLayout.NORTH);
        JScrollPane sp=makeTable(new String[]{"Dept ID","Department Name","Manager"});p.add(sp,BorderLayout.CENTER);
        JLabel lblSt=makeStatusBar();p.add(makeSouth("SELECT * FROM Departments",lblSt),BorderLayout.SOUTH);
        btnClear.addActionListener(e->{txtId.setText("");txtName.setText("");txtMgr.setText("");txtId.requestFocus();});
        btnView.addActionListener(e->{DefaultTableModel m=getModel(sp);m.setRowCount(0);
            try(Connection con=DBConnection.getConnection();ResultSet rs=con.prepareStatement("SELECT * FROM Departments").executeQuery()){
                while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3)});updateStatus(lblSt,sp);
                logSQL("SELECT * FROM Departments");
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnAdd.addActionListener(e->{
            if(txtId.getText().trim().isEmpty()||txtName.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please fill in Department ID and Name.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement("INSERT INTO Departments VALUES (?,?,?)")){
                ps.setInt(1,Integer.parseInt(txtId.getText().trim()));ps.setString(2,txtName.getText().trim());ps.setString(3,txtMgr.getText().trim());
                ps.executeUpdate();
                logSQL("INSERT INTO Departments\nVALUES (" + txtId.getText().trim() + ", '" + txtName.getText().trim() + "', '" + txtMgr.getText().trim() + "')");
                JOptionPane.showMessageDialog(null,"Department added successfully.","Success",JOptionPane.INFORMATION_MESSAGE);
                btnClear.doClick();btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnUpd.addActionListener(e->{
            if(txtId.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter a Department ID to update.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement("UPDATE Departments SET Manager_Name=? WHERE Dept_ID=?")){
                ps.setString(1,txtMgr.getText().trim());ps.setInt(2,Integer.parseInt(txtId.getText().trim()));
                int r=ps.executeUpdate();
                logSQL("UPDATE Departments\nSET Manager_Name = '" + txtMgr.getText().trim() + "'\nWHERE Dept_ID = " + txtId.getText().trim());
                JOptionPane.showMessageDialog(null,r>0?"Manager updated successfully.":"No department found with the given ID.","Update",JOptionPane.INFORMATION_MESSAGE);
                btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnDel.addActionListener(e->{
            if(txtId.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter a Department ID to delete.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement("DELETE FROM Departments WHERE Dept_ID=?")){
                ps.setInt(1,Integer.parseInt(txtId.getText().trim()));int r=ps.executeUpdate();
                logSQL("DELETE FROM Departments\nWHERE Dept_ID = " + txtId.getText().trim());
                JOptionPane.showMessageDialog(null,r>0?"Department deleted successfully.":"No department found with the given ID.","Delete",JOptionPane.INFORMATION_MESSAGE);
                btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnCSV.addActionListener(e->exportToCSV(sp,"Departments"));
        getTable(sp).addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){int row=getTable(sp).getSelectedRow();if(row>=0){txtId.setText(getTable(sp).getValueAt(row,0).toString());txtName.setText(getTable(sp).getValueAt(row,1).toString());txtMgr.setText(getTable(sp).getValueAt(row,2).toString());}}
        });
        pContent.add(p,BorderLayout.CENTER);pContent.revalidate();pContent.repaint();btnView.doClick();
    }

    // ==================== SALARIES ====================
    void showSalaries(){
        pContent.removeAll();
        JPanel p=new JPanel(new BorderLayout(0,8));p.setBackground(C_BG);p.setBorder(new EmptyBorder(12,12,8,12));
        JPanel fr=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JTextField txtSalId=makeTxt(80),txtEmpId=makeTxt(80),txtSalary=makeTxt(140);
        fr.add(makeLbl("Salary ID:"));fr.add(txtSalId);fr.add(makeLbl("Employee ID:"));fr.add(txtEmpId);fr.add(makeLbl("Basic Salary:"));fr.add(txtSalary);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JButton btnAdd=makeBtn("Add / Upsert",C_GREEN),btnUpd=makeBtn("Update",C_ORANGE),btnBonus=makeBtn("Calc Bonus",new Color(120,50,160));
        JButton btnSlip=makeBtn("Salary Slip",new Color(20,100,130)),btnHistory=makeBtn("History",new Color(60,80,120));
        JButton btnView=makeBtn("View All",C_ACTIVE),btnClear=makeBtn("Clear",new Color(100,100,120)),btnCSV=makeBtn("Export CSV",new Color(20,110,70));
        br.add(btnAdd);br.add(btnUpd);br.add(btnBonus);br.add(btnSlip);br.add(btnHistory);br.add(btnView);br.add(btnClear);br.add(btnCSV);
        addTooltip(btnAdd,"UPSERT: Updates existing salary record or inserts a new one");
        addTooltip(btnUpd,"Update salary record by Salary ID");
        addTooltip(btnBonus,"Bulk calculate and update bonus by percentage");
        addTooltip(btnSlip,"Select a row then click to generate formatted salary slip");
        addTooltip(btnHistory,"View salary change history for selected employee");
        addTooltip(btnCSV,"Export salary data to CSV file");
        p.add(makeTwoRowForm(fr,br),BorderLayout.NORTH);
        JScrollPane sp=makeTable(new String[]{"ID","Employee","Basic Salary","Bonus","Total"});
        DefaultTableCellRenderer salaryRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v != null) {
                    try {
                        double val = Double.parseDouble(v.toString());
                        setText(String.format("%,.0f PKR", val));
                    } catch (Exception ignored) {}
                }
                setHorizontalAlignment(SwingConstants.RIGHT);
                return this;
            }
        };
        getTable(sp).getColumnModel().getColumn(2).setCellRenderer(salaryRenderer);
        getTable(sp).getColumnModel().getColumn(3).setCellRenderer(salaryRenderer);
        getTable(sp).getColumnModel().getColumn(4).setCellRenderer(salaryRenderer);
        p.add(sp,BorderLayout.CENTER);
        JLabel lblSt=makeStatusBar();
        p.add(makeSouth(
            "-- UPSERT: Check if record exists, then UPDATE or INSERT\n" +
            "-- All salary changes are logged to Salary_History (Transaction)",
            lblSt),BorderLayout.SOUTH);
        btnClear.addActionListener(e->{txtSalId.setText("");txtEmpId.setText("");txtSalary.setText("");txtEmpId.requestFocus();});
        btnView.addActionListener(e->{DefaultTableModel m=getModel(sp);m.setRowCount(0);
            String sql="SELECT s.Salary_ID, e.Emp_Name, s.Basic_Salary, s.Bonus,\n" +
                       "       (s.Basic_Salary + s.Bonus) AS Total_Salary\n" +
                       "FROM Salaries s\n" +
                       "JOIN Employees e ON s.Emp_ID = e.Emp_ID";
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement(sql);ResultSet rs=ps.executeQuery()){
                while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getDouble(3),rs.getDouble(4),rs.getDouble(5)});updateStatus(lblSt,sp);
                logSQL(sql);
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });

        btnAdd.addActionListener(e->{
            if(txtEmpId.getText().trim().isEmpty()||txtSalary.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter Employee ID and Basic Salary.","Validation Error",JOptionPane.WARNING_MESSAGE);return;
            }
            Connection con=null;
            try{
                con=DBConnection.getConnection();
                con.setAutoCommit(false);
                double basic=Double.parseDouble(txtSalary.getText().trim());
                double bonus=basic*0.10;
                int empId=Integer.parseInt(txtEmpId.getText().trim());

                PreparedStatement chk=con.prepareStatement("SELECT COUNT(*) FROM Salaries WHERE Emp_ID=?");
                chk.setInt(1,empId);
                ResultSet rs=chk.executeQuery();
                rs.next();
                int existing=rs.getInt(1);

                String action;
                if(existing>0){
                    PreparedStatement ups=con.prepareStatement("UPDATE Salaries SET Basic_Salary=?,Bonus=? WHERE Emp_ID=?");
                    ups.setDouble(1,basic); ups.setDouble(2,bonus); ups.setInt(3,empId);
                    ups.executeUpdate();
                    action="Upsert-Updated";
                    logSQL(
                        "-- Step 1: Record exists → UPDATE\n" +
                        "-- SELECT COUNT(*) FROM Salaries WHERE Emp_ID = " + empId + "  →  " + existing + " record(s) found\n\n" +
                        "BEGIN TRANSACTION\n" +
                        "  UPDATE Salaries\n" +
                        "  SET Basic_Salary = " + basic + ", Bonus = " + bonus + "\n" +
                        "  WHERE Emp_ID = " + empId + "\n\n" +
                        "  INSERT INTO Salary_History (Emp_ID, Basic_Salary, Change_Date, Action)\n" +
                        "  VALUES (" + empId + ", " + basic + ", GETDATE(), 'Upsert-Updated')\n" +
                        "COMMIT"
                    );
                } else {
                    PreparedStatement ins=con.prepareStatement("INSERT INTO Salaries (Emp_ID,Basic_Salary,Bonus) VALUES (?,?,?)");
                    ins.setInt(1,empId); ins.setDouble(2,basic); ins.setDouble(3,bonus);
                    ins.executeUpdate();
                    action="Upsert-Inserted";
                    logSQL(
                        "-- Step 1: No record found → INSERT\n" +
                        "-- SELECT COUNT(*) FROM Salaries WHERE Emp_ID = " + empId + "  →  0 records found\n\n" +
                        "BEGIN TRANSACTION\n" +
                        "  INSERT INTO Salaries (Emp_ID, Basic_Salary, Bonus)\n" +
                        "  VALUES (" + empId + ", " + basic + ", " + bonus + ")\n\n" +
                        "  INSERT INTO Salary_History (Emp_ID, Basic_Salary, Change_Date, Action)\n" +
                        "  VALUES (" + empId + ", " + basic + ", GETDATE(), 'Upsert-Inserted')\n" +
                        "COMMIT"
                    );
                }

                PreparedStatement ps2=con.prepareStatement("INSERT INTO Salary_History (Emp_ID,Basic_Salary,Change_Date,Action) VALUES (?,?,GETDATE(),?)");
                ps2.setInt(1,empId); ps2.setDouble(2,basic); ps2.setString(3,action);
                ps2.executeUpdate();

                con.commit();
                JOptionPane.showMessageDialog(null,
                    (existing>0?"Salary record UPDATED (Upsert)":"Salary record INSERTED (Upsert)") +
                    "\nBonus = " + bonus + "\nTransaction committed successfully.","Success",JOptionPane.INFORMATION_MESSAGE);
                btnClear.doClick(); btnView.doClick();
            }catch(Exception ex){
                try{if(con!=null)con.rollback();}catch(Exception ignored){}
                logSQL("ROLLBACK\n-- Transaction failed: " + ex.getMessage());
                JOptionPane.showMessageDialog(null,"Transaction failed and was rolled back.\n"+ex.getMessage(),"Transaction Error",JOptionPane.ERROR_MESSAGE);
            }finally{
                try{if(con!=null){con.setAutoCommit(true);con.close();}}catch(Exception ignored){}
            }
        });

        btnUpd.addActionListener(e->{
            if(txtSalId.getText().trim().isEmpty()||txtSalary.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter Salary ID and Basic Salary.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            Connection con=null;
            try{
                con=DBConnection.getConnection();
                con.setAutoCommit(false);
                double basic=Double.parseDouble(txtSalary.getText().trim());
                double bonus=basic*0.10;
                int salId=Integer.parseInt(txtSalId.getText().trim());
                PreparedStatement ps=con.prepareStatement("UPDATE Salaries SET Basic_Salary=?,Bonus=? WHERE Salary_ID=?");
                ps.setDouble(1,basic);ps.setDouble(2,bonus);ps.setInt(3,salId);
                int r=ps.executeUpdate();
                if(r>0){
                    PreparedStatement gp=con.prepareStatement("SELECT Emp_ID FROM Salaries WHERE Salary_ID=?");
                    gp.setInt(1,salId);ResultSet gr=gp.executeQuery();
                    if(gr.next()){
                        int empId=gr.getInt(1);
                        PreparedStatement ps2=con.prepareStatement("INSERT INTO Salary_History (Emp_ID,Basic_Salary,Change_Date,Action) VALUES (?,?,GETDATE(),?)");
                        ps2.setInt(1,empId);ps2.setDouble(2,basic);ps2.setString(3,"Updated");ps2.executeUpdate();
                    }
                }
                con.commit();
                logSQL(
                    "BEGIN TRANSACTION\n" +
                    "  UPDATE Salaries\n" +
                    "  SET Basic_Salary = " + basic + ",\n" +
                    "      Bonus        = " + bonus + "\n" +
                    "  WHERE Salary_ID  = " + salId + "\n\n" +
                    "  INSERT INTO Salary_History (Emp_ID, Basic_Salary, Change_Date, Action)\n" +
                    "  VALUES (Emp_ID, " + basic + ", GETDATE(), 'Updated')\n" +
                    "COMMIT"
                );
                JOptionPane.showMessageDialog(null,r>0?"Salary updated successfully. Bonus = "+bonus+"\nTransaction committed.":"No record found with the given Salary ID.","Update",JOptionPane.INFORMATION_MESSAGE);
                btnView.doClick();
            }catch(Exception ex){
                try{if(con!=null)con.rollback();}catch(Exception ignored){}
                logSQL("ROLLBACK\n-- Transaction failed: " + ex.getMessage());
                JOptionPane.showMessageDialog(null,"Transaction failed and was rolled back.\n"+ex.getMessage(),"Transaction Error",JOptionPane.ERROR_MESSAGE);
            }finally{
                try{if(con!=null){con.setAutoCommit(true);con.close();}}catch(Exception ignored){}
            }
        });
        btnBonus.addActionListener(e->{
            String pct=JOptionPane.showInputDialog("Enter bonus percentage (e.g. 10):");if(pct==null||pct.trim().isEmpty())return;
            String eid=JOptionPane.showInputDialog("Enter Employee ID (enter 0 for ALL employees):");if(eid==null||eid.trim().isEmpty())return;
            try(Connection con=DBConnection.getConnection()){
                double rate=Double.parseDouble(pct)/100.0;int eId=Integer.parseInt(eid);
                String q=eId==0?"UPDATE Salaries SET Bonus=Basic_Salary*?":"UPDATE Salaries SET Bonus=Basic_Salary*? WHERE Emp_ID=?";
                try(PreparedStatement ps=con.prepareStatement(q)){
                    ps.setDouble(1,rate);if(eId!=0)ps.setInt(2,eId);int r=ps.executeUpdate();
                    logSQL(eId==0
                        ? "UPDATE Salaries\nSET Bonus = Basic_Salary * " + rate + "  -- Applied to ALL employees"
                        : "UPDATE Salaries\nSET Bonus = Basic_Salary * " + rate + "\nWHERE Emp_ID = " + eId);
                    JOptionPane.showMessageDialog(null,"Bonus updated for "+r+" record(s).","Success",JOptionPane.INFORMATION_MESSAGE);}
                btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnSlip.addActionListener(e->{
            int row=getTable(sp).getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(null,"Please select an employee row first.","No Selection",JOptionPane.WARNING_MESSAGE);return;}
            showSalarySlip(getTable(sp).getValueAt(row,1).toString(),
                Double.parseDouble(getTable(sp).getValueAt(row,2).toString()),
                Double.parseDouble(getTable(sp).getValueAt(row,3).toString()),
                Double.parseDouble(getTable(sp).getValueAt(row,4).toString()));
        });
        btnHistory.addActionListener(e->{
            if(txtEmpId.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Please enter an Employee ID.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            showSalaryHistory(txtEmpId.getText().trim());
        });
        btnCSV.addActionListener(e->exportToCSV(sp,"Salaries"));
        getTable(sp).addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){int row=getTable(sp).getSelectedRow();if(row>=0){txtSalId.setText(getTable(sp).getValueAt(row,0).toString());txtSalary.setText(getTable(sp).getValueAt(row,2).toString());}}
        });
        pContent.add(p,BorderLayout.CENTER);pContent.revalidate();pContent.repaint();btnView.doClick();
    }

    void showSalaryHistory(String empIdStr){
        JPanel panel=new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE); panel.setPreferredSize(new Dimension(450,250));
        JLabel title=new JLabel("  Salary History — Employee ID: "+empIdStr); title.setFont(new Font("Arial",Font.BOLD,13)); title.setForeground(C_HEADER);
        panel.add(title,BorderLayout.NORTH);
        DefaultTableModel m=new DefaultTableModel(new String[]{"Date","Basic Salary","Action"},0){public boolean isCellEditable(int r,int c){return false;}};
        JTable t=new JTable(m); t.setFont(F_LABEL); t.setRowHeight(24);
        t.getTableHeader().setBackground(C_HEADER); t.getTableHeader().setForeground(Color.WHITE);
        try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement(
            "SELECT Change_Date, Basic_Salary, Action\n" +
            "FROM Salary_History\n" +
            "WHERE Emp_ID = ?\n" +
            "ORDER BY Change_Date DESC")){
            ps.setInt(1,Integer.parseInt(empIdStr));ResultSet rs=ps.executeQuery();
            while(rs.next())m.addRow(new Object[]{rs.getString(1),rs.getDouble(2),rs.getString(3)});
        }catch(Exception ex){JOptionPane.showMessageDialog(null,"Error loading history (ensure Salary_History table exists): "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);return;}
        panel.add(new JScrollPane(t),BorderLayout.CENTER);
        JLabel note=new JLabel("  SQL: SELECT Change_Date, Basic_Salary, Action FROM Salary_History WHERE Emp_ID = "+empIdStr+" ORDER BY Change_Date DESC");
        note.setFont(new Font("Courier New",Font.PLAIN,10)); note.setForeground(new Color(100,160,100)); note.setBackground(C_SQL_BG); note.setOpaque(true);
        panel.add(note,BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(this,panel,"Salary History",JOptionPane.PLAIN_MESSAGE);
    }

    void showSalarySlip(String empName,double basic,double bonus,double total){
        JPanel slip=new JPanel();slip.setLayout(new BoxLayout(slip,BoxLayout.Y_AXIS));
        slip.setBackground(Color.WHITE);slip.setBorder(new EmptyBorder(15,25,15,25));
        JLabel title=new JLabel("SALARY SLIP",SwingConstants.CENTER);title.setFont(new Font("Arial",Font.BOLD,18));title.setForeground(C_HEADER);title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel company=new JLabel("HRMS Portal",SwingConstants.CENTER);company.setFont(new Font("Arial",Font.PLAIN,12));company.setForeground(new Color(120,120,160));company.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel monthLbl=new JLabel("Month: "+LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")),SwingConstants.CENTER);
        monthLbl.setFont(new Font("Arial",Font.ITALIC,11));monthLbl.setForeground(new Color(120,130,160));monthLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        slip.add(title);slip.add(company);slip.add(Box.createVerticalStrut(4));slip.add(new JSeparator());slip.add(Box.createVerticalStrut(4));slip.add(monthLbl);slip.add(Box.createVerticalStrut(10));
        Object[][] rows={{"Employee Name",empName},{"Basic Salary",String.format("Rs. %,.0f",basic)},{"Bonus",String.format("Rs. %,.0f",bonus)},{"──────────────","─────────"},{"TOTAL SALARY",String.format("Rs. %,.0f",total)}};
        for(Object[] row:rows){
            JPanel line=new JPanel(new BorderLayout());line.setBackground(Color.WHITE);line.setMaximumSize(new Dimension(320,28));
            JLabel k=new JLabel("  "+row[0]);k.setFont(row[0].toString().startsWith("TOTAL")?new Font("Arial",Font.BOLD,13):new Font("Arial",Font.PLAIN,12));k.setForeground(row[0].toString().startsWith("TOTAL")?C_HEADER:C_TEXT);
            JLabel v=new JLabel(row[1].toString()+"  ");v.setFont(row[0].toString().startsWith("TOTAL")?new Font("Arial",Font.BOLD,13):new Font("Arial",Font.PLAIN,12));v.setForeground(row[0].toString().startsWith("TOTAL")?C_GREEN:C_TEXT);v.setHorizontalAlignment(SwingConstants.RIGHT);
            line.add(k,BorderLayout.WEST);line.add(v,BorderLayout.EAST);slip.add(line);
        }
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.CENTER));btnRow.setBackground(Color.WHITE);
        JButton btnPrint=new JButton("Print Salary Slip");
        btnPrint.setFont(F_BTN);btnPrint.setBackground(C_HEADER);btnPrint.setForeground(Color.WHITE);btnPrint.setBorderPainted(false);btnPrint.setFocusPainted(false);
        btnPrint.addActionListener(pe->{
            PrinterJob job=PrinterJob.getPrinterJob();
            job.setPrintable((graphics,pageFormat,pageIndex)->{
                if(pageIndex>0)return Printable.NO_SUCH_PAGE;
                slip.setSize(slip.getPreferredSize());slip.validate();
                graphics.translate((int)pageFormat.getImageableX(),(int)pageFormat.getImageableY());
                slip.print(graphics);return Printable.PAGE_EXISTS;
            });
            if(job.printDialog()){try{job.print();}catch(Exception ex){JOptionPane.showMessageDialog(null,"Print error: "+ex.getMessage(),"Print Error",JOptionPane.ERROR_MESSAGE);}}
        });
        btnRow.add(btnPrint);slip.add(Box.createVerticalStrut(8));slip.add(btnRow);
        JOptionPane.showMessageDialog(this,slip,"Salary Slip - "+empName,JOptionPane.PLAIN_MESSAGE);
    }

    // ==================== LEAVE ====================
    void showLeave(){
        pContent.removeAll();
        JPanel p=new JPanel(new BorderLayout(0,8));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(12,12,8,12));

        JPanel fr=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JTextField txtEmpId=makeTxt(70),txtLeaveId=makeTxt(70),txtBalId=makeTxt(70);
        DatePickerField txtStart = new DatePickerField(100);
        DatePickerField txtEnd   = new DatePickerField(100);
        JComboBox<String> cmbType=new JComboBox<>(new String[]{"Annual","Sick","Casual"});
        cmbType.setFont(F_LABEL);

        fr.add(makeLbl("Emp ID:"));    fr.add(txtEmpId);
        fr.add(makeLbl("Type:"));      fr.add(cmbType);
        fr.add(makeLbl("Start:"));     fr.add(txtStart);
        fr.add(makeLbl("End:"));       fr.add(txtEnd);
        fr.add(makeLbl("Leave ID:"));  fr.add(txtLeaveId);
        fr.add(makeLbl("Balance Emp ID:")); fr.add(txtBalId);

        JPanel fr2=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JTextField txtLoopEmpId=makeTxt(80);
        DatePickerField txtLoopEndDate = new DatePickerField(120);
        fr2.add(makeLbl("Multi-Day Emp ID:"));  fr2.add(txtLoopEmpId);
        fr2.add(makeLbl("End Date (YYYY-MM-DD):")); fr2.add(txtLoopEndDate);

        JPanel br=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JButton btnApply=makeBtn("Apply",C_GREEN),
                btnApprove=makeBtn("Approve",C_ACTIVE),
                btnReject=makeBtn("Reject",C_RED);
        JButton btnBalance=makeBtn("Balance",new Color(100,30,130)),
                btnView=makeBtn("View All",new Color(70,70,80));
        JButton btnClear=makeBtn("Clear",new Color(100,100,120)),
                btnCSV=makeBtn("Export CSV",new Color(20,110,70));
        JButton btnLoop=makeBtn("Multi-Day Leave",new Color(20,90,120));

        br.add(btnApply);br.add(btnApprove);br.add(btnReject);
        br.add(btnBalance);br.add(btnLoop);br.add(btnView);
        br.add(btnClear);br.add(btnCSV);

        JPanel topPanel=new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));
        topPanel.setBackground(C_WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,215,240)),
            new EmptyBorder(6,8,6,8)));
        fr.setBackground(C_WHITE);fr2.setBackground(C_WHITE);br.setBackground(C_WHITE);
        fr.setAlignmentX(Component.LEFT_ALIGNMENT);fr2.setAlignmentX(Component.LEFT_ALIGNMENT);br.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(fr);topPanel.add(Box.createVerticalStrut(2));
        topPanel.add(fr2);topPanel.add(Box.createVerticalStrut(2));
        topPanel.add(br);

        p.add(topPanel, BorderLayout.NORTH);
        JScrollPane sp=makeTable(new String[]{"ID","Employee","Type","Start","End","Status"});p.add(sp,BorderLayout.CENTER);
        JLabel lblSt=makeStatusBar();
        p.add(makeSouth(
            "INSERT INTO Leave_Mgmt (Emp_ID, Leave_Type, Start_Date, End_Date, Status)\n" +
            "VALUES (?, ?, ?, ?, 'Pending')",
            lblSt),BorderLayout.SOUTH);

        btnClear.addActionListener(e->{txtEmpId.setText("");txtStart.clear();txtEnd.clear();txtLeaveId.setText("");txtBalId.setText("");txtLoopEmpId.setText("");txtLoopEndDate.clear();cmbType.setSelectedIndex(0);txtEmpId.requestFocus();});
        btnView.addActionListener(e->{DefaultTableModel m=getModel(sp);m.setRowCount(0);
            String sql="SELECT l.Leave_ID, e.Emp_Name, l.Leave_Type,\n" +
                       "       l.Start_Date, l.End_Date, l.Status\n" +
                       "FROM Leave_Mgmt l\n" +
                       "JOIN Employees e ON l.Emp_ID = e.Emp_ID\n" +
                       "ORDER BY l.Leave_ID DESC";
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement(sql);ResultSet rs=ps.executeQuery()){
                while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)});updateStatus(lblSt,sp);
                logSQL(sql);
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
            getTable(sp).getColumnModel().getColumn(5).setCellRenderer(makeStatusRenderer());
        });
        btnApply.addActionListener(e->{
            if(txtEmpId.getText().trim().isEmpty()||txtStart.getDate().isEmpty()||txtEnd.getDate().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please fill in Employee ID, Start Date and End Date.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement("INSERT INTO Leave_Mgmt (Emp_ID,Leave_Type,Start_Date,End_Date,Status) VALUES (?,?,?,?,'Pending')")){
                ps.setInt(1,Integer.parseInt(txtEmpId.getText().trim()));ps.setString(2,(String)cmbType.getSelectedItem());ps.setString(3,txtStart.getDate());ps.setString(4,txtEnd.getDate());
                ps.executeUpdate();
                logSQL(
                    "INSERT INTO Leave_Mgmt\n" +
                    "       (Emp_ID, Leave_Type, Start_Date, End_Date, Status)\n" +
                    "VALUES (" + txtEmpId.getText().trim() + ", '" + cmbType.getSelectedItem() + "', '" + txtStart.getDate() + "', '" + txtEnd.getDate() + "', 'Pending')"
                );
                JOptionPane.showMessageDialog(null,"Leave application submitted. Status: Pending.","Success",JOptionPane.INFORMATION_MESSAGE);
                btnClear.doClick();btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnApprove.addActionListener(e->{if(txtLeaveId.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Please enter a Leave ID.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}updateLeave(txtLeaveId.getText().trim(),"Approved",btnView);});
        btnReject.addActionListener(e->{if(txtLeaveId.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Please enter a Leave ID.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}updateLeave(txtLeaveId.getText().trim(),"Rejected",btnView);});
        btnBalance.addActionListener(e->{if(txtBalId.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Please enter an Employee ID to check balance.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}showLeaveBalance(txtBalId.getText().trim());});
        btnCSV.addActionListener(e->exportToCSV(sp,"Leave"));

        btnLoop.addActionListener(e->{
            if(txtLoopEmpId.getText().trim().isEmpty()||txtLoopEndDate.getDate().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter Employee ID and End Date for multi-day leave.","Validation Error",JOptionPane.WARNING_MESSAGE);return;
            }
            try{
                int empId=Integer.parseInt(txtLoopEmpId.getText().trim());
                LocalDate startDate=LocalDate.now();
                LocalDate endDate=LocalDate.parse(txtLoopEndDate.getDate());
                if(endDate.isBefore(startDate)){
                    JOptionPane.showMessageDialog(null,"End date must be today or a future date.","Validation Error",JOptionPane.WARNING_MESSAGE);return;
                }
                String leaveType=(String)cmbType.getSelectedItem();
                int count=0;
                Connection con=DBConnection.getConnection();
                con.setAutoCommit(false);
                try{
                    LocalDate d=startDate;
                    while(!d.isAfter(endDate)){
                        String ds=d.toString();
                        PreparedStatement chk=con.prepareStatement("SELECT COUNT(*) FROM Leave_Mgmt WHERE Emp_ID=? AND Start_Date=? AND Leave_Type=?");
                        chk.setInt(1,empId);chk.setString(2,ds);chk.setString(3,leaveType);
                        ResultSet cr=chk.executeQuery();cr.next();
                        if(cr.getInt(1)==0){
                            PreparedStatement ins=con.prepareStatement("INSERT INTO Leave_Mgmt (Emp_ID,Leave_Type,Start_Date,End_Date,Status) VALUES (?,?,?,?,'Approved')");
                            ins.setInt(1,empId);ins.setString(2,leaveType);ins.setString(3,ds);ins.setString(4,ds);
                            ins.executeUpdate();
                            count++;
                        }
                        d=d.plusDays(1);
                    }
                    con.commit();
                    logSQL(
                        "BEGIN TRANSACTION\n" +
                        "  -- Loop: Inserting approved leave for each day\n" +
                        "  INSERT INTO Leave_Mgmt\n" +
                        "         (Emp_ID, Leave_Type, Start_Date, End_Date, Status)\n" +
                        "  VALUES (" + empId + ", '" + leaveType + "', '" + startDate + "' TO '" + endDate + "', 'Approved')\n" +
                        "  -- Total records inserted: " + count + "\n" +
                        "COMMIT"
                    );
                    JOptionPane.showMessageDialog(null,count+" leave record(s) inserted from "+startDate+" to "+endDate+".\nTransaction committed successfully.","Multi-Day Leave",JOptionPane.INFORMATION_MESSAGE);
                    btnView.doClick();
                }catch(Exception ex2){
                    con.rollback();
                    logSQL("ROLLBACK\n-- Multi-day leave insertion failed: " + ex2.getMessage());
                    JOptionPane.showMessageDialog(null,"Operation failed and was rolled back.\n"+ex2.getMessage(),"Transaction Error",JOptionPane.ERROR_MESSAGE);
                }finally{
                    con.setAutoCommit(true);con.close();
                }
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });

        getTable(sp).addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){int row=getTable(sp).getSelectedRow();if(row>=0)txtLeaveId.setText(getTable(sp).getValueAt(row,0).toString());}});
        pContent.add(p,BorderLayout.CENTER);pContent.revalidate();pContent.repaint();btnView.doClick();
    }

   void updateLeave(String id, String status, JButton refresh) {
    Connection con = null;
    try {
        con = DBConnection.getConnection();
        con.setAutoCommit(false);

        // Step 1: Leave ki details lo
        PreparedStatement info = con.prepareStatement(
            "SELECT Emp_ID, Start_Date, End_Date FROM Leave_Mgmt WHERE Leave_ID=?");
        info.setInt(1, Integer.parseInt(id));
        ResultSet rs = info.executeQuery();

        int autoInserted = 0;

        if (rs.next() && status.equals("Approved")) {
            int empId        = rs.getInt(1);
            LocalDate start  = LocalDate.parse(rs.getString(2));
            LocalDate end    = LocalDate.parse(rs.getString(3));

            // Step 2: Har din ke liye attendance auto-insert
            LocalDate d = start;
            while (!d.isAfter(end)) {
                String ds = d.toString();

                // Duplicate check — same din pehle se hai?
                PreparedStatement chk = con.prepareStatement(
                    "SELECT COUNT(*) FROM Attendance WHERE Emp_ID=? AND Att_Date=?");
                chk.setInt(1, empId);
                chk.setString(2, ds);
                ResultSet cr = chk.executeQuery();
                cr.next();

                if (cr.getInt(1) == 0) {
                    PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO Attendance (Emp_ID, Att_Date, Status) VALUES (?, ?, 'Leave')");
                    ins.setInt(1, empId);
                    ins.setString(2, ds);
                    ins.executeUpdate();
                    autoInserted++;
                }
                d = d.plusDays(1);
            }

            logSQL(
                "BEGIN TRANSACTION\n" +
                "  -- Auto-insert attendance on leave approval\n" +
                "  INSERT INTO Attendance (Emp_ID, Att_Date, Status)\n" +
                "  VALUES (" + empId + ", '" + start + "' TO '" + end + "', 'Leave')\n" +
                "  -- Days inserted: " + autoInserted + "\n" +
                "  UPDATE Leave_Mgmt SET Status = 'Approved' WHERE Leave_ID = " + id + "\n" +
                "COMMIT"
            );
        }

        // Step 3: Leave status update karo
        PreparedStatement ps = con.prepareStatement(
            "UPDATE Leave_Mgmt SET Status=? WHERE Leave_ID=?");
        ps.setString(1, status);
        ps.setInt(2, Integer.parseInt(id));
        int r = ps.executeUpdate();

        con.commit();

        String msg = r > 0
            ? "Leave " + status + " successfully.\n" +
              (status.equals("Approved") && autoInserted > 0
                  ? "Attendance auto-marked 'Leave' for " + autoInserted + " day(s)."
                  : status.equals("Approved") ? "Attendance already existed for these days."
                  : "")
            : "No leave record found with the given ID.";

        JOptionPane.showMessageDialog(null, msg, "Status Updated",
            JOptionPane.INFORMATION_MESSAGE);
        refresh.doClick();

    } catch (Exception ex) {
        try { if (con != null) con.rollback(); } catch (Exception ignored) {}
        logSQL("ROLLBACK\n-- updateLeave failed: " + ex.getMessage());
        JOptionPane.showMessageDialog(null,
            "Transaction failed and was rolled back.\n" + ex.getMessage(),
            "Transaction Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try { if (con != null) { con.setAutoCommit(true); con.close(); } }
        catch (Exception ignored) {}
    }
}

    void showLeaveBalance(String empIdStr){
        try(Connection con=DBConnection.getConnection()){
            PreparedStatement np=con.prepareStatement("SELECT Emp_Name FROM Employees WHERE Emp_ID=?");np.setInt(1,Integer.parseInt(empIdStr));
            ResultSet nr=np.executeQuery();if(!nr.next()){JOptionPane.showMessageDialog(null,"No employee found with the given ID.","Not Found",JOptionPane.WARNING_MESSAGE);return;}String empName=nr.getString(1);
            PreparedStatement ps=con.prepareStatement("SELECT Leave_Type, COUNT(*) FROM Leave_Mgmt WHERE Emp_ID=? AND Status='Approved' GROUP BY Leave_Type");
            ps.setInt(1,Integer.parseInt(empIdStr));ResultSet rs=ps.executeQuery();
            int a=0,s=0,c=0;while(rs.next()){String t=rs.getString(1);int ct=rs.getInt(2);if(t.equals("Annual"))a=ct;if(t.equals("Sick"))s=ct;if(t.equals("Casual"))c=ct;}
            JPanel panel=new JPanel();panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));panel.setBackground(Color.WHITE);panel.setBorder(new EmptyBorder(10,20,10,20));
            JLabel title=new JLabel("Leave Balance — "+empName);title.setFont(new Font("Arial",Font.BOLD,14));title.setForeground(C_HEADER);title.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(title);panel.add(Box.createVerticalStrut(10));
            int[] quotas={15,10,7};int[] used={a,s,c};String[] types={"Annual","Sick","Casual"};Color[] colors={C_ACTIVE,C_ORANGE,C_GREEN};
            for(int i=0;i<3;i++){int rem=quotas[i]-used[i];
                JPanel row=new JPanel(new BorderLayout());row.setBackground(Color.WHITE);row.setMaximumSize(new Dimension(310,28));
                JLabel k=new JLabel("  "+types[i]+" Leave");k.setFont(new Font("Arial",Font.PLAIN,12));k.setForeground(C_TEXT);
                JLabel v=new JLabel("Used: "+used[i]+" / "+quotas[i]+"   Remaining: "+rem+"  ");v.setFont(new Font("Arial",Font.BOLD,12));v.setForeground(rem>0?colors[i]:C_RED);v.setHorizontalAlignment(SwingConstants.RIGHT);
                row.add(k,BorderLayout.WEST);row.add(v,BorderLayout.EAST);panel.add(row);panel.add(Box.createVerticalStrut(4));
            }
            JOptionPane.showMessageDialog(this,panel,"Leave Balance",JOptionPane.INFORMATION_MESSAGE);
        }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    // ==================== ATTENDANCE ====================
    void showAttendance(){
        pContent.removeAll();
        JPanel p=new JPanel(new BorderLayout(0,8));p.setBackground(C_BG);p.setBorder(new EmptyBorder(12,12,8,12));
        JPanel fr=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JTextField txtEmpId=makeTxt(80),txtSumId=makeTxt(80);
        DatePickerField txtDate = new DatePickerField(120);
        JComboBox<String> cmbStatus=new JComboBox<>(new String[]{"Present","Absent","Leave"});cmbStatus.setFont(F_LABEL);
        fr.add(makeLbl("Employee ID:"));fr.add(txtEmpId);fr.add(makeLbl("Date:"));fr.add(txtDate);fr.add(makeLbl("Status:"));fr.add(cmbStatus);fr.add(makeLbl("Summary Emp ID:"));fr.add(txtSumId);
        JPanel br=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        JButton btnMark=makeBtn("Mark",C_GREEN),btnSummary=makeBtn("Summary",new Color(20,100,80));
        JButton btnView=makeBtn("View All",C_ACTIVE),btnClear=makeBtn("Clear",new Color(100,100,120)),btnCSV=makeBtn("Export CSV",new Color(20,110,70));
        br.add(btnMark);br.add(btnSummary);br.add(btnView);br.add(btnClear);br.add(btnCSV);
        addTooltip(btnMark,"Mark attendance with duplicate and leave conflict checks");
        addTooltip(btnSummary,"View Present / Absent / Leave summary for an employee");
        addTooltip(btnCSV,"Export attendance records to CSV file");
        p.add(makeTwoRowForm(fr,br),BorderLayout.NORTH);
        JScrollPane sp=makeTable(new String[]{"ID","Employee","Date","Status"});p.add(sp,BorderLayout.CENTER);
        JLabel lblSt=makeStatusBar();
        p.add(makeSouth(
            "-- CHECK Constraint active: CHECK (Status IN ('Present', 'Absent', 'Leave'))\n" +
            "INSERT INTO Attendance (Emp_ID, Att_Date, Status) VALUES (?, ?, ?)",
            lblSt),BorderLayout.SOUTH);
        btnClear.addActionListener(e->{txtEmpId.setText("");txtDate.clear();txtSumId.setText("");cmbStatus.setSelectedIndex(0);txtEmpId.requestFocus();});
        btnView.addActionListener(e->{DefaultTableModel m=getModel(sp);m.setRowCount(0);
            String sql="SELECT a.Att_ID, e.Emp_Name, a.Att_Date, a.Status\n" +
                       "FROM Attendance a\n" +
                       "JOIN Employees e ON a.Emp_ID = e.Emp_ID\n" +
                       "ORDER BY a.Att_Date DESC";
            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement(sql);ResultSet rs=ps.executeQuery()){
                while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4)});updateStatus(lblSt,sp);
                logSQL(sql);
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
            getTable(sp).getColumnModel().getColumn(3).setCellRenderer(makeStatusRenderer());
        });
        btnMark.addActionListener(e->{
            if(txtEmpId.getText().trim().isEmpty()||txtDate.getDate().isEmpty()){
                JOptionPane.showMessageDialog(null,"Please enter Employee ID and Date.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            try{
                LocalDate entered=LocalDate.parse(txtDate.getDate());
                LocalDate today=LocalDate.now();
                if(entered.isAfter(today)){
                    JOptionPane.showMessageDialog(null,"Future dates are not allowed.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
                if(entered.isBefore(today.minusYears(1))){
                    JOptionPane.showMessageDialog(null,"Dates older than 1 year are not permitted.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null,"Invalid date format. Please enter date in YYYY-MM-DD format.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            try(Connection con=DBConnection.getConnection();PreparedStatement chk=con.prepareStatement("SELECT COUNT(*) FROM Attendance WHERE Emp_ID=? AND Att_Date=?")){
                chk.setInt(1,Integer.parseInt(txtEmpId.getText().trim()));chk.setString(2,txtDate.getDate());
                ResultSet rs=chk.executeQuery();
                if(rs.next()&&rs.getInt(1)>0){
                    JOptionPane.showMessageDialog(null,"Attendance has already been marked for this date.","Duplicate Entry",JOptionPane.WARNING_MESSAGE);return;}
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);return;}

            if(cmbStatus.getSelectedItem().equals("Present")){
                try(Connection con2=DBConnection.getConnection();
                    PreparedStatement chkLeave=con2.prepareStatement(
                    "SELECT COUNT(*) FROM Leave_Mgmt WHERE Emp_ID=? AND ? BETWEEN Start_Date AND End_Date AND Status='Approved'")){
                    chkLeave.setInt(1,Integer.parseInt(txtEmpId.getText().trim()));
                    chkLeave.setString(2,txtDate.getDate());
                    ResultSet lr=chkLeave.executeQuery();
                    if(lr.next()&&lr.getInt(1)>0){
                        JOptionPane.showMessageDialog(null,
                            "This employee has an approved leave on the selected date.\nAttendance cannot be marked as Present.",
                            "Leave Conflict",JOptionPane.WARNING_MESSAGE);return;
                    }
                }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);return;}
            }

            try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement("INSERT INTO Attendance (Emp_ID,Att_Date,Status) VALUES (?,?,?)")){
                ps.setInt(1,Integer.parseInt(txtEmpId.getText().trim()));ps.setString(2,txtDate.getDate());ps.setString(3,(String)cmbStatus.getSelectedItem());
                ps.executeUpdate();
                logSQL(
                    "-- CHECK Constraint: CHECK (Status IN ('Present', 'Absent', 'Leave'))\n" +
                    "INSERT INTO Attendance (Emp_ID, Att_Date, Status)\n" +
                    "VALUES (" + txtEmpId.getText().trim() + ", '" + txtDate.getDate() + "', '" + cmbStatus.getSelectedItem() + "')"
                );
                JOptionPane.showMessageDialog(null,"Attendance marked successfully.","Success",JOptionPane.INFORMATION_MESSAGE);
                btnClear.doClick();btnView.doClick();
            }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        });
        btnSummary.addActionListener(e->{
            if(txtSumId.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Please enter an Employee ID for the summary.","Validation Error",JOptionPane.WARNING_MESSAGE);return;}
            showAttendanceSummary(txtSumId.getText().trim());
        });
        btnCSV.addActionListener(e->exportToCSV(sp,"Attendance"));
        pContent.add(p,BorderLayout.CENTER);pContent.revalidate();pContent.repaint();btnView.doClick();
    }

    void showAttendanceSummary(String empIdStr){
        try(Connection con=DBConnection.getConnection()){
            PreparedStatement np=con.prepareStatement("SELECT Emp_Name FROM Employees WHERE Emp_ID=?");np.setInt(1,Integer.parseInt(empIdStr));
            ResultSet nr=np.executeQuery();if(!nr.next()){JOptionPane.showMessageDialog(null,"No employee found with the given ID.","Not Found",JOptionPane.WARNING_MESSAGE);return;}String empName=nr.getString(1);
            PreparedStatement ps=con.prepareStatement("SELECT Status, COUNT(*) FROM Attendance WHERE Emp_ID=? GROUP BY Status");
            ps.setInt(1,Integer.parseInt(empIdStr));ResultSet rs=ps.executeQuery();
            int present=0,absent=0,leave=0;
            while(rs.next()){String s=rs.getString(1);int c=rs.getInt(2);if(s.equals("Present"))present=c;if(s.equals("Absent"))absent=c;if(s.equals("Leave"))leave=c;}
            int total=present+absent+leave;
            JPanel panel=new JPanel();panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));panel.setBackground(Color.WHITE);panel.setBorder(new EmptyBorder(10,25,10,25));
            JLabel title=new JLabel("Attendance Summary — "+empName);title.setFont(new Font("Arial",Font.BOLD,14));title.setForeground(C_HEADER);title.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(title);panel.add(Box.createVerticalStrut(12));
            Object[][] rows={{"Present",present,C_GREEN},{"Absent",absent,C_RED},{"On Leave",leave,C_ORANGE},{"Total Days",total,C_HEADER}};
            for(Object[] row:rows){
                JPanel line=new JPanel(new BorderLayout());line.setBackground(Color.WHITE);line.setMaximumSize(new Dimension(280,30));
                JLabel k=new JLabel("  "+row[0]);k.setFont(new Font("Arial",Font.PLAIN,13));k.setForeground(C_TEXT);
                JLabel v=new JLabel(row[1]+" days  ");v.setFont(new Font("Arial",Font.BOLD,13));v.setForeground((Color)row[2]);v.setHorizontalAlignment(SwingConstants.RIGHT);
                line.add(k,BorderLayout.WEST);line.add(v,BorderLayout.EAST);panel.add(line);panel.add(Box.createVerticalStrut(4));
            }
            JOptionPane.showMessageDialog(this,panel,"Attendance Summary",JOptionPane.INFORMATION_MESSAGE);
        }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    // ==================== REPORTS ====================
    void showReports(){
        pContent.removeAll();
        JPanel p=new JPanel(new BorderLayout(0,8));p.setBackground(C_BG);p.setBorder(new EmptyBorder(12,12,8,12));
        JPanel cards=new JPanel(new FlowLayout(FlowLayout.LEFT,12,6));cards.setBackground(C_BG);
        try(Connection con=DBConnection.getConnection()){
            ResultSet r1=con.prepareStatement("SELECT COUNT(*) FROM Employees").executeQuery();r1.next();
            ResultSet r2=con.prepareStatement("SELECT ISNULL(AVG(Basic_Salary),0) FROM Salaries").executeQuery();r2.next();
            ResultSet r3=con.prepareStatement("SELECT COUNT(*) FROM Leave_Mgmt WHERE Status='Pending'").executeQuery();r3.next();
            ResultSet r4=con.prepareStatement("SELECT COUNT(*) FROM Departments").executeQuery();r4.next();
            String[][] stats={
                {"Total Employees",String.valueOf(r1.getInt(1))},
                {"Avg Salary",String.format("%.0f",r2.getDouble(1))},
                {"Pending Leaves",String.valueOf(r3.getInt(1))},
                {"Departments",String.valueOf(r4.getInt(1))}
            };
            Color[] cardColors={C_ACTIVE,C_GREEN,C_ORANGE,new Color(120,50,160)};
            for(int ci=0;ci<stats.length;ci++){
                final int idx=ci;
                final String[] s=stats[ci];
                JPanel card=new JPanel(){
                    protected void paintComponent(Graphics g2){
                        Graphics2D g=(Graphics2D)g2;
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setColor(getBackground());g.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                        g.setColor(cardColors[idx]);g.fillRoundRect(0,0,getWidth(),6,14,14);
                        g.fillRect(0,3,getWidth(),3);
                    }
                };
                card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
                card.setBackground(C_WHITE);card.setPreferredSize(new Dimension(168,78));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(cardColors[idx].brighter(),1,true),
                    new EmptyBorder(14,14,10,14)));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                JLabel lbl=new JLabel(s[0]);lbl.setFont(new Font("Arial",Font.BOLD,11));lbl.setForeground(cardColors[idx]);
                JLabel val=new JLabel(s[1]);val.setFont(new Font("Arial",Font.BOLD,24));val.setForeground(C_HEADER);
                JLabel hint=new JLabel("click for details");hint.setFont(new Font("Arial",Font.ITALIC,9));hint.setForeground(new Color(160,170,190));
                card.add(lbl);card.add(val);card.add(hint);
                card.addMouseListener(new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){ showCardDetail(idx); }
                    public void mouseEntered(MouseEvent e){ card.setBackground(new Color(240,246,255));card.repaint(); }
                    public void mouseExited(MouseEvent e){ card.setBackground(C_WHITE);card.repaint(); }
                });
                cards.add(card);
            }
        }catch(Exception ex){cards.add(new JLabel("Database connection error: "+ex.getMessage()));}
        p.add(cards, BorderLayout.NORTH);

        JScrollPane sp = makeTable(new String[]{"Department","Manager","Employees","Avg Salary","Total Bill"});
        JPanel mid = new JPanel(new BorderLayout());
        mid.add(buildBarChart(), BorderLayout.NORTH);
        mid.add(sp, BorderLayout.CENTER);
        p.add(mid, BorderLayout.CENTER);

        JLabel lblSt=makeStatusBar();
        JPanel south=new JPanel(new BorderLayout());
        south.add(makeSQLPanel(
            "SELECT d.Dept_Name, d.Manager_Name,\n" +
            "       COUNT(e.Emp_ID)                        AS Total_Employees,\n" +
            "       ISNULL(ROUND(AVG(s.Basic_Salary),0),0) AS Avg_Salary,\n" +
            "       ISNULL(SUM(s.Basic_Salary),0)          AS Total_Bill\n" +
            "FROM Departments d\n" +
            "LEFT JOIN Employees e ON d.Dept_ID = e.Dept_ID\n" +
            "LEFT JOIN Salaries  s ON e.Emp_ID  = s.Emp_ID\n" +
            "GROUP BY d.Dept_Name, d.Manager_Name\n" +
            "ORDER BY Total_Employees DESC"),BorderLayout.CENTER);
        JPanel southBot=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));southBot.setBackground(new Color(225,232,248));
        southBot.add(lblSt);
        JButton btnCSV=makeBtn("Export CSV",new Color(20,110,70));btnCSV.setPreferredSize(new Dimension(120,26));
        addTooltip(btnCSV,"Export report data to CSV file");
        southBot.add(btnCSV);
        south.add(southBot,BorderLayout.SOUTH);
        p.add(south,BorderLayout.SOUTH);

        String reportSql=
            "SELECT d.Dept_Name, d.Manager_Name,\n" +
            "       COUNT(e.Emp_ID)                        AS Total_Employees,\n" +
            "       ISNULL(ROUND(AVG(s.Basic_Salary),0),0) AS Avg_Salary,\n" +
            "       ISNULL(SUM(s.Basic_Salary),0)          AS Total_Bill\n" +
            "FROM Departments d\n" +
            "LEFT JOIN Employees e ON d.Dept_ID = e.Dept_ID\n" +
            "LEFT JOIN Salaries  s ON e.Emp_ID  = s.Emp_ID\n" +
            "GROUP BY d.Dept_Name, d.Manager_Name\n" +
            "ORDER BY Total_Employees DESC";
        try(Connection con=DBConnection.getConnection();PreparedStatement ps=con.prepareStatement(reportSql);ResultSet rs=ps.executeQuery()){
            DefaultTableModel m=getModel(sp);
            while(rs.next())m.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getInt(3),rs.getDouble(4),rs.getDouble(5)});
            updateStatus(lblSt,sp);
            logSQL(reportSql);
        }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        btnCSV.addActionListener(e->exportToCSV(sp,"Reports"));
        pContent.add(p,BorderLayout.CENTER);pContent.revalidate();pContent.repaint();
    }

    // ==================== CARD DETAIL POPUPS ====================
    void showCardDetail(int cardIdx){
        JPanel panel=new JPanel(new BorderLayout(0,8));
        panel.setBackground(Color.WHITE);panel.setPreferredSize(new Dimension(480,280));
        panel.setBorder(new EmptyBorder(10,15,10,15));

        switch(cardIdx){
            case 0: {
                JLabel title=new JLabel("  All Employees");title.setFont(new Font("Arial",Font.BOLD,14));title.setForeground(C_ACTIVE);
                panel.add(title,BorderLayout.NORTH);
                DefaultTableModel m=new DefaultTableModel(new String[]{"ID","Name","Department","Joining Date"},0){public boolean isCellEditable(int r,int c){return false;}};
                JTable t=new JTable(m);t.setFont(F_LABEL);t.setRowHeight(26);
                t.getTableHeader().setBackground(C_HEADER);t.getTableHeader().setForeground(Color.WHITE);
                try(Connection con=DBConnection.getConnection();ResultSet rs=con.prepareStatement(
                    "SELECT e.Emp_ID, e.Emp_Name, d.Dept_Name, e.Joining_Date\n" +
                    "FROM Employees e\n" +
                    "LEFT JOIN Departments d ON e.Dept_ID = d.Dept_ID").executeQuery()){
                    while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4)});
                    logSQL("SELECT e.Emp_ID, e.Emp_Name, d.Dept_Name, e.Joining_Date\nFROM Employees e\nLEFT JOIN Departments d ON e.Dept_ID = d.Dept_ID");
                }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);return;}
                panel.add(new JScrollPane(t),BorderLayout.CENTER);break;
            }
            case 1: {
                JLabel title=new JLabel("  Salary Breakdown");title.setFont(new Font("Arial",Font.BOLD,14));title.setForeground(C_GREEN);
                panel.add(title,BorderLayout.NORTH);
                DefaultTableModel m=new DefaultTableModel(new String[]{"Employee","Basic Salary","Bonus","Total"},0){public boolean isCellEditable(int r,int c){return false;}};
                JTable t=new JTable(m);t.setFont(F_LABEL);t.setRowHeight(26);
                t.getTableHeader().setBackground(C_HEADER);t.getTableHeader().setForeground(Color.WHITE);
                try(Connection con=DBConnection.getConnection();ResultSet rs=con.prepareStatement(
                    "SELECT e.Emp_Name, s.Basic_Salary, s.Bonus,\n" +
                    "       (s.Basic_Salary + s.Bonus) AS Total_Salary\n" +
                    "FROM Salaries s\n" +
                    "JOIN Employees e ON s.Emp_ID = e.Emp_ID\n" +
                    "ORDER BY s.Basic_Salary DESC").executeQuery()){
                    while(rs.next())m.addRow(new Object[]{rs.getString(1),String.format("%.0f",rs.getDouble(2)),String.format("%.0f",rs.getDouble(3)),String.format("%.0f",rs.getDouble(4))});
                    logSQL("SELECT e.Emp_Name, s.Basic_Salary, s.Bonus, (s.Basic_Salary+s.Bonus)\nFROM Salaries s\nJOIN Employees e ON s.Emp_ID=e.Emp_ID\nORDER BY s.Basic_Salary DESC");
                }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);return;}
                panel.add(new JScrollPane(t),BorderLayout.CENTER);break;
            }
            case 2: {
                JLabel title=new JLabel("  Pending Leave Requests");title.setFont(new Font("Arial",Font.BOLD,14));title.setForeground(C_ORANGE);
                panel.add(title,BorderLayout.NORTH);
                DefaultTableModel m=new DefaultTableModel(new String[]{"Leave ID","Employee","Type","Start","End"},0){public boolean isCellEditable(int r,int c){return false;}};
                JTable t=new JTable(m);t.setFont(F_LABEL);t.setRowHeight(26);
                t.getTableHeader().setBackground(C_HEADER);t.getTableHeader().setForeground(Color.WHITE);
                try(Connection con=DBConnection.getConnection();ResultSet rs=con.prepareStatement(
                    "SELECT l.Leave_ID, e.Emp_Name, l.Leave_Type,\n" +
                    "       l.Start_Date, l.End_Date\n" +
                    "FROM Leave_Mgmt l\n" +
                    "JOIN Employees e ON l.Emp_ID = e.Emp_ID\n" +
                    "WHERE l.Status = 'Pending'").executeQuery()){
                    while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)});
                    logSQL("SELECT l.Leave_ID, e.Emp_Name, l.Leave_Type, l.Start_Date, l.End_Date\nFROM Leave_Mgmt l\nJOIN Employees e ON l.Emp_ID=e.Emp_ID\nWHERE l.Status = 'Pending'");
                }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);return;}
                panel.add(new JScrollPane(t),BorderLayout.CENTER);break;
            }
            case 3: {
                JLabel title=new JLabel("  All Departments");title.setFont(new Font("Arial",Font.BOLD,14));title.setForeground(new Color(120,50,160));
                panel.add(title,BorderLayout.NORTH);
                DefaultTableModel m=new DefaultTableModel(new String[]{"Dept ID","Department","Manager","Employees"},0){public boolean isCellEditable(int r,int c){return false;}};
                JTable t=new JTable(m);t.setFont(F_LABEL);t.setRowHeight(26);
                t.getTableHeader().setBackground(C_HEADER);t.getTableHeader().setForeground(Color.WHITE);
                try(Connection con=DBConnection.getConnection();ResultSet rs=con.prepareStatement(
                    "SELECT d.Dept_ID, d.Dept_Name, d.Manager_Name,\n" +
                    "       COUNT(e.Emp_ID) AS Total_Employees\n" +
                    "FROM Departments d\n" +
                    "LEFT JOIN Employees e ON d.Dept_ID = e.Dept_ID\n" +
                    "GROUP BY d.Dept_ID, d.Dept_Name, d.Manager_Name").executeQuery()){
                    while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4)});
                    logSQL("SELECT d.Dept_ID, d.Dept_Name, d.Manager_Name, COUNT(e.Emp_ID)\nFROM Departments d\nLEFT JOIN Employees e ON d.Dept_ID=e.Dept_ID\nGROUP BY d.Dept_ID, d.Dept_Name, d.Manager_Name");
                }catch(Exception ex){JOptionPane.showMessageDialog(null,"Database error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);return;}
                panel.add(new JScrollPane(t),BorderLayout.CENTER);break;
            }
        }
        String[] titles={"Total Employees","Salary Breakdown","Pending Leaves","Departments"};
        JOptionPane.showMessageDialog(this,panel,titles[cardIdx],JOptionPane.PLAIN_MESSAGE);
    } 
}