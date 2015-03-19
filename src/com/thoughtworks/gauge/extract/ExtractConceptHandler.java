package com.thoughtworks.gauge.extract;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import gauge.messages.Api;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtractConceptHandler implements RefactoringActionHandler {

    private SpecStepsBuilder builder;
    private PsiFile psiFile;
    private Editor editor;

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile psiFile, DataContext dataContext) {
        this.psiFile = psiFile;
        this.editor = editor;
        List<SpecStepImpl> specSteps = getSpecSteps(this.editor, this.psiFile);
        ExtractConceptInfoCollector collector = new ExtractConceptInfoCollector(editor, builder.getTextToTableMap(), specSteps);
        ExtractConceptInfo info = collector.getAllInfo();
        if (info.shouldContinue) {
            Api.ExtractConceptResponse response = makeExtractConceptRequest(specSteps, info.fileName, info.conceptName, false, psiFile);
            if (!response.getIsSuccess()) {
                HintManager.getInstance().showErrorHint(editor, response.getError());
            }
        }
    }

    private Api.ExtractConceptResponse makeExtractConceptRequest(List<SpecStepImpl> specSteps, String fileName, String concept, boolean refactorOtherUsages, PsiElement element) {
        int startLine = ((EditorImpl) editor).getSelectionModel().getSelectionStartPosition().getLine() + 1;
        int endLine = ((EditorImpl) editor).getSelectionModel().getSelectionEndPosition().getLine() + 1;
        Api.textInfo textInfo = gauge.messages.Api.textInfo.newBuilder().setFileName(psiFile.getVirtualFile().getPath()).setStartingLineNo(startLine).setEndLineNo(endLine).build();
        ExtractConceptRequest request = new ExtractConceptRequest(fileName, concept, refactorOtherUsages, textInfo);
        request.convertToSteps(specSteps, builder.getTableMap());
        return request.makeExtractConceptRequest(element);
    }

    private List<SpecStepImpl> getSpecSteps(Editor editor, PsiFile psiFile) {
        builder = new SpecStepsBuilder(editor, psiFile);
        return builder.build();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {
    }
}
