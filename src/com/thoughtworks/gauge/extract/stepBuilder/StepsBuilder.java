package com.thoughtworks.gauge.extract.stepBuilder;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.ConceptFileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class StepsBuilder {
    protected final Editor editor;
    protected final PsiFile psiFile;
    protected Map<String, String> tableMap = new HashMap<String, String>();
    protected Map<String, String> TextToTableMap = new HashMap<String, String>();


    public StepsBuilder(Editor editor, PsiFile psiFile) {
        this.editor = editor;
        this.psiFile = psiFile;
    }

    public Map<String, String> getTableMap() {
        return tableMap;
    }

    public Map<String, String> getTextToTableMap() {
        return TextToTableMap;
    }

    public List<PsiElement> build() {
        return null;
    }

    public static StepsBuilder getBuilder(Editor editor, PsiFile psiFile) {
        if (psiFile.getFileType().getClass().equals(ConceptFileType.class))
            return new ConceptStepsBuilder(editor, psiFile);
        return new SpecStepsBuilder(editor, psiFile);
    }

    protected PsiElement getStep(PsiElement element, Class stepClass) {
        if (element.getParent() == null) return null;
        if (element.getParent().getClass().equals(stepClass))
            return element.getParent();
        return getStep(element.getParent(), stepClass);
    }

    protected List<PsiElement> getPsiElements(Class stepClass) {
        SelectionModel selectionModel = editor.getSelectionModel();
        List<PsiElement> specSteps = new ArrayList<PsiElement>();
        int currentOffset = selectionModel.getSelectionStart();
        while (selectionModel.getSelectionEnd() >= currentOffset) {
            if (psiFile.getText().charAt(currentOffset++) == '\n') continue;
            PsiElement step = getStep(psiFile.findElementAt(currentOffset), stepClass);
            if (step == null) {
                HintManager.getInstance().showErrorHint(editor, "Cannot extract concept, selected text contains invalid elements");
                return null;
            }
            specSteps.add(step);
            currentOffset += step.getText().length();
        }
        return specSteps;
    }
}
