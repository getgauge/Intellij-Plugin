package com.thoughtworks.gauge.extract.stepBuilder;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;

import java.util.List;

public class SpecStepsBuilder extends StepsBuilder {

    public SpecStepsBuilder(Editor editor, PsiFile psiFile) {
        super(editor, psiFile);
    }

    public List<PsiElement> build() {
        List<PsiElement> specSteps = getPsiElements(SpecStepImpl.class);
        Integer count = 0;
        for (PsiElement element : specSteps) {
            SpecStepImpl specStep = (SpecStepImpl) element;
            if (specStep.getInlineTable() != null && TextToTableMap.get(specStep.getInlineTable().getText().trim()) == null) {
                tableMap.put("table" + (++count).toString(), specStep.getInlineTable().getText().trim());
                TextToTableMap.put(specStep.getInlineTable().getText().trim(), "table" + (count).toString());
            }
        }
        return specSteps;
    }
}
