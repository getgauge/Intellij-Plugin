package com.thoughtworks.gauge.core;

import java.util.ArrayList;
import java.util.List;

public class GaugeVersionInfo {
    public String version;
    public List<Plugin> plugins;

    public GaugeVersionInfo(String v, List<Plugin> plugins) {
        this.version = v;
        this.plugins = plugins;
    }

    public GaugeVersionInfo(String v) {
        this.version = v;
        this.plugins = new ArrayList<>();
    }

    public GaugeVersionInfo() {
    }

    public Boolean isPluginInstalled(String plugin) {
        return plugins.stream().anyMatch(p -> plugin.equalsIgnoreCase(p.name));
    }

    Boolean isGreaterOrEqual(GaugeVersionInfo versionInfo) {
        return this.version != null && this.version.compareTo(versionInfo.version) >= 0;
    }
}
