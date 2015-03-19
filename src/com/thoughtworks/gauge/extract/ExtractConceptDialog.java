package com.thoughtworks.gauge.extract;

import javax.swing.*;
import java.awt.event.*;

public class ExtractConceptDialog extends JDialog {
    private JPanel contentPane;
    private JTextField textField1;
    private JTextArea textArea1;
    private JComboBox comboBox1;

    public ExtractConceptDialog() {
        setContentPane(contentPane);
        setModal(true);

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        ExtractConceptDialog dialog = new ExtractConceptDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void setData(String data, java.util.List<String> files) {
        this.textArea1.setColumns(50);
        this.textArea1.setRows(10);
        this.textArea1.setEditable(false);
        this.textArea1.setText(data);
        for (String file : files) {
            this.comboBox1.addItem(file);
        }
    }

    public ExtractConceptInfo getInfo() {
        return new ExtractConceptInfo(this.textField1.getText(), this.comboBox1.getSelectedItem().toString(), true);
    }
}
