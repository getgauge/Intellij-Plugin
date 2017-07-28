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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.thoughtworks.gauge.connection.GaugeConnection;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeExceptionHandler;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.module.GaugeModuleType;
import com.thoughtworks.gauge.module.lib.LibHelperFactory;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.SocketUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.thoughtworks.gauge.util.GaugeUtil.*;


public class GaugeModuleComponent implements ModuleComponent {
    private final Module module;
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.GaugeModuleComponent");

    public GaugeModuleComponent(Module module) {
        this.module = module;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GaugeModuleComponent";
    }

    public void moduleAdded() {
        new LibHelperFactory().helperFor(module).checkDeps();
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
        return apiPort != -1 ? new GaugeConnection(apiPort) : null;
    }

    private static Process initializeGaugeProcess(int apiPort, Module module) {
        try {
            GaugeSettingsModel settings = getGaugeSettings();
            String port = String.valueOf(apiPort);
            ProcessBuilder gauge = new ProcessBuilder(settings.getGaugePath(), Constants.DAEMONIZE, Constants.API_PORT, port);
            GaugeUtil.setGaugeEnvironmentsTo(gauge, settings);
            String cp = classpathForModule(module);
            LOG.info(String.format("Setting `%s` to `%s`", Constants.GAUGE_CUSTOM_CLASSPATH, cp));
            gauge.environment().put(Constants.GAUGE_CUSTOM_CLASSPATH, cp);
            File dir = moduleDir(module);
            LOG.info(String.format("Using `%s` as api port to connect to gauge API for project %s", port, dir));
            gauge.directory(dir);
            Process process = gauge.start();
            new GaugeExceptionHandler(process, module.getProject()).start();
            return process;
        } catch (IOException | GaugeNotFoundException e) {
            LOG.error("An error occurred while starting gauge api. \n" + e);
        }
        return null;
    }

    public static void makeGaugeModuleType(Module module) {
        module.setModuleType(GaugeModuleType.MODULE_TYPE_ID);
    }

    public static boolean isGaugeModule(Module module) {
        return GaugeModuleType.MODULE_TYPE_ID.equals(module.getModuleTypeName()) || isGaugeProjectDir(moduleDir(module));
    }

    public static boolean isGaugeProject(Module module) {
        return isGaugeProjectDir(moduleDir(module));
    }
}
