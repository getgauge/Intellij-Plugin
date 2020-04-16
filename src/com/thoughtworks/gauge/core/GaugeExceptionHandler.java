/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.core;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.thoughtworks.gauge.util.GaugeUtil;

import java.io.IOException;
import java.io.InputStream;

public class GaugeExceptionHandler extends Thread {

    private static final String LINE_BREAK = "\n";
    private static final String NOTIFICATION_TEMPLATE = "More details...<br><br>%s%s";
    private static final String NOTIFICATION_TITLE = "Exception occurred in Gauge plugin";
    private static final String ISSUE_TEMPLATE = "\n\nPlease log an issue in https://github.com/getgauge/intellij-plugin with following details:<br><br>" +
            "#### gauge process exited with code %d"+
            "<pre>```%s```" +
            "\n* Idea version: %s\n* API version: %s\n* Plugin version: %s\n* Gauge version: %s</pre>";
    private Process process;
    private Project project;
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.core.GaugeExceptionHandler");

    public GaugeExceptionHandler(Process process, Project project) {
        this.process = process;
        this.project = project;
    }

    @Override
    public void run() {
        String output = "";
        try {
            do {
                output = getOutput(output, process.getErrorStream());
                output = getOutput(output, process.getInputStream());
            } while (process.isAlive());
            if (process.exitValue() != 0 && !output.trim().equals("") && project.isOpen()) {
                LOG.debug(output);
                Notifications.Bus.notify(createNotification(output, process.exitValue()), project);
            }
        } catch (Exception ignored) {
            LOG.debug(ignored);
        }
    }

    private String getOutput(String output, InputStream stream) throws IOException {
        String lines = GaugeUtil.getOutput(stream, LINE_BREAK);
        return lines.trim().isEmpty() ? "" : String.format("%s%s%s", output, LINE_BREAK, lines);
    }

    private Notification createNotification(String stacktrace, int exitValue) {
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.findId("com.thoughtworks.gauge"));
        String pluginVersion = plugin == null ? "" : plugin.getVersion();
        String apiVersion = ApplicationInfo.getInstance().getApiVersion();
        String ideaVersion = ApplicationInfo.getInstance().getFullVersion();
        String gaugeVersion = GaugeVersion.getVersion(false).version;
        String body = String.format(ISSUE_TEMPLATE, exitValue,stacktrace, ideaVersion, apiVersion, pluginVersion, gaugeVersion);
        String content = String.format(NOTIFICATION_TEMPLATE, LINE_BREAK, body);
        return new Notification("Gauge Exception", NOTIFICATION_TITLE, content, NotificationType.ERROR, NotificationListener.URL_OPENING_LISTENER);
    }
}
