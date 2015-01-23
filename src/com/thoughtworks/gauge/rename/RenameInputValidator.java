package com.thoughtworks.gauge.rename;

import com.google.protobuf.ProtocolStringList;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import gauge.messages.Api;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static com.intellij.openapi.vfs.LocalFileSystem.getInstance;

public class RenameInputValidator implements InputValidator {
    private final Project project;
    private Editor editor;
    private String text;
    private PsiElement psiElement;

    public RenameInputValidator(final Project project, Editor editor, String text, PsiElement psiElement) {
        this.project = project;
        this.editor = editor;
        this.text = text;
        this.psiElement = psiElement;
    }

    public boolean checkInput(String inputString) {
        return true;
    }

    public boolean canClose(final String inputString) {
        return doRename(inputString, editor, psiElement);
    }

    private boolean doRename(final String inputString, final Editor editor, final PsiElement psiElement) {
        Api.PerformRefactoringResponse response = null;
        try {
            FileDocumentManager.getInstance().saveDocumentAsIs(editor.getDocument());
            final Module module = ModuleUtil.findModuleForPsiElement(psiElement);
            GaugeService gaugeService = Gauge.getGaugeService(module);
            response = gaugeService.getGaugeConnection().sendPerformRefactoringRequest(text, inputString);
        } catch (Exception e) {
            HintManager.getInstance().showErrorHint(editor, String.format("Could not execute refactor command: %s", e.toString()));
        }
        runWriteAction(response);
        showMessage(response);
        return true;
    }

    private void runWriteAction(final Api.PerformRefactoringResponse finalResponse) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        refreshFiles(finalResponse.getFilesChangedList());
                    }
                }, "Refactoring", "Refactoring");
            }
        });
    }

    private void refreshFiles(ProtocolStringList filesChangedList) {
        for (String fileName : filesChangedList)
            try {
                VirtualFile virtualFile = getInstance().findFileByIoFile(new File(fileName));
                if (virtualFile != null) {
                    Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                    getInstance().refreshAndFindFileByIoFile(new File(fileName));
                    if (document != null) document.setText(FileUtils.readFileToString(new File(fileName)));
                }
            } catch (Exception ignored) {
            }
    }

    private void showMessage(Api.PerformRefactoringResponse response) {
        if (!response.getSuccess()) {
            String message = "";
            for (String error : response.getErrorsList()) message += error + "\n";
            HintManager.getInstance().showErrorHint(this.editor, message.replace("<", "\"").replace(">", "\""));
        }
    }
}
