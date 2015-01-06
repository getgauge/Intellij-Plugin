package com.thoughtworks.gauge.rename;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameHandler;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class CustomRenameHandler implements RenameHandler {

    private PsiElement psiElement;

    public boolean isAvailableOnDataContext(DataContext dataContext) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (element == null) {
            Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
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
        if (element.toString().equals("PsiAnnotation"))
            text = element.getChildren()[2].getChildren()[1].getText().substring(1, element.getChildren()[2].getChildren()[1].getText().length() - 1);
        else if (element.getClass().equals(SpecStepImpl.class))
            text = element.getText().replaceFirst("\\*", "").trim();
        Messages.showInputDialog(project, String.format("Refactoring \"%s\" to : ", text), "Refactor", Messages.getInformationIcon(), text, new RenameInputValidator(project, text));
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
        private String text;

        public RenameInputValidator(final Project project, String text) {
            this.project = project;
            this.text = text;
        }

        public boolean checkInput(String inputString) {
            return true;
        }

        public boolean canClose(final String inputString) {
            return doRename(inputString);
        }

        private boolean doRename(final String inputString) {
            String[] commands = new String[]{"gauge", "--refactor", text, inputString};
            try {
                Process exec = Runtime.getRuntime().exec(commands, null, new File(project.getBaseDir().getPath()));
                exec.waitFor();
                String errorMessage = "";
                errorMessage = getMessages(errorMessage, exec.getErrorStream());
                errorMessage = getMessages(errorMessage, exec.getInputStream());
                if (!errorMessage.equals("")) Messages.showWarningDialog(errorMessage, "Warning");
                return true;
            } catch (Exception e) {
                Messages.showWarningDialog("Cannot execute refactor command", "Warning");
                return false;
            }
        }

        private static String getMessages(String errorMessage, InputStream stream) throws IOException {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            while ((line = br.readLine()) != null)
                errorMessage += line;
            return errorMessage;
        }
    }
}