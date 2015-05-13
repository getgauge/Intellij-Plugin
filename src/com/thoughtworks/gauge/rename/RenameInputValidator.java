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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import gauge.messages.Api;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.openapi.vfs.LocalFileSystem.getInstance;

public class RenameInputValidator implements InputValidator {
    private final Module module;
    private Editor editor;
    private String text;
    private PsiElement psiElement;

    public RenameInputValidator(final Module module, Editor editor, String text, PsiElement psiElement) {
        this.module = module;
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
            GaugeService gaugeService = Gauge.getGaugeService(module);
            response = gaugeService.getGaugeConnection().sendPerformRefactoringRequest(text, inputString);
            refreshFiles(response);
        } catch (Exception e) {
            Messages.showErrorDialog(String.format("Could not execute refactor command: %s", e.toString()), "Rephrase Failed");
            return true;
        }
        runWriteAction(response);
        showMessage(response);
        return true;
    }

    private void refreshFiles(Api.PerformRefactoringResponse response) {
        final Map<Document, String> documentTextMap = new HashMap<Document, String>();
        for (String fileName : response.getFilesChangedList()) {
            VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(fileName));
            if (fileByIoFile != null) {
                Document document = FileDocumentManager.getInstance().getDocument(fileByIoFile);
                if (document != null) documentTextMap.put(document, document.getText());
            }
        }
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                for (Document document : documentTextMap.keySet())
                    document.setText(documentTextMap.get(document));
            }
        });
    }

    private void runWriteAction(final Api.PerformRefactoringResponse finalResponse) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(module.getProject(), new Runnable() {
                    @Override
                    public void run() {
                        performUndoableAction(finalResponse.getFilesChangedList());
                    }
                }, "Refactoring", "Refactoring");
            }
        });
    }

    private void performUndoableAction(List<String> filesChangedList) {
        for (String fileName : filesChangedList)
            try {
                VirtualFile virtualFile = getInstance().findFileByIoFile(new File(fileName));
                if (virtualFile != null) {
                    Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                    getInstance().refreshAndFindFileByIoFile(new File(fileName));
                    if (document != null) document.setText(FileUtils.readFileToString(new File(fileName)).replaceAll(System.getProperty("line.separator"), "\n"));
                }
            } catch (Exception ignored) {
            }
    }

    private void showMessage(Api.PerformRefactoringResponse response) {
        if (!response.getSuccess()) {
            String message = "";
            for (String error : response.getErrorsList()) message += error + "\n";
            HintManager.getInstance().showErrorHint(this.editor, message.replace("<", "\"").replace(">", "\""));
        }
    }
}
