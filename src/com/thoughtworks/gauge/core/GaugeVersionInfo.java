package com.thoughtworks.gauge.core;

class GaugeVersionInfo {
    public String version;

    GaugeVersionInfo(String v1) {
        this.version = v1;
    }

    GaugeVersionInfo() {
    }

    Boolean isGreaterThan(GaugeVersionInfo versionInfo) {
        return this.version.compareTo(versionInfo.version) > 0;
    }

    Boolean isLessThan(GaugeVersionInfo versionInfo) {
        return this.version.compareTo(versionInfo.version) < 0;
    }
}
