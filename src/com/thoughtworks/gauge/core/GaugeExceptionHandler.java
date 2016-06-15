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

package com.thoughtworks.gauge.core;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.extensions.PluginId;
import com.thoughtworks.gauge.util.GaugeUtil;

public class GaugeExceptionHandler extends Thread {

    private static final String LINE_BREAK = "\n";
    private static final String NOTIFICATION_TEMPLATE = "Please log an issue <a href=\"https://github.com/getgauge/Intellij-Plugin/issues/new\">here</a> with the following details.%s%s";
    private static final String NOTIFICATION_TITLE = "Exception occurred in Gauge plugin";
    private static final String ISSUE_TEMPLATE = "<pre>```%s```\n* Idea version: %s\n* API version: %s\n* Plugin version: %s\n* Gauge version: %s</pre>";
    private Process process;

    public GaugeExceptionHandler(Process process) {
        this.process = process;
    }

    @Override
    public void run() {
        String output = "";
        try {
            while(process.isAlive())
                output = String.format("%s%s%s", output, LINE_BREAK, GaugeUtil.getOutput(process.getErrorStream(), LINE_BREAK));
            if (process.exitValue() != 0) {
                Notifications.Bus.notify(createNotification(output));
            }
        } catch (Exception ignored) {
        }
    }

    private Notification createNotification(String stacktrace) {
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.findId("com.thoughtworks.gauge"));
        String pluginVersion = plugin == null ? "" : plugin.getVersion();
        String apiVersion = ApplicationInfo.getInstance().getApiVersion();
        String ideaVersion = ApplicationInfo.getInstance().getFullVersion();
        String gaugeVersion = GaugeVersion.versionInfo.version;
        String body = String.format(ISSUE_TEMPLATE, stacktrace, ideaVersion, apiVersion, pluginVersion, gaugeVersion);
        String content = String.format(NOTIFICATION_TEMPLATE, LINE_BREAK, body);
        return new Notification("Gauge Exception", NOTIFICATION_TITLE, content, NotificationType.ERROR, NotificationListener.URL_OPENING_LISTENER);
    }
}
