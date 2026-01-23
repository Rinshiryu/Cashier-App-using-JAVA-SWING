/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javanese.kasir;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Lenovo
 */

public class ItemStok extends JPanel {
    private JLabel lblNama = new JLabel();
    private JLabel lblStok = new JLabel();

    public ItemStok(String nama, int stok) {
        // Desain Card
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Agar tidak terlalu tinggi

        // Label Nama
        lblNama.setText(nama);
        lblNama.setFont(new Font("Inter", Font.BOLD, 14));
        
        // Label Stok
        lblStok.setText("Stok: " + stok);
        lblStok.setFont(new Font("Inter", Font.PLAIN, 13));
        // Jika stok sedikit (misal < 5), beri warna merah
        if (stok < 5) {
            lblStok.setForeground(Color.RED);
        } else {
            lblStok.setForeground(new Color(100, 100, 100));
        }

        add(lblNama, BorderLayout.WEST);
        add(lblStok, BorderLayout.EAST);
    }
}
