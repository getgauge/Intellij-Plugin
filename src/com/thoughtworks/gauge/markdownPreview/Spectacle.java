package com.thoughtworks.gauge.markdownPreview;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.intellij.openapi.vcs.VcsNotifier.STANDARD_NOTIFICATION;
import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

class Spectacle {
    private final Project project;
    private GaugeSettingsModel settings;
    private boolean installing = false;

    Spectacle(Project project, GaugeSettingsModel settings) {
        this.project = project;
        this.settings = settings;
    }


    private void install() {
        if (installing) {
            Notifications.Bus.notify(new Notification("Installing", "Installation in progress...", "Installing Plugin: Spectacle", NotificationType.INFORMATION));
            return;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(this.project, "Installing Plugin : Spectacle", false) {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Installing plugin : Spectacle");
                String failureMessage = "Plugin installation unsuccessful";
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(settings.getGaugePath(), "install", "spectacle");
                    GaugeUtil.setGaugeEnvironmentsTo(processBuilder, settings);
                    processBuilder.directory(new File(project.getBasePath()));
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();
                    installing = false;
                    if (exitCode != 0) {
                        throw new RuntimeException(failureMessage);
                    } else {
                        Notifications.Bus.notify(new Notification("Successful", "Installation Completed", "Installation of plugin Spectacle is completed successfully", NotificationType.INFORMATION));
                    }
                } catch (Exception e) {
                    Messages.showWarningDialog("Failed to install plugin spectacle", "Error");
                }
                progressIndicator.cancel();
            }
        });
        installing = true;
    }

    boolean isInstalled() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(settings.getGaugePath(), "version", "-m");
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            String output = GaugeUtil.getOutput(process.getInputStream(), "\n");
            return output.contains("spectacle");
        }
        return false;
    }

    void notifyToInstall() {
        Notification notification = STANDARD_NOTIFICATION.createNotification("Error: Specification Preview", "Missing plugin: Spectacle. To install, run `gauge install spectacle` or click below", NotificationType.ERROR, null);
        notification.addAction(new NotificationAction("Install Spectacle") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                install();
                notification.expire();
            }
        });
        Notifications.Bus.notify(notification);
    }
}
