package com.thoughtworks.gauge.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.thoughtworks.gauge.GaugeService;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeConnection;
import com.thoughtworks.gauge.util.SocketUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;


public class GaugeProjectComponent implements ProjectComponent {
    private final Project project;
    private final int apiPort;
    private Process gaugeProcess;
    private GaugeConnection gaugeConnection;

    public GaugeProjectComponent(Project project) {
        this.project = project;
        this.apiPort = SocketUtils.findFreePortForApi();
    }

    public void initComponent() {
        this.gaugeProcess = initializeGaugeProcess();
        this.gaugeConnection = initializeGaugeConnection(apiPort);
        Gauge.addProject(project, new GaugeService(gaugeProcess, gaugeConnection));
    }

    private GaugeConnection initializeGaugeConnection(int apiPort) {
        int freePortForApi = SocketUtils.findFreePortForApi();
        if (freePortForApi != -1) {
            return new GaugeConnection(apiPort);
        } else {
            return null;
        }
    }

    private Process initializeGaugeProcess() {
        ProcessBuilder gauge = new ProcessBuilder("gauge", "--daemonize");
        gauge.environment().put("GAUGE_API_PORT", String.valueOf(apiPort));
        gauge.directory(new File(project.getBasePath()));
        try {
            gaugeProcess = gauge.start();
        } catch (IOException e) {
            System.out.println("could not start gauge api");
        }
        return gaugeProcess;
    }

    public void disposeComponent() {
        if (gaugeProcess != null) {
            gaugeProcess.destroy();
        }
    }

    @NotNull
    public String getComponentName() {
        return "GaugeProjectComponent";
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }
}
