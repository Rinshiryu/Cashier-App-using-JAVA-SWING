/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javanese.kasir;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
/**
 *
 * @author Lenovo
 */
public class ItemPenawaran extends javax.swing.JPanel {
    private int idJual, hargaTotal, jumlah;
    private String namaProduk, namaPenjual;
    private tampiladmin parent;

    public ItemPenawaran(int id, String nama, int qty, int harga, String penjual, tampiladmin parent) {
        this.idJual = id;
        this.namaProduk = nama;
        this.jumlah = qty;
        this.hargaTotal = harga;
        this.namaPenjual = penjual;
        this.parent = parent;

        // 1. Layout & Dasar
        setLayout(new BorderLayout(20, 0));
        setOpaque(false); // Agar sudut melengkung terlihat rapi
        setBackground(Color.WHITE);
        
        // Jarak antar kartu (Margin Luar)
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // 2. Info Detail (Kiri)
        // Gunakan font yang lebih clean dan warna yang kontras
        String infoText = "<html>"
            + "<div style='margin-left: 5px;'>"
            + "<b style='color: #34495e; font-size: 12px;'>" + namaProduk.toUpperCase() + "</b><br>"
            + "<span style='color: #27ae60; font-size: 14px; font-weight: bold;'>Rp " + String.format("%,d", hargaTotal) + "</span><br>"
            + "<span style='color: #95a5a6; font-size: 10px;'>Stok: " + jumlah + " • Penjual: " + namaPenjual + "</span>"
            + "</div></html>";
        
        JLabel lblInfo = new JLabel(infoText);
        lblInfo.setIcon(createInitialIcon(namaProduk)); // Tambahkan Icon Inisial Bulat
        lblInfo.setIconTextGap(15);

        // 3. Tombol Beli (Kanan) - Desain Modern
        JButton btnBeli = new JButton("BELI STOK");
        btnBeli.setFocusPainted(false);
        btnBeli.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBeli.setBackground(new Color(52, 152, 219)); // Biru Cerah
        btnBeli.setForeground(Color.WHITE);
        btnBeli.setFont(new Font("Inter", Font.BOLD, 11));
        btnBeli.setPreferredSize(new Dimension(100, 35));
        
        // Efek Hover Tombol
        btnBeli.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnBeli.setBackground(new Color(41, 128, 185)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnBeli.setBackground(new Color(52, 152, 219)); }
        });

        btnBeli.addActionListener(e -> {
            parent.eksekusiBeliStok(idJual, namaProduk, jumlah, hargaTotal);
        });

        // Panel khusus untuk tombol agar berada di tengah secara vertikal
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(btnBeli);

        add(lblInfo, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    // Membuat Icon Inisial Bulat
    private ImageIcon createInitialIcon(String text) {
        int size = 45;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Gambar Bulatan
        g2.setColor(new Color(236, 240, 241));
        g2.fillOval(0, 0, size, size);
        // Gambar Huruf Inisial
        g2.setColor(new Color(52, 152, 219));
        g2.setFont(new Font("Inter", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        String initial = text.substring(0, 1).toUpperCase();
        int x = (size - fm.stringWidth(initial)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(initial, x, y);
        g2.dispose();
        return new ImageIcon(img);
    }
    // rounded corner
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        // Gambar kotak melengkung
        g2.fill(new RoundRectangle2D.Double(5, 5, getWidth()-10, getHeight()-10, 15, 15));
        // Gambar border tipis
        g2.setColor(new Color(230, 230, 230));
        g2.draw(new RoundRectangle2D.Double(5, 5, getWidth()-10, getHeight()-10, 15, 15));
        g2.dispose();
        super.paintComponent(g);
    }
}