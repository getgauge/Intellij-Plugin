package com.thoughtworks.gauge;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeConnection;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.util.SocketUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;


public class GaugeProjectComponent implements ProjectComponent {
    private final Project project;
    private Process gaugeProcess;

    public GaugeProjectComponent(Project project) {
        this.project = project;
    }

    public void initComponent() {

    }

    private static GaugeConnection initializeGaugeConnection(int apiPort) {
        if (apiPort != -1) {
            return new GaugeConnection(apiPort);
        } else {
            return null;
        }
    }

    private static Process initializeGaugeProcess(int apiPort, Project project) {
        ProcessBuilder gauge = new ProcessBuilder("gauge", "--daemonize");
        gauge.environment().put("GAUGE_API_PORT", String.valueOf(apiPort));
        gauge.directory(new File(project.getBasePath()));
        try {
            return gauge.start();
        } catch (IOException e) {
            System.out.println("could not start gauge api");
        }
        return null;
    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "GaugeProjectComponent";
    }

    @Override
    public void projectOpened() {
        if (Gauge.getGaugeService(project) == null) {
            GaugeService gaugeService = createGaugeService(project);
            gaugeProcess = gaugeService.getGaugeProcess();
            Gauge.addProject(project, gaugeService);
        }
    }

    @Override
    public void projectClosed() {
        if (gaugeProcess != null) {
            gaugeProcess.destroy();
        }
    }

    public static GaugeService createGaugeService(Project project) {
        int freePortForApi = SocketUtils.findFreePortForApi();
        return new GaugeService(initializeGaugeProcess(freePortForApi, project), initializeGaugeConnection(freePortForApi));
    }

}
