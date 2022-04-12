/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package software.controladores;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author MSI
 */
public class ConfiguracionElementos {

    static class CustomComboBox extends JLabel implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            JLabel label = new JLabel() {
                public Dimension getPreferredSize() {
                    return new Dimension(200, 30);
                }
            };
            label.setText(String.valueOf(value));
            label.setFont(new Font("", Font.BOLD, 14));

            return label;
        }
    }
}
