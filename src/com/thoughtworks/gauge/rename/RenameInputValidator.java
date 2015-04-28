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

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.undo.UndoHandler;
import gauge.messages.Api;

public class RenameInputValidator implements InputValidator {
    private final Project project;
    private Editor editor;
    private String text;
    private PsiElement psiElement;

    public RenameInputValidator(final Project project, Editor editor, String text, PsiElement psiElement) {
        this.project = project;
        this.editor = editor;
        this.text = text;
        this.psiElement = psiElement;
    }

    public boolean checkInput(String inputString) {
        return true;
    }

    public boolean canClose(final String inputString) {
        return doRename(inputString, editor, psiElement);
    }

    private boolean doRename(final String inputString, final Editor editor, final PsiElement psiElement) {
        Api.PerformRefactoringResponse response = null;
        FileDocumentManager.getInstance().saveAllDocuments();
        try {
            FileDocumentManager.getInstance().saveDocumentAsIs(editor.getDocument());
            final Module module = ModuleUtil.findModuleForPsiElement(psiElement);
            GaugeService gaugeService = Gauge.getGaugeService(module);
            response = gaugeService.getGaugeConnection().sendPerformRefactoringRequest(text, inputString);
        } catch (Exception e) {
            HintManager.getInstance().showErrorHint(editor, String.format("Could not execute refactor command: %s", e.toString()));
        }
        new UndoHandler(response.getFilesChangedList(), project, "Refactoring").handle();
        showMessage(response);
        return true;
    }

    private void showMessage(Api.PerformRefactoringResponse response) {
        if (!response.getSuccess()) {
            String message = "";
            for (String error : response.getErrorsList()) message += error + "\n";
            HintManager.getInstance().showErrorHint(this.editor, message.replace("<", "\"").replace(">", "\""));
        }
    }
}
