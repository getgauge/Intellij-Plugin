/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;

import java.io.IOException;

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

public class GaugeVersion {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.core.GaugeVersion");
    private static GaugeVersionInfo versionInfo = getVersion(true);

    public static GaugeVersionInfo getVersion(Boolean update) {
        if (!update) return versionInfo;
        GaugeVersionInfo gaugeVersionInfo = new GaugeVersionInfo();
        try {
            GaugeSettingsModel settings = getGaugeSettings();
            ProcessBuilder processBuilder = new ProcessBuilder(settings.getGaugePath(), Constants.VERSION, Constants.MACHINE_READABLE);
            GaugeUtil.setGaugeEnvironmentsTo(processBuilder, settings);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                String output = GaugeUtil.getOutput(process.getInputStream(), "\n");
                try {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    gaugeVersionInfo = gson.fromJson(output, GaugeVersionInfo.class);
                } catch (Exception e) {
                    LOG.error( String.format("Unable to parse <%s %s %s> command's output.\n%s", settings.getGaugePath(), Constants.VERSION, Constants.MACHINE_READABLE, output));
                    Notification notification = new Notification(
                            "Error",
                            String.format("Unable to parse <%s %s %s> command's output.", settings.getGaugePath(), Constants.VERSION, Constants.MACHINE_READABLE),
                            e.getMessage(),
                            NotificationType.ERROR
                    );
                    Notifications.Bus.notify(notification);
                }
            }
        } catch (InterruptedException | IOException | GaugeNotFoundException e) {
            String notificationTitle = "Unable to start Gauge Intellij plugin.";
            LOG.error(String.format("%s%s", notificationTitle, e.getMessage()));
            Notification notification = new Notification("Error", notificationTitle, e.getMessage(), NotificationType.ERROR);
            Notifications.Bus.notify(notification);
        }
        versionInfo = gaugeVersionInfo;
        return gaugeVersionInfo;
    }

    public static Boolean isGreaterOrEqual(String v1, Boolean update) {
        getVersion(update);
        return versionInfo.isGreaterOrEqual(new GaugeVersionInfo(v1));
    }
}

