package com.thoughtworks.gauge.rename;

import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.application.TransactionId;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.inspection.GaugeError;
import com.thoughtworks.gauge.undo.UndoHandler;
import com.thoughtworks.gauge.util.GaugeUtil;
import gauge.messages.Api;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;

class GaugeRefactorHandler {

    private final Project project;
    private final PsiFile file;
    private final Editor editor;

    GaugeRefactorHandler(Project project, PsiFile file, Editor editor) {
        this.project = project;
        this.file = file;
        this.editor = editor;
    }

    void compileAndRefactor(String currentStepText, String newStepText, @Nullable RefactorStatusCallback refactorStatusCallback) {
        refactorStatusCallback.onStatusChange("Compiling...");
        TransactionId contextTransaction = TransactionGuard.getInstance().getContextTransaction();
        CompilerManager.getInstance(project).make((aborted, errors, warnings, context) -> {
            if (errors > 0) {
                refactorStatusCallback.onFinish(new RefactoringStatus(false, "Please fix all errors before refactoring."));
                return;
            }
            refactor(currentStepText, newStepText, contextTransaction, context, refactorStatusCallback);
        });
    }

    private void refactor(String currentStepText, String newStepText, TransactionId contextTransaction, CompileContext context, RefactorStatusCallback refactorStatusCallback) {
        refactorStatusCallback.onStatusChange("Refactoring...");
        Module module = GaugeUtil.moduleForPsiElement(file);
        TransactionGuard.getInstance().submitTransaction(() -> {
        }, contextTransaction, () -> {
            Api.PerformRefactoringResponse response = null;
            FileDocumentManager.getInstance().saveAllDocuments();
            FileDocumentManager.getInstance().saveDocumentAsIs(editor.getDocument());
            GaugeService gaugeService = Gauge.getGaugeService(module, true);
            try {
                response = gaugeService.getGaugeConnection().sendPerformRefactoringRequest(currentStepText, newStepText);
            } catch (Exception e) {
                refactorStatusCallback.onFinish(new RefactoringStatus(false, String.format("Could not execute refactor command: %s", e.toString())));
                return;
            }
            new UndoHandler(response.getFilesChangedList(), module.getProject(), "Refactoring").handle();
            if (!response.getSuccess()) {
                showMessage(response, context, refactorStatusCallback);
                return;
            }
            refactorStatusCallback.onFinish(new RefactoringStatus(true));
        });
    }

    private void showMessage(Api.PerformRefactoringResponse response, CompileContext context, RefactorStatusCallback refactorStatusCallback) {
        refactorStatusCallback.onFinish(new RefactoringStatus(false, "Please fix all errors before refactoring."));
        for (String error : response.getErrorsList()) {
            GaugeError gaugeError = GaugeError.getInstance(error);
            context.addMessage(CompilerMessageCategory.ERROR, gaugeError.getMessage(), Paths.get(gaugeError.getFileName()).toUri().toString(), gaugeError.getLineNumber(), -1);
        }
    }
}

