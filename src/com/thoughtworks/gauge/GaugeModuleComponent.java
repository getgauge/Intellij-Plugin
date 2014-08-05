package com.thoughtworks.gauge;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.module.GaugeModuleType;
import com.thoughtworks.gauge.util.SocketUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;


public class GaugeModuleComponent implements ModuleComponent {
    private final Module module;
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.GaugeModuleComponent");

    public GaugeModuleComponent(Module module) {
        this.module = module;
    }

    public void initComponent() {

    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "GaugeModuleComponent";
    }

    public void projectOpened() {
        if (isGaugeModule(module)) {
            if (Gauge.getGaugeService(module) == null) {
                GaugeService gaugeService = createGaugeService(module);
                Gauge.addModule(module, gaugeService);
            }
        }
    }

    public void projectClosed() {
        // called when project is being closed
        GaugeService gaugeService = Gauge.getGaugeService(module);
        if (gaugeService != null && gaugeService.getGaugeProcess() != null) {
            gaugeService.getGaugeProcess().destroy();
        }
    }

    public void moduleAdded() {
        // Invoked when the module corresponding to this component instance has been completely
        // loaded and added to the project.
    }

    public static GaugeService createGaugeService(Module module) {
        int freePortForApi = SocketUtils.findFreePortForApi();
        Process gaugeProcess;
        gaugeProcess = initializeGaugeProcess(freePortForApi, module);
        GaugeConnection gaugeConnection = initializeGaugeConnection(freePortForApi);
        return new GaugeService(gaugeProcess, gaugeConnection);
    }

    private static GaugeConnection initializeGaugeConnection(int apiPort) {
        if (apiPort != -1) {
            return new GaugeConnection(apiPort);
        } else {
            return null;
        }
    }

    private static Process initializeGaugeProcess(int apiPort, Module module) {
        ProcessBuilder gauge = new ProcessBuilder("gauge", "--daemonize");
        gauge.environment().put("GAUGE_API_PORT", String.valueOf(apiPort));
        gauge.directory(new File(module.getModuleFilePath()).getParentFile());
        try {
            return gauge.start();
        } catch (IOException e) {
            LOG.error("could not start gauge api:" + e.getMessage(), e);
            System.out.println("could not start gauge api:" + e.getMessage());
        }
        return null;
    }

    private boolean isGaugeModule(Module module) {
        return GaugeModuleType.MODULE_TYPE_ID.equals(module.getOptionValue(Module.ELEMENT_TYPE));
    }

}
