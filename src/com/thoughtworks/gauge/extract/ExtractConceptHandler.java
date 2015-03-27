package com.thoughtworks.gauge.extract;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import com.thoughtworks.gauge.extract.stepBuilder.StepsBuilder;
import gauge.messages.Api;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtractConceptHandler implements RefactoringActionHandler {

    private StepsBuilder builder;
    private PsiFile psiFile;
    private Editor editor;

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile psiFile, final DataContext dataContext) {
        this.psiFile = psiFile;
        this.editor = editor;
        try {
            List<PsiElement> specSteps = getSteps(this.editor, this.psiFile);
            ExtractConceptInfoCollector collector = new ExtractConceptInfoCollector(editor, builder.getTextToTableMap(), specSteps);
            ExtractConceptInfo info = collector.getAllInfo();
            if (info.shouldContinue) {
                Api.ExtractConceptResponse response = makeExtractConceptRequest(specSteps, info.fileName, info.conceptName, false, psiFile);
                if (!response.getIsSuccess()) {
                    HintManager.getInstance().showErrorHint(editor, response.getError());
                    return;
                }
                VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
            }
        } catch (Exception e) {
            HintManager.getInstance().showErrorHint(editor, "Cannot extract Concept.");
        }
    }

    private Api.ExtractConceptResponse makeExtractConceptRequest(List<PsiElement> specSteps, String fileName, String concept, boolean refactorOtherUsages, final PsiFile element) {
        int startLine = editor.getSelectionModel().getSelectionStartPosition().getLine() + 1;
        String selectedText = editor.getSelectionModel().getSelectedText();
        int endLine = startLine + (selectedText != null ? selectedText.split("\n").length : 0) - 1;
        Api.textInfo textInfo = gauge.messages.Api.textInfo.newBuilder().setFileName(psiFile.getVirtualFile().getPath()).setStartingLineNo(startLine).setEndLineNo(endLine).build();
        ExtractConceptRequest request = new ExtractConceptRequest(fileName, concept, refactorOtherUsages, textInfo);
        request.convertToSteps(specSteps, builder.getTableMap());
        return request.makeExtractConceptRequest(element);
    }

    private List<PsiElement> getSteps(Editor editor, PsiFile psiFile) {
        builder = StepsBuilder.getBuilder(editor, psiFile);
        return builder.build();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {
    }
}