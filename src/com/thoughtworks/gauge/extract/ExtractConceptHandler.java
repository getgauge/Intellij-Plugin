package com.thoughtworks.gauge.extract;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.extract.stepBuilder.StepsBuilder;
import gauge.messages.Api;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtractConceptHandler {
    private StepsBuilder builder;
    private PsiFile psiFile;
    private Editor editor;

    public void invoke(@NotNull final Project project, @NotNull final Editor editor, @NotNull final PsiFile psiFile) {
        this.psiFile = psiFile;
        this.editor = editor;
        try {
            List<PsiElement> steps = getSteps(this.editor, this.psiFile);
            if (steps.size() == 0) throw new RuntimeException("Invalid selection");
            ExtractConceptInfoCollector collector = new ExtractConceptInfoCollector(editor, builder.getTextToTableMap(), steps, project);
            ExtractConceptInfo info = collector.getAllInfo();
            if (info.shouldContinue) {
                Api.ExtractConceptResponse response = makeExtractConceptRequest(steps, info.fileName, info.conceptName, false, psiFile);
                if (!response.getIsSuccess()) throw new RuntimeException(response.getError());
                VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
            }
        } catch (Exception e) {
            HintManager.getInstance().showErrorHint(editor, e.getMessage());
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
}