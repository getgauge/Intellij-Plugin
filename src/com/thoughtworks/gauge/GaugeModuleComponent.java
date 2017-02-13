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

package com.thoughtworks.gauge;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.thoughtworks.gauge.connection.GaugeConnection;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeExceptionHandler;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.core.GaugeVersion;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.module.GaugeModuleType;
import com.thoughtworks.gauge.module.lib.LibHelperFactory;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.SocketUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.thoughtworks.gauge.GaugeConstant.DAEMONIZE_FLAG;
import static com.thoughtworks.gauge.GaugeConstant.GAUGE_API_PORT;
import static com.thoughtworks.gauge.execution.GaugeRunConfiguration.GAUGE_CUSTOM_CLASSPATH;
import static com.thoughtworks.gauge.util.GaugeUtil.*;


public class GaugeModuleComponent implements ModuleComponent {
    public static final String GAUGE_SUPPORTED_VERSION = "0.8.0";
    private final Module module;
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.GaugeModuleComponent");

    public GaugeModuleComponent(Module module) {
        this.module = module;
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
        Gauge.disposeComponent(module);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GaugeModuleComponent";
    }

    @Override
    public void projectOpened() {
        if (GaugeUtil.isGaugeModule(module) && GaugeVersion.isLessThan(GAUGE_SUPPORTED_VERSION)) {
            String message = "This plugin version supports Gauge " + GAUGE_SUPPORTED_VERSION + " or above. Please refer <a href=\"https://docs.getgauge.io/installing.html\">this doc</a> to update Gauge.";
            Notification notification = new Notification("Gauge", "Version Incompatible", message, NotificationType.ERROR, NotificationListener.URL_OPENING_LISTENER);
            Notifications.Bus.notify(notification, module.getProject());
            return;
        }
        new LibHelperFactory().helperFor(module).checkDeps();
    }

    @Override
    public void projectClosed() {
        GaugeService gaugeService = Gauge.getGaugeService(module, true);
        if (gaugeService != null && gaugeService.getGaugeProcess() != null) {
            gaugeService.getGaugeProcess().destroy();
        }
    }

    @Override
    public void moduleAdded() {
        GaugeVersion.updateVersionInfo();
        if (GaugeVersion.isLessThan(GAUGE_SUPPORTED_VERSION)) return;
        projectOpened();
    }

    /**
     * Creates a gauge service for the particular module. GaugeService is used to make api calls to the gauge daemon process.
     *
     * @param module
     * @return
     */
    public static GaugeService createGaugeService(Module module) {
        int freePortForApi = SocketUtils.findFreePortForApi();
        Process gaugeProcess = initializeGaugeProcess(freePortForApi, module);
        GaugeConnection gaugeConnection = initializeGaugeConnection(freePortForApi);
        GaugeService gaugeService = new GaugeService(gaugeProcess, gaugeConnection);
        Gauge.addModule(module, gaugeService);
        return gaugeService;
    }

    private static GaugeConnection initializeGaugeConnection(int apiPort) {
        if (apiPort != -1) {
            return new GaugeConnection(apiPort);
        } else {
            return null;
        }
    }

    private static Process initializeGaugeProcess(int apiPort, Module module) {
        try {
            String path = getGaugeExecPath();
            ProcessBuilder gauge = new ProcessBuilder(path, DAEMONIZE_FLAG);
            gauge.environment().put(GAUGE_API_PORT, String.valueOf(apiPort));
            gauge.environment().put(GAUGE_CUSTOM_CLASSPATH, classpathForModule(module));
            gauge.directory(moduleDir(module));
            Process process = gauge.start();
            new GaugeExceptionHandler(process, module.getProject()).start();
            return process;
        } catch (IOException e) {
            LOG.error("Could not start gauge api:" + e.getMessage(), e);
            System.err.println("could not start gauge api:" + e.getMessage());
        } catch (GaugeNotFoundException e) {
            LOG.error("Could not start gauge api: " + e.getMessage(), e);
            System.err.println("Could not start gauge api:" + e.getMessage());
        }
        return null;
    }

    public static void makeGaugeModuleType(Module module) {
        module.setOption("type", GaugeModuleType.MODULE_TYPE_ID);
    }

    public static boolean isGaugeModule(Module module) {
        return GaugeModuleType.MODULE_TYPE_ID.equals(module.getOptionValue(Module.ELEMENT_TYPE)) || isGaugeProjectDir(moduleDir(module));
    }

    public static boolean isGaugeProject(Module module) {
        return isGaugeProjectDir(moduleDir(module));
    }
}
