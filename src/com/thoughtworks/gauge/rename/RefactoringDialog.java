package com.thoughtworks.gauge.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RefactoringDialog extends DialogWrapper {
    private EditorTextField inputText;
    private JPanel contentPane;
    private JLabel heading;
    private JLabel infoPane;
    private Project project;
    private PsiFile file;
    private Editor editor;
    private String text;

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return inputText;
    }

    public RefactoringDialog(Project project, PsiFile file, Editor editor, String text) {
        super(project);
        this.project = project;
        this.file = file;
        this.editor = editor;
        this.text = text;
        setModal(true);
        setTitle("Refactoring");
        this.heading.setText(String.format("Refactoring \"%s\" to :", this.text));
        this.inputText.setText(this.text);
        setSize();
        registerActions();
        repaint();
        init();
        addListeners();
    }

    private void registerActions() {
        this.contentPane.registerKeyboardAction(e -> doOKAction(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void addListeners() {
        addResizeListener();
        addInputFocusListener();
    }

    private void addResizeListener() {
        final ComponentAdapter resizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                Dimension contentSize = contentPane.getSize();
                Dimension componentSize = event.getComponent().getSize();
                if (componentSize.getWidth() > contentSize.getWidth()) {
                    event.getComponent().setSize(new Dimension((int) contentSize.getWidth() - 20, (int) componentSize.getHeight()));
                }
            }
        };
        this.inputText.addComponentListener(resizeListener);
        this.heading.addComponentListener(resizeListener);
    }

    private void addInputFocusListener() {
        inputText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                inputText.removeSelection();
                inputText.setCaretPosition(0);
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
    }

    @Override
    protected void doOKAction() {
        setOKActionEnabled(false);
        this.infoPane.setEnabled(true);
        this.infoPane.setVisible(true);
        String inputString = inputText.getText();
        new GaugeRefactorHandler(project, file, editor).compileAndRefactor(text, inputString, new RefactorStatusCallback() {
            @Override
            public void onStatusChange(String statusMessage) {
                infoPane.setText(statusMessage);
            }

            @Override
            public void onFinish(RefactoringStatus refactoringStatus) {
                if (refactoringStatus.isPassed()) {
                    doCancelAction();
                } else {
                    getRootPane().setDefaultButton(getButton(myCancelAction));
                    infoPane.setVisible(false);
                    setErrorText(refactoringStatus.getErrorMessage());
                }
            }
        });
    }

    @Override
    public void doCancelAction() {
        this.getWindow().setVisible(false);
        dispose();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private void setSize() {
        int stringWidth = SwingUtilities.computeStringWidth(this.heading.getFontMetrics(this.heading.getFont()), this.heading.getText());
        int height = this.contentPane.getHeight();
        this.contentPane.setSize(new Dimension(stringWidth + 30, height));
        this.contentPane.setMinimumSize(new Dimension(stringWidth + 30, height));
    }

    private void createUIComponents() {
        this.inputText = new EditorTextField();
    }
}
