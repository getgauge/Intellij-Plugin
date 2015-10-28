package com.thoughtworks.gauge.extract.stepBuilder;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;

import java.util.ArrayList;
import java.util.List;

public class ConceptStepsBuilder extends StepsBuilder {
    public ConceptStepsBuilder(Editor editor, PsiFile psiFile) {
        super(editor, psiFile);
    }

    @Override
    public List<PsiElement> build() {
        List<PsiElement> specSteps = getPsiElements(ConceptStepImpl.class);
        Integer count = 0;
        for (PsiElement element : specSteps) {
            ConceptStepImpl specStep = (ConceptStepImpl) element;
            if (specStep.getTable() != null && TextToTableMap.get(specStep.getTable().getText().trim()) == null) {
                tableMap.put("table" + (++count).toString(), specStep.getTable().getText().trim());
                TextToTableMap.put(specStep.getTable().getText().trim(), "table" + (count).toString());
            }
        }
        return specSteps;
    }
}
