/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.javanese.kasir;

import javax.swing.*;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public final class tampiladmin extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(tampiladmin.class.getName());
    private Connection conn;

    /**
     * Creates new form tampiladmin
     */
    public tampiladmin() {
        initComponents();
        this.setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        chartpanel.setLayout(new BorderLayout());

        loadDataStok();
        loadDataPenawaran();
        jTabbedPane1.setUI(new MetalTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0; 
            }
        });
        styleSidebarButton(homebtn);
        styleSidebarButton(belibtn);
    }
    
    public void loadDataStok() {
    panelproduk.removeAll();
    panelproduk.setLayout(new BoxLayout(panelproduk, BoxLayout.Y_AXIS));
    initDb();
    loadStats();
    if (conn == null) {
        return;
    }
    try {
        String sql = "SELECT nama_produk, stok FROM produk";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            String nama = rs.getString("nama_produk");
            int stok = rs.getInt("stok");
            ItemStok item = new ItemStok(nama, stok);
            panelproduk.add(item);
            panelproduk.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        panelproduk.revalidate();
        panelproduk.repaint();
        } catch (SQLException e) {
           logger.log(Level.SEVERE, "Error Load Data", e);
           }
        }
   
    private void initDb() {
    conn = koneksi.getConnection();
    if (conn == null) {
        JOptionPane.showMessageDialog(this,
                "Koneksi database gagal. Periksa konfigurasi class koneksi.");
            }
        }

     private void loadStats() {
    if (conn == null) {
        return;
    }
    try {
        double totalDana = 0;
        String sqlDana = "SELECT COALESCE(SUM(dana), 0) FROM admin";
        try (PreparedStatement ps = conn.prepareStatement(sqlDana);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalDana = rs.getDouble(1);
            }
        }
        jLabel2.setText("Rp " + String.format("%,.0f", totalDana));
        danastat.removeAll();
        danastat.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(danatitle, BorderLayout.NORTH);
        header.add(jLabel2, BorderLayout.CENTER);
        danastat.add(header, BorderLayout.CENTER);
        danastat.revalidate();
        danastat.repaint();
        int totalStok = 0;
        String sqlStok = "SELECT COALESCE(SUM(stok), 0) FROM produk";
        try (PreparedStatement ps = conn.prepareStatement(sqlStok);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalStok = rs.getInt(1);
            }
        }
        jLabel3.setText(String.valueOf(totalStok));
        int totalTerjual = 0;
        String sqlTerjual = "SELECT COALESCE(SUM(jumlah), 0) FROM transaksi";
        try (PreparedStatement ps = conn.prepareStatement(sqlTerjual);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalTerjual = rs.getInt(1);
            }
        }
        jLabel4.setText(String.valueOf(totalTerjual));
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(totalStok,    "Jumlah", "Stok");
        dataset.addValue(totalTerjual, "Jumlah", "Terjual");

        JFreeChart barChart = ChartFactory.createBarChart(
                null,                   
                "Kategori",              
                "Jumlah",               
                dataset,
                PlotOrientation.HORIZONTAL, 
                false,                 
                true,                    
                false                  
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setOpaque(false);

        chartpanel.removeAll();
        chartpanel.add(chartPanel, BorderLayout.CENTER);
        chartpanel.revalidate();
        chartpanel.repaint();

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error loading stats", ex);
        }

    }
     public void eksekusiBeliStok(int idJual, String nama, int qty, int harga) {
        try {
            Connection conn = koneksi.getConnection();
            // cek dana admin
            String sqlCek = "SELECT dana FROM admin WHERE id_admin = ?";
            PreparedStatement pstCek = conn.prepareStatement(sqlCek);
            pstCek.setInt(1, 1); // variable id admin
            ResultSet rs = pstCek.executeQuery();
            if (rs.next()) {
                int danaAdmin = rs.getInt("dana");
                if (danaAdmin < harga) {
                    JOptionPane.showMessageDialog(this, "Dana tidak cukup! Sisa dana: Rp " + danaAdmin);
                    return;
                }
                conn.setAutoCommit(false); // Mulai transaction
                try {
                    // Kurangi Dana Admin
                    String sqlUpdateDana = "UPDATE admin SET dana = dana - ? WHERE id_admin = ?";
                    PreparedStatement pstDana = conn.prepareStatement(sqlUpdateDana);
                    pstDana.setInt(1, harga);
                    pstDana.setInt(2, 1);
                    pstDana.executeUpdate();
                    // Tambah Stok di tabel Produk
                    String sqlUpdateStok = "UPDATE produk SET stok = stok + ? WHERE nama_produk = ?";
                    PreparedStatement pstStok = conn.prepareStatement(sqlUpdateStok);
                    pstStok.setInt(1, qty);
                    pstStok.setString(2, nama);
                    pstStok.executeUpdate();
                    // Hapus dari tabel jualan (karena sudah dibeli)
                    String sqlHapusJualan = "DELETE FROM jualan WHERE id_jual = ?";
                    PreparedStatement pstDel = conn.prepareStatement(sqlHapusJualan);
                    pstDel.setInt(1, idJual);
                    pstDel.executeUpdate();
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Berhasil membeli stok " + nama);
                    //Refresh semua data di dashboard
                    loadDataPenawaran(); 
                    loadDataStok();
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal Transaksi: " + e.getMessage());
        }
    }
    public void loadDataPenawaran() {
        // Bersihin dulu
        paneljual.removeAll();
        paneljual.setLayout(new javax.swing.BoxLayout(paneljual, javax.swing.BoxLayout.Y_AXIS));
        try {
            Connection conn = koneksi.getConnection();
            String sql = "SELECT id_jual, nama_produk, jumlah, harga_total, nama_penjual FROM jualan";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                // Ambil data dari hasil query
                int id = rs.getInt("id_jual");
                String produk = rs.getString("nama_produk");
                int qty = rs.getInt("jumlah");
                int harga = rs.getInt("harga_total");
                String penjual = rs.getString("nama_penjual");

                ItemPenawaran item = new ItemPenawaran(id, produk, qty, harga, penjual, this);
                paneljual.add(item);
                // kasi jarak
                paneljual.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
            }
            paneljual.revalidate();
            paneljual.repaint();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal memuat penawaran: " + e.getMessage());
        }
    }
    private void styleSidebarButton(JButton btn) {
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false);     
        btn.setFocusPainted(false);      
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); 
        btn.setHorizontalAlignment(SwingConstants.LEFT); 
        btn.setForeground(Color.WHITE); 
        // Efek Hover (Berubah warna saat mouse lewat)
