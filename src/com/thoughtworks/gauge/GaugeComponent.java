package com.thoughtworks.gauge;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeVersion;

import java.io.File;
import java.util.Arrays;

import static com.thoughtworks.gauge.Constants.MIN_GAUGE_VERSION;
import static com.thoughtworks.gauge.util.GaugeUtil.isGaugeProjectDir;

public class GaugeComponent implements ProjectComponent {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.GaugeComponent");
    private Project project;

    public GaugeComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        if (isGaugeProjectDir(new File(this.project.getBasePath()))) {
            if (!GaugeVersion.isGreaterOrEqual(MIN_GAUGE_VERSION, false)) {
                String notificationTitle = String.format("Unsupported Gauge Version(%s)", GaugeVersion.getVersion(false).version);
                String errorMessage = String.format("This version of Gauge Intellij plugin only works with Gauge version >= %s", MIN_GAUGE_VERSION);
                LOG.debug(String.format("%s\n%s", notificationTitle, errorMessage));
                Notification notification = new Notification("Error", notificationTitle, errorMessage, NotificationType.ERROR);
                Notifications.Bus.notify(notification, this.project);
            }
        }
    }

    @Override
    public void projectClosed() {
        Arrays.stream(ModuleManager.getInstance(project).getModules()).forEach(Gauge::disposeComponent);
    }
}
