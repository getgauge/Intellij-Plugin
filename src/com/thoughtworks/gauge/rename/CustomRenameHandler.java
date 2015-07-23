// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.rename;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.rename.RenameHandler;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.thoughtworks.gauge.util.StepUtil.*;

public class CustomRenameHandler implements RenameHandler {

    private PsiElement psiElement;
    private Editor editor;

    public boolean isAvailableOnDataContext(DataContext dataContext) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        if(file != null && !GaugeUtil.isGaugeFile(file)) return false;
        this.editor = editor;
        if (element == null) {
            if (editor == null) return false;
            int offset = editor.getCaretModel().getOffset();
            if (offset > 0 && offset == editor.getDocument().getTextLength()) offset--;
            PsiFile data = CommonDataKeys.PSI_FILE.getData(dataContext);
            if (data == null) return false;
            psiElement = getStepElement(data.findElementAt(offset));
            return psiElement != null && (isConcept(psiElement) || isStep(psiElement));
        }
        boolean isAvailable = CommonDataKeys.PROJECT.getData(dataContext) != null && (isMethod(element) || isConcept(element) || isStep(element));
        if (isAvailable) makeProject(CommonDataKeys.PROJECT.getData(dataContext));
        return isAvailable;
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
        if (isMethod(element)) {
            List<String> values = StepUtil.getGaugeStepAnnotationValues((PsiMethod) element);
            if (values.size() == 0) {
                return;
            } else if (values.size() == 1)
                text = values.get(0);
            else if (values.size() > 1) {
                Messages.showWarningDialog("Refactoring for steps having aliases are not supported", "Warning");
                return;
            }
        } else if (isStep(element)) {
            text = ((SpecStepImpl) element).getStepValue().getStepAnnotationText();
        } else if (isConcept(element)) {
            text = removeIdentifiers(((ConceptStepImpl) element).getStepValue().getStepAnnotationText());
        }
        Messages.showInputDialog(project, String.format("Refactoring \"%s\" to : ", text), "Refactor", Messages.getInformationIcon(), text,
                new RenameInputValidator(GaugeUtil.moduleForPsiElement(file), this.editor, text, this.psiElement));

    }

    private String removeIdentifiers(String text) {
        text = text.trim();
        return text.charAt(0) == '*' || text.charAt(0) == '#' ? text.substring(1).trim() : text;
    }

    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        invoke(project, null, null, dataContext);
    }

    private PsiElement getStepElement(PsiElement selectedElement) {
        if (selectedElement.getClass().equals(SpecStepImpl.class) || selectedElement.getClass().equals(ConceptStepImpl.class))
            return selectedElement;
        if (selectedElement.getParent() == null) return null;
        return getStepElement(selectedElement.getParent());
    }

    private void makeProject(Project project) {
        CompilerManager.getInstance(project).make(new CompileStatusNotification() {
            @Override
            public void finished(boolean b, int i, int i1, CompileContext compileContext) {
            }
        });
    }
}