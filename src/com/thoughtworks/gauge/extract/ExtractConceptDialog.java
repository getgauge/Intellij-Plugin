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
    private com.intellij.ui.TextFieldWithAutoCompletion textField2;
    private Project project;
    private List<String> args;
    private List<String> dirNames;

    public ExtractConceptDialog(Project project, List<String> args, List<String> dirNames) {
        this.project = project;
        this.args = args;
        this.dirNames = dirNames;
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
        this.textField2.setEnabled(false);
        this.comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtractConceptDialog.this.textField2.setEnabled(false);
                if (ExtractConceptDialog.this.comboBox1.getSelectedItem().toString().equals(ExtractConceptInfoCollector.CREATE_NEW_FILE))
                    ExtractConceptDialog.this.textField2.setEnabled(true);
            }
        });
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public void setData(String data, List<String> files) {
        this.textArea1.setColumns(50);
        this.textArea1.setRows(10);
        this.textArea1.setEditable(false);
        this.textArea1.setText(data);
        for (String file : files) {
            this.comboBox1.addItem(file);
        }
    }

    public ExtractConceptInfo getInfo() {
        String fileName = this.comboBox1.getSelectedItem().toString();
        if (fileName.equals(ExtractConceptInfoCollector.CREATE_NEW_FILE)) fileName = this.textField2.getText();
        return new ExtractConceptInfo(this.textField1.getText(), fileName, true);
    }

    private void createUIComponents() {
        this.textField1 = new com.intellij.ui.TextFieldWithAutoCompletion<String>(this.project, getAutoCompleteTextField(this.args),true,"");
        this.textField2 = new com.intellij.ui.TextFieldWithAutoCompletion<String>(this.project, getAutoCompleteTextField(this.dirNames),true,"");
    }

    private TextFieldWithAutoCompletionListProvider<String> getAutoCompleteTextField(final List<String> dirNames) {
        return new TextFieldWithAutoCompletionListProvider<String>(dirNames) {
            @Nullable
            @Override
            protected Icon getIcon(String o) {
                return null;
            }

            @NotNull
            @Override
            protected String getLookupString(String o) {
                return o;
            }

            @Nullable
            @Override
            protected String getTailText(String o) {
                return null;
            }

            @Nullable
            @Override
            protected String getTypeText(String o) {
                return null;
            }

            @Override
            public int compare(String o, String t1) {
                return 0;
            }
        };
    }
}
