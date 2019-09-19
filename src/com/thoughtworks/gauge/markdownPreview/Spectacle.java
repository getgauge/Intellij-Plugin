package com.thoughtworks.gauge.markdownPreview;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.core.GaugeVersion;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.intellij.openapi.vcs.VcsNotifier.STANDARD_NOTIFICATION;

class Spectacle {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.markdownPreview.Spectacle");
    public static final String NAME = "spectacle";
    private final Project project;
    private GaugeSettingsModel settings;
    private static boolean installing = false;

    Spectacle(Project project, GaugeSettingsModel settings) {
        this.project = project;
        this.settings = settings;
    }


    private void install() {
        if (installing) {
            Notifications.Bus.notify(new Notification("Installing", "Installation in progress...", "Installing Plugin: Spectacle", NotificationType.INFORMATION));
            return;
        }
        installing = true;
        ProgressManager.getInstance().run(new Task.Backgroundable(this.project, "Installing Plugin : Spectacle", false) {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Installing plugin : Spectacle");
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(settings.getGaugePath(), Constants.INSTALL, NAME);
                    GaugeUtil.setGaugeEnvironmentsTo(processBuilder, settings);
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();
                    installing = false;
                    if (exitCode != 0)
                        throw new RuntimeException(GaugeUtil.getOutput(process.getInputStream(), "\n"));
                    Notifications.Bus.notify(new Notification("Successful", "Installation Completed", "Installation of plugin Spectacle is completed successfully", NotificationType.INFORMATION));
                } catch (Exception e) {
                    LOG.debug(e);
                    Notification notification = new Notification("Error", "Installation Failed", e.getMessage(), NotificationType.ERROR);
                    Notifications.Bus.notify(notification, project);
                }
                progressIndicator.cancel();
            }
        });
    }

    boolean isInstalled() throws IOException, InterruptedException {
        return GaugeVersion.getVersion(true).isPluginInstalled(NAME);
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