//        btn.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                btn.setContentAreaFilled(true);
//                btn.setBackground(new java.awt.Color(255, 255, 255, 40)); 
//            }
//            @Override
//            public void mouseExited(java.awt.event.MouseEvent evt) {
//                btn.setContentAreaFilled(false);
//            }
//        });
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        sidebar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        homebtn = new javax.swing.JButton();
        belibtn = new javax.swing.JButton();
        logoutbtn = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        dashboardpanel = new javax.swing.JPanel();
        dashboardtitle = new javax.swing.JLabel();
        statcard = new javax.swing.JPanel();
        danastat = new javax.swing.JPanel();
        danatitle = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        stokstat = new javax.swing.JPanel();
        stoktitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        terjualstat = new javax.swing.JPanel();
        terjualtitle = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        stokscroll = new javax.swing.JScrollPane();
        panelproduk = new javax.swing.JPanel();
        chartpanel = new javax.swing.JPanel();
        buypanel = new javax.swing.JPanel();
        buylabel = new javax.swing.JLabel();
        jualscroll = new javax.swing.JScrollPane();
        paneljual = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        sidebar.setBackground(new java.awt.Color(51, 51, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("TOKO JAVA");

        homebtn.setBackground(new java.awt.Color(51, 51, 255));
        homebtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        homebtn.setForeground(new java.awt.Color(255, 255, 255));
        homebtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/javanese/kasir/images/icons8-home-25.png"))); // NOI18N
        homebtn.setText("Home");
        homebtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        homebtn.addActionListener(this::homebtnActionPerformed);

        belibtn.setBackground(new java.awt.Color(51, 51, 255));
        belibtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        belibtn.setForeground(new java.awt.Color(255, 255, 255));
        belibtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/javanese/kasir/images/icons8-cart-30.png"))); // NOI18N
        belibtn.setText("Beli");
        belibtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        belibtn.addActionListener(this::belibtnActionPerformed);

        logoutbtn.setBackground(new java.awt.Color(51, 51, 255));
        logoutbtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        logoutbtn.setForeground(new java.awt.Color(255, 255, 255));
        logoutbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/javanese/kasir/images/icons8-logout-25.png"))); // NOI18N
        logoutbtn.setText("Logout");
        logoutbtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        logoutbtn.addActionListener(this::logoutbtnActionPerformed);

        javax.swing.GroupLayout sidebarLayout = new javax.swing.GroupLayout(sidebar);
        sidebar.setLayout(sidebarLayout);
        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logoutbtn)
                    .addComponent(homebtn)
                    .addComponent(jLabel1)
                    .addComponent(belibtn))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        sidebarLayout.setVerticalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(41, 41, 41)
                .addComponent(homebtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(belibtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoutbtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(800, 600));

        dashboardtitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        dashboardtitle.setText("DASHBOARD");

        statcard.setBackground(new java.awt.Color(255, 255, 255));
        statcard.setLayout(new java.awt.GridLayout(0, 3, 10, 10));

        danastat.setBackground(new java.awt.Color(51, 51, 255));

        danatitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        danatitle.setForeground(new java.awt.Color(255, 255, 255));
        danatitle.setText("DANA");
        danatitle.setToolTipText("");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("0");

        javax.swing.GroupLayout danastatLayout = new javax.swing.GroupLayout(danastat);
        danastat.setLayout(danastatLayout);
        danastatLayout.setHorizontalGroup(
            danastatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(danastatLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(danastatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(danastatLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2))
                    .addComponent(danatitle))
                .addContainerGap(150, Short.MAX_VALUE))
        );
        danastatLayout.setVerticalGroup(
            danastatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(danastatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(danatitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        statcard.add(danastat);

        stokstat.setBackground(new java.awt.Color(51, 51, 255));

        stoktitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        stoktitle.setForeground(new java.awt.Color(255, 255, 255));
        stoktitle.setText("STOK");
        stoktitle.setToolTipText("");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("0");

        javax.swing.GroupLayout stokstatLayout = new javax.swing.GroupLayout(stokstat);
        stokstat.setLayout(stokstatLayout);
        stokstatLayout.setHorizontalGroup(
            stokstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stokstatLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(stoktitle)
                .addContainerGap(155, Short.MAX_VALUE))
            .addGroup(stokstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(stokstatLayout.createSequentialGroup()
                    .addGap(75, 75, 75)
                    .addComponent(jLabel3)
                    .addContainerGap(160, Short.MAX_VALUE)))
        );
        stokstatLayout.setVerticalGroup(
            stokstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stokstatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stoktitle)
                .addContainerGap(78, Short.MAX_VALUE))
            .addGroup(stokstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(stokstatLayout.createSequentialGroup()
                    .addGap(28, 28, 28)
                    .addComponent(jLabel3)
                    .addContainerGap(28, Short.MAX_VALUE)))
        );

        statcard.add(stokstat);

        terjualstat.setBackground(new java.awt.Color(51, 51, 255));

        terjualtitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        terjualtitle.setForeground(new java.awt.Color(255, 255, 255));
        terjualtitle.setText("TERJUAL");
        terjualtitle.setToolTipText("");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("0");

        javax.swing.GroupLayout terjualstatLayout = new javax.swing.GroupLayout(terjualstat);
        terjualstat.setLayout(terjualstatLayout);
        terjualstatLayout.setHorizontalGroup(
            terjualstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(terjualstatLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(terjualtitle)
                .addContainerGap(142, Short.MAX_VALUE))
            .addGroup(terjualstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(terjualstatLayout.createSequentialGroup()
                    .addGap(75, 75, 75)
                    .addComponent(jLabel4)
                    .addContainerGap(160, Short.MAX_VALUE)))
        );
        terjualstatLayout.setVerticalGroup(
            terjualstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(terjualstatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(terjualtitle)
                .addContainerGap(78, Short.MAX_VALUE))
            .addGroup(terjualstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(terjualstatLayout.createSequentialGroup()
                    .addGap(28, 28, 28)
                    .addComponent(jLabel4)
                    .addContainerGap(28, Short.MAX_VALUE)))
        );

        statcard.add(terjualstat);

        stokscroll.setBackground(new java.awt.Color(255, 255, 255));
        stokscroll.setBorder(null);
        stokscroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelproduk.setLayout(new javax.swing.BoxLayout(panelproduk, javax.swing.BoxLayout.Y_AXIS));
        stokscroll.setViewportView(panelproduk);

        chartpanel.setBackground(new java.awt.Color(204, 204, 204));
        chartpanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        chartpanel.setPreferredSize(new java.awt.Dimension(0, 250));

        javax.swing.GroupLayout chartpanelLayout = new javax.swing.GroupLayout(chartpanel);
        chartpanel.setLayout(chartpanelLayout);
        chartpanelLayout.setHorizontalGroup(
            chartpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        chartpanelLayout.setVerticalGroup(
            chartpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout dashboardpanelLayout = new javax.swing.GroupLayout(dashboardpanel);
        dashboardpanel.setLayout(dashboardpanelLayout);
        dashboardpanelLayout.setHorizontalGroup(
            dashboardpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashboardpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dashboardpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chartpanel, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addComponent(stokscroll)
                    .addComponent(statcard, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, dashboardpanelLayout.createSequentialGroup()
                        .addComponent(dashboardtitle)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        dashboardpanelLayout.setVerticalGroup(
            dashboardpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dashboardtitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statcard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stokscroll, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartpanel, 224, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("tab1", dashboardpanel);

        buylabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        buylabel.setText("BELI STOK");

        jualscroll.setBackground(new java.awt.Color(255, 255, 255));
        jualscroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        paneljual.setBackground(new java.awt.Color(255, 255, 255));
        paneljual.setLayout(new javax.swing.BoxLayout(paneljual, javax.swing.BoxLayout.Y_AXIS));
        jualscroll.setViewportView(paneljual);

        javax.swing.GroupLayout buypanelLayout = new javax.swing.GroupLayout(buypanel);
        buypanel.setLayout(buypanelLayout);
        buypanelLayout.setHorizontalGroup(
            buypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buypanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buylabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(buypanelLayout.createSequentialGroup()
                .addComponent(jualscroll)
                .addContainerGap())
        );
        buypanelLayout.setVerticalGroup(
            buypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buypanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buylabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jualscroll))
        );

        jTabbedPane1.addTab("tab2", buypanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void belibtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_belibtnActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_belibtnActionPerformed

    private void homebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homebtnActionPerformed
        // TODO add your handling code here:
       jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_homebtnActionPerformed

    private void logoutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutbtnActionPerformed
        // TODO add your handling code here:
        //konfimr dulu
        int konfirmasi = javax.swing.JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin logout?", 
            "Konfirmasi Logout", 
            javax.swing.JOptionPane.YES_NO_OPTION
        );
        if (konfirmasi == javax.swing.JOptionPane.YES_OPTION) {
            //bersihin session
            com.javanese.kasir.login.idLoggedIn = 0; 
            com.javanese.kasir.login.namaLoggedIn = null;
            com.javanese.kasir.login.roleLoggedIn = null;

            new com.javanese.kasir.tampilkasir().setVisible(true);
            this.dispose(); 

            javax.swing.JOptionPane.showMessageDialog(null, "Anda telah berhasil logout.");
        }
    }//GEN-LAST:event_logoutbtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new tampiladmin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton belibtn;
    private javax.swing.JLabel buylabel;
    private javax.swing.JPanel buypanel;
    private javax.swing.JPanel chartpanel;
    private javax.swing.JPanel danastat;
    private javax.swing.JLabel danatitle;
    private javax.swing.JPanel dashboardpanel;
    private javax.swing.JLabel dashboardtitle;
    private javax.swing.JButton homebtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JScrollPane jualscroll;
    private javax.swing.JButton logoutbtn;
    private javax.swing.JPanel paneljual;
    private javax.swing.JPanel panelproduk;
    private javax.swing.JPanel sidebar;
    private javax.swing.JPanel statcard;
    private javax.swing.JScrollPane stokscroll;
    private javax.swing.JPanel stokstat;
    private javax.swing.JLabel stoktitle;
    private javax.swing.JPanel terjualstat;
    private javax.swing.JLabel terjualtitle;
    // End of variables declaration//GEN-END:variables

}
