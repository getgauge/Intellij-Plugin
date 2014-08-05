package com.thoughtworks.gauge.core;

import com.intellij.openapi.module.Module;

import java.util.Hashtable;

public class Gauge {
    private static Hashtable<Module, GaugeService> gaugeProjectHandle = new Hashtable<Module, GaugeService>();

    public static void addModule(Module module, GaugeService gaugeService) {
        gaugeProjectHandle.put(module, gaugeService);
    }

    public static GaugeService getGaugeService(Module module) {
        return gaugeProjectHandle.get(module);
    }
}
