package com.thoughtworks.gauge.core;

import java.util.List;

public class GaugeVersionInfo {
    public String version;
    public List<Plugin> plugins;

    public GaugeVersionInfo(String v1, List<Plugin> plugins) {
        this.version = v1;
        this.plugins = plugins;
    }

    public GaugeVersionInfo() {
    }

    public Boolean isPluginInstalled(String plugin) {
        return plugins.stream().anyMatch(p -> plugin.equalsIgnoreCase(p.name));
    }
}
