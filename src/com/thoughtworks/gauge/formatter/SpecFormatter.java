/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.formatter;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;

import java.io.File;

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

public class SpecFormatter extends AnAction {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.formatter.SpecFormatter");
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        String projectDir = project.getBasePath();
        if (projectDir == null) {
            return;
        }

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile selectedFile = fileEditorManager.getSelectedFiles()[0];
        String fileName = selectedFile.getCanonicalPath();
        Document doc = FileDocumentManager.getInstance().getDocument(selectedFile);
        if (doc != null) {
            FileDocumentManager.getInstance().saveDocument(doc);
        }
        try {
            GaugeSettingsModel settings = getGaugeSettings();
            ProcessBuilder processBuilder = new ProcessBuilder(settings.getGaugePath(), Constants.FORMAT, fileName);
            GaugeUtil.setGaugeEnvironmentsTo(processBuilder, settings);
            processBuilder.directory(new File(projectDir));
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String output = String.format("<pre>%s</pre>", GaugeUtil.getOutput(process.getInputStream(), "\n").replace("<", "&lt;").replace(">", "&gt;"));
                Notifications.Bus.notify(new Notification("Spec Formatting", "Error: Spec Formatting", output, NotificationType.ERROR));
                return;
            }
            VirtualFileManager.getInstance().syncRefresh();
            selectedFile.refresh(false, false);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.debug(e);
            Messages.showErrorDialog("Error on formatting spec", "Format Error");
        }
    }
}
