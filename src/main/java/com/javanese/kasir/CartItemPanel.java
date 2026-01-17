/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javanese.kasir;
import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
/**
 *
 * @author Lenovo
 */
public class CartItemPanel extends JPanel {

    private final int harga;
    private final JSpinner qtySpinner;
    private final JLabel subtotalLabel;
    private final String nama;

    public CartItemPanel(String nama, int harga, Runnable onRemove) {
        this.nama = nama;
        this.harga = harga;

        setLayout(new BorderLayout(15, 5));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // ================= Kotak initial karena gambar gk bisa ajg =================
        JPanel initialPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245)); // Abu-abu muda untuk keranjang
                g2.fillOval(0, 0, 50, 50);
                
                g2.setColor(new Color(100, 100, 100));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String initial = nama.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (50 - fm.stringWidth(initial)) / 2;
                int y = ((50 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
            }
        };
        initialPanel.setPreferredSize(new Dimension(50, 50));
        initialPanel.setOpaque(false);
        add(initialPanel, BorderLayout.WEST);

        // ================= ditengah, buat qty =================
        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setOpaque(false);

        JLabel namaLabel = new JLabel(nama);
        namaLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel hargaLabel = new JLabel(formatRupiah(harga));
        hargaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        qtySpinner.setMaximumSize(new Dimension(60, 25));
        qtySpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        qtySpinner.addChangeListener(e -> updateSubtotal());

        detail.add(namaLabel);
        detail.add(hargaLabel);
        detail.add(Box.createVerticalStrut(5));
        detail.add(qtySpinner);

        add(detail, BorderLayout.CENTER);

        // ================= kanan buat yg laen=================
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        subtotalLabel = new JLabel(formatRupiah(harga));
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subtotalLabel.setForeground(new Color(0, 102, 255));
        subtotalLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnHapus.setForeground(Color.RED);
        btnHapus.setContentAreaFilled(false);
        btnHapus.setBorderPainted(false);
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnHapus.addActionListener(e -> onRemove.run());

        rightPanel.add(subtotalLabel);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(btnHapus);

        add(rightPanel, BorderLayout.EAST);
    }

    private void updateSubtotal() {
        int qty = (int) qtySpinner.getValue();
        subtotalLabel.setText(formatRupiah(qty * harga));
    }

    private String formatRupiah(int nominal) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return nf.format(nominal).replace(",00", "");
    }

    public int getSubtotal() {
        return (int) qtySpinner.getValue() * harga;
    }
}
