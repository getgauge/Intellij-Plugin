package com.thoughtworks.gauge.rename;

import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.application.TransactionId;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorTextField;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.inspection.GaugeError;
import com.thoughtworks.gauge.undo.UndoHandler;
import com.thoughtworks.gauge.util.GaugeUtil;
import gauge.messages.Api;
import org.jetbrains.annotations.Nullable;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Paths;

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
        this.contentPane.registerKeyboardAction(e -> doOKAction(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
        compileAndRefactor(inputString);
    }

    private void compileAndRefactor(String inputString) {
        this.infoPane.setText("Compiling...");
        TransactionId contextTransaction = TransactionGuard.getInstance().getContextTransaction();
        Module module = GaugeUtil.moduleForPsiElement(file);
        CompilerManager.getInstance(project).make(module, (aborted, errors, warnings, context) -> {
            if (errors > 0) {
                showErrorMessage("Please fix all errors before refactoring.");
                return;
            }
            refactor(inputString, contextTransaction, module, context);
        });
    }

    private void refactor(String inputString, TransactionId contextTransaction, Module module, CompileContext context) {
        this.infoPane.setText("Refactoring...");
        TransactionGuard.getInstance().submitTransaction(() -> {
        }, contextTransaction, () -> {
            Api.PerformRefactoringResponse response = null;
            FileDocumentManager.getInstance().saveAllDocuments();
            FileDocumentManager.getInstance().saveDocumentAsIs(editor.getDocument());
            GaugeService gaugeService = Gauge.getGaugeService(module, true);
            try {
                response = gaugeService.getGaugeConnection().sendPerformRefactoringRequest(text, inputString);
            } catch (Exception e) {
                showErrorMessage(String.format("Could not execute refactor command: %s", e.toString()));
                return;
            }
            new UndoHandler(response.getFilesChangedList(), module.getProject(), "Refactoring").handle();
            showMessage(response, context);
            onCancel();
        });
    }

    private void showMessage(Api.PerformRefactoringResponse response, CompileContext context) {
        if (!response.getSuccess()) {
            for (String error : response.getErrorsList()) {
                GaugeError gaugeError = GaugeError.getInstance(error);
                context.addMessage(CompilerMessageCategory.ERROR, gaugeError.getMessage(), Paths.get(gaugeError.getFileName()).toUri().toString(), gaugeError.getLineNumber(), -1);
            }
        }
    }

    private void showErrorMessage(String text) {
        getRootPane().setDefaultButton(getButton(myCancelAction));
        this.infoPane.setVisible(false);
        setErrorText(text);
    }

    @Override
    public void doCancelAction() {
        onCancel();
    }

    private void onCancel() {
        this.getWindow().setVisible(false);
        dispose();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private void setSize() {
        int stringWidth = SwingUtilities2.stringWidth(this.heading, this.heading.getFontMetrics(this.heading.getFont()), this.heading.getText());
        int height = this.contentPane.getHeight();
        this.contentPane.setSize(new Dimension(stringWidth + 30, height));
        this.contentPane.setMinimumSize(new Dimension(stringWidth + 30, height));
    }

    private void createUIComponents() {
        this.inputText = new EditorTextField();
    }
}
