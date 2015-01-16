package com.thoughtworks.gauge.rename;

import com.apple.eawt.Application;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.impl.CurrentEditorProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.psi.ExternalChangeAction;
import com.intellij.psi.ExternalChangeAction.ExternalDocumentChange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameHandler;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.jetbrains.annotations.NotNull;

import java.io.*;

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeExecPath;

public class CustomRenameHandler implements RenameHandler {

    private PsiElement psiElement;
    private Editor editor;

    public boolean isAvailableOnDataContext(DataContext dataContext) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (element == null) {
            Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
            this.editor = editor;
            if (editor == null) return false;
            int offset = editor.getCaretModel().getOffset();
            if (offset > 0 && offset == editor.getDocument().getTextLength()) offset--;
            PsiFile data = CommonDataKeys.PSI_FILE.getData(dataContext);
            if (data == null) return false;
            psiElement = getStepElement(data.findElementAt(offset));
            return psiElement != null && psiElement.getClass().equals(SpecStepImpl.class);
        }
        return CommonDataKeys.PROJECT.getData(dataContext) != null && (element.toString().equals("PsiAnnotation") ||
                element.getClass().equals(ConceptStepImpl.class) || element.getClass().equals(SpecStepImpl.class));
    }

    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (element == null) element = psiElement;
        String text = element.toString();
        //Finding text from annotation
        if (element.toString().equals("PsiAnnotation")) {
            if (element.getChildren()[2].getChildren()[1].getChildren()[0].getChildren().length == 1)
                text = element.getChildren()[2].getChildren()[1].getText().substring(1, element.getChildren()[2].getChildren()[1].getText().length() - 1);
            else {
                Messages.showWarningDialog("Refactoring for steps having aliases are not supported", "Warning");
                return;
            }
        } else if (element.getClass().equals(SpecStepImpl.class))
            text = element.getText().replaceFirst("\\*", "").trim();
        Messages.showInputDialog(project, String.format("Refactoring \"%s\" to : ", text), "Refactor", Messages.getInformationIcon(), text, new RenameInputValidator(project, this.editor, text));
    }

    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        invoke(project, null, null, dataContext);
    }

    private PsiElement getStepElement(PsiElement selectedElement) {
        if (selectedElement.getClass().equals(SpecStepImpl.class))
            return selectedElement;
        if (selectedElement.getParent() == null) return null;
        return getStepElement(selectedElement.getParent());
    }

    private static class RenameInputValidator implements InputValidator {
        private final Project project;
        private Editor editor;
        private String text;

        public RenameInputValidator(final Project project, Editor editor, String text) {
            this.project = project;
            this.editor = editor;
            this.text = text;
        }

        public boolean checkInput(String inputString) {
            return true;
        }

        public boolean canClose(final String inputString) {
            return doRename(inputString);
        }

        private boolean doRename(final String inputString) {
            final ProcessBuilder processBuilder = new ProcessBuilder(getGaugeExecPath(), "--refactor", text, inputString);
            processBuilder.directory(new File(project.getBaseDir().getPath()));

            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    try {
                        Process process = processBuilder.start();
                        if (process.waitFor() != 0) {
                            String errorMessage = "";
                            errorMessage = getMessages(errorMessage, process.getInputStream());
                            int flags = HintManager.HIDE_BY_ANY_KEY | HintManager.HIDE_BY_TEXT_CHANGE | HintManager.HIDE_BY_SCROLLING;
                            int timeout = 0;
                            HintManager.getInstance().showErrorHint(editor, errorMessage,
                                    editor.getCaretModel().getOffset(), editor.getCaretModel().getOffset() + 1,
                                    HintManager.ABOVE, flags, timeout);
                        }
                        LocalFileSystem.getInstance().refresh(false);
                    } catch (Exception e) {
                        Messages.showInfoMessage(String.format("Could not execute refactor command: %s", e.getMessage()), "Warning");
                    }
                }
            });
            return true;
        }

        private static String getMessages(String errorMessage, InputStream stream) throws IOException {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            while ((line = br.readLine()) != null)
                errorMessage += line + "\n";
            return errorMessage;
        }
    }
}