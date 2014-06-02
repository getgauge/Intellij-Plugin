package com.thoughtworks.gauge.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.thoughtworks.gauge.util.SocketUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;


public class GaugeProjectComponent implements ProjectComponent {
    private final Project project;
    private final int apiPort;
    private Process process;

    public GaugeProjectComponent(Project project) {
        this.project = project;
        this.apiPort = SocketUtils.findFreePortForApi();
    }

    public void initComponent() {

    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "GaugeProjectComponent";
    }

    public void projectOpened() {
        ProcessBuilder gauge = new ProcessBuilder("gauge", "--daemonize");
        gauge.environment().put("GAUGE_API_PORT", String.valueOf(apiPort));
        gauge.directory(new File(project.getBasePath()));
        try {
            process = gauge.start();
        } catch (IOException e) {
            System.out.println("could not start gauge api");
        }
    }

    public void projectClosed() {
        process.destroy();
    }
}
