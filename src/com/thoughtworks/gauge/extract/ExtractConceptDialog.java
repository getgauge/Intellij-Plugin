package com.thoughtworks.gauge.extract;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class ExtractConceptDialog extends JDialog {
    private JPanel contentPane;
    private com.intellij.ui.TextFieldWithAutoCompletion textField1;
    private JTextArea textArea1;
    private JComboBox<String> comboBox1;
    private Project project;
    private List<String> args;

    public ExtractConceptDialog(Project project, List<String> args) {
        this.project = project;
        this.args = args;
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

    private void createUIComponents() {
        this.textField1 = new com.intellij.ui.TextFieldWithAutoCompletion(this.project, new TextFieldWithAutoCompletionListProvider(this.args) {
            @Nullable
            @Override
            protected Icon getIcon(Object o) {
                return null;
            }

            @NotNull
            @Override
            protected String getLookupString(Object o) {
                return o.toString();
            }

            @Nullable
            @Override
            protected String getTailText(Object o) {
                return null;
            }

            @Nullable
            @Override
            protected String getTypeText(Object o) {
                return null;
            }

            @Override
            public int compare(Object o, Object t1) {
                return 0;
            }
        },true,"");
    }
}
