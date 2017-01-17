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

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.undo.UndoHandler;
import gauge.messages.Api;

public class RenameInputValidator implements InputValidator {
    private final Module module;
    private Editor editor;
    private String text;
    private Project project;

    RenameInputValidator(Module module, Editor editor, String text, Project project) {
        this.module = module;
        this.editor = editor;
        this.text = text;
        this.project = project;
    }

    public boolean checkInput(String inputString) {
        return true;
    }

    public boolean canClose(final String inputString) {
        return doRename(inputString, editor);
    }

    private boolean doRename(final String inputString, final Editor editor) {
        CompilerManager.getInstance(project).make((aborted, errors, warnings, context) -> {
            if (errors > 0) {
                Messages.showErrorDialog(editor.getProject(), "Please fix all compilation errors before refactoring steps.", "Refactoring Failed");
                return;
            }
            TransactionGuard.submitTransaction(() -> {
            }, () -> {
                Api.PerformRefactoringResponse response;
                FileDocumentManager.getInstance().saveAllDocuments();
                try {
                    FileDocumentManager.getInstance().saveDocumentAsIs(editor.getDocument());
                    GaugeService gaugeService = Gauge.getGaugeService(module, true);
                    response = gaugeService.getGaugeConnection().sendPerformRefactoringRequest(text, inputString);
                } catch (Exception e) {
                    Messages.showErrorDialog(String.format("Could not execute refactor command: %s", e.toString()), "Refactoring Failed");
                    return;
                }
                new UndoHandler(response.getFilesChangedList(), module.getProject(), "Refactoring").handle();
                showMessage(response);
            });
        });
        return true;
    }

    private void showMessage(Api.PerformRefactoringResponse response) {
        Notification notification = new Notification("Gauge Refactoring", "Gauge", "Refactoring completed successfully", NotificationType.INFORMATION);
        if (!response.getSuccess()) {
            String message = String.join("\n", response.getErrorsList()).replace("<", "\"").replace(">", "\"");
            notification = new Notification("Gauge Refactoring", "Error: Gauge refactoring failed", message, NotificationType.ERROR);
        }
        Notifications.Bus.notify(notification, project);
    }
}
