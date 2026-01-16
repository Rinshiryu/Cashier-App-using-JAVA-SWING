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
public class CartItemPanel extends JPanel {

    private int harga;
    private JSpinner qtySpinner;
    private JLabel subtotalLabel;

    public CartItemPanel(String nama, int harga, String imgPath) {
        this.harga = harga;

        setLayout(new BorderLayout(10, 10));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        ImageIcon icon = new ImageIcon(imgPath);
        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(img));
        add(imgLabel, BorderLayout.WEST);

        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setOpaque(false);

        JLabel namaLabel = new JLabel(nama);
        namaLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel hargaLabel = new JLabel("Rp " + harga);

        qtySpinner = new JSpinner(
            new SpinnerNumberModel(1, 1, 99, 1)
        );

        subtotalLabel = new JLabel("Subtotal: Rp " + harga);

        qtySpinner.addChangeListener(e -> updateSubtotal());

        detail.add(namaLabel);
        detail.add(hargaLabel);
        detail.add(qtySpinner);
        detail.add(subtotalLabel);

        add(detail, BorderLayout.CENTER);
    }

    private void updateSubtotal() {
        int qty = (int) qtySpinner.getValue();
        subtotalLabel.setText("Subtotal: Rp " + (qty * harga));
    }
}
