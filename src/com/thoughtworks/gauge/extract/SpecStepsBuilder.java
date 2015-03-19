package com.thoughtworks.gauge.extract;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecStepsBuilder {
    private final Editor editor;
    private final PsiFile psiFile;
    private Map<String, String> tableMap = new HashMap<String, String>();
    private Map<String, String> TextToTableMap = new HashMap<String, String>();


    public SpecStepsBuilder(Editor editor, PsiFile psiFile) {
        this.editor = editor;
        this.psiFile = psiFile;
    }

    public Map<String, String> getTableMap() {
        return tableMap;
    }

    public Map<String, String> getTextToTableMap() {
        return TextToTableMap;
    }

    public List<SpecStepImpl> build() {
        SelectionModel selectionModel = editor.getSelectionModel();
        List<SpecStepImpl> specSteps = new ArrayList<SpecStepImpl>();
        int currentOffset = selectionModel.getSelectionStart();
        while (selectionModel.getSelectionEnd() >= currentOffset) {
            if (psiFile.getText().charAt(currentOffset++) == '\n') continue;
            SpecStepImpl step = getSpecStep(psiFile.findElementAt(currentOffset));
            if (step == null) {
                HintManager.getInstance().showErrorHint(editor, "Cannot extract concept, selected text contains invalid elements");
                return null;
            }
            specSteps.add(step);
            currentOffset += step.getText().length();
        }
        Integer count = 0;
        for (SpecStepImpl specStep : specSteps)
            if (specStep.getInlineTable() != null && TextToTableMap.get(specStep.getInlineTable().getText().trim()) == null) {
                tableMap.put("table" + (++count).toString(), specStep.getInlineTable().getText().trim());
                TextToTableMap.put(specStep.getInlineTable().getText().trim(), "table" + (count).toString());
            }
        return specSteps;
    }

    private SpecStepImpl getSpecStep(PsiElement element) {
        if (element.getParent() == null) return null;
        if (element.getParent().getClass().equals(SpecStepImpl.class))
            return (SpecStepImpl) element.getParent();
        return getSpecStep(element.getParent());
    }

}
