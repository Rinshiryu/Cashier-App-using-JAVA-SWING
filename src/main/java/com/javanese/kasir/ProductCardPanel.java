/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javanese.kasir;

import javax.swing.*;
import java.awt.*;


public class ProductCardPanel extends JPanel {

    public ProductCardPanel(String nama, int harga, String imgPath, Runnable onAdd) {
        // Desain Kartu Utama
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);
        // Memberikan border bulat halus dan bayangan tipis (simulasi)
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        setPreferredSize(new Dimension(450, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // ================= LEFT PANEL (Inisial Produk) =================
        JPanel initialPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 102, 255)); // Warna Biru Toko Java
                g2.fillOval(0, 0, 60, 60);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                String initial = nama.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (60 - fm.stringWidth(initial)) / 2;
                int y = ((60 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
            }
        };
        initialPanel.setPreferredSize(new Dimension(60, 60));
        initialPanel.setOpaque(false);
        
        // Panel pembungkus agar lingkaran berada di tengah vertikal
        JPanel westWrapper = new JPanel(new GridBagLayout());
        westWrapper.setOpaque(false);
        westWrapper.add(initialPanel);
        add(westWrapper, BorderLayout.WEST);

        // ================= CENTER PANEL (Info Produk) =================
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblNama = new JLabel(nama.toUpperCase());
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNama.setForeground(new Color(50, 50, 50));

        // Format harga ke Rupiah
        String hargaFormatted = String.format("Rp %,d", harga).replace(',', '.');
        JLabel lblHarga = new JLabel(hargaFormatted);
        lblHarga.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHarga.setForeground(new Color(0, 153, 51)); // Warna Hijau Harga

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(lblNama);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblHarga);
        infoPanel.add(Box.createVerticalGlue());

        add(infoPanel, BorderLayout.CENTER);

        // ================= RIGHT PANEL (Tombol Tambah) =================
        JButton btnTambah = new JButton("TAMBAH +");
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambah.setFocusPainted(false);
        btnTambah.setBackground(new Color(0, 102, 255));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.setPreferredSize(new Dimension(100, 35));
        
        // Efek hover sederhana (opsional)
        btnTambah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnTambah.setBackground(new Color(0, 80, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnTambah.setBackground(new Color(0, 102, 255));
            }
        });

        btnTambah.addActionListener(e -> onAdd.run());

        JPanel btnWrapper = new JPanel(new GridBagLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.add(btnTambah);
        add(btnWrapper, BorderLayout.EAST);
    }
}