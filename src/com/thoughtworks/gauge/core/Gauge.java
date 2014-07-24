package com.thoughtworks.gauge.core;

import com.intellij.openapi.project.Project;

import java.util.Hashtable;

public class Gauge {
    private static Hashtable<Project, GaugeService> gaugeProjectHandle = new Hashtable<Project, GaugeService>();

    public static void addProject(Project project, GaugeService gaugeService) {
        gaugeProjectHandle.put(project, gaugeService);
    }

    public static GaugeService getGaugeService(Project project) {
        return gaugeProjectHandle.get(project);
    }
}
