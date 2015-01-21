package com.thoughtworks.gauge.rename;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameHandler;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import gauge.messages.Api;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class CustomRenameHandler implements RenameHandler {

    private PsiElement psiElement;
    private Editor editor;

    public boolean isAvailableOnDataContext(DataContext dataContext) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        this.editor = editor;
        if (element == null) {
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
        psiElement = element;
        String text = element.toString();
        //Finding text from annotation
        if (element.toString().equals("PsiAnnotation")) {
            if (element.getChildren()[2].getChildren()[1].getChildren()[0].getChildren().length == 1)
                text = element.getChildren()[2].getChildren()[1].getText().substring(1, element.getChildren()[2].getChildren()[1].getText().length() - 1);
            else {
                Messages.showWarningDialog("Refactoring for steps having aliases are not supported", "Warning");
                return;
            }
        } else if (element.getClass().equals(SpecStepImpl.class)) {
            text = ((SpecStepImpl) element).getStepValue().getStepAnnotationText();
        }
        Messages.showInputDialog(project, String.format("Refactoring \"%s\" to : ", text), "Refactor", Messages.getInformationIcon(), text,
                new RenameInputValidator(project, this.editor, text, this.psiElement));

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
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileDocumentManager.getInstance().saveDocumentAsIs(editor.getDocument());
                        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
                        GaugeService gaugeService = Gauge.getGaugeService(module);
                        Api.PerformRefactoringResponse response = gaugeService.getGaugeConnection().sendPerformRefactoringRequest(text, inputString);
                        refreshFiles();
                        showMessage(response);
                    } catch (Exception e) {
                        HintManager.getInstance().showErrorHint(editor, String.format("Could not execute refactor command: %s", e.toString()));
                    }
                }
            });
            return true;
        }

        private void refreshFiles() {
            VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
            LocalFileSystem.getInstance().refreshFiles(Arrays.asList(currentFile));
            LocalFileSystem.getInstance().refresh(false);
        }

        private void showMessage(Api.PerformRefactoringResponse response) throws IOException {
            if (!response.hasSuccess()) {
                String message = "";
                for (String error : response.getErrorsList()) {
                    message += error + "\n";
                }
                HintManager.getInstance().showErrorHint(this.editor, message);
            }
        }
    }
}