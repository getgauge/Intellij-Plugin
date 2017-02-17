package com.thoughtworks.gauge.core;

class GaugeVersionInfo {
    public String version;

    GaugeVersionInfo(String v1) {
        this.version = v1;
    }

    GaugeVersionInfo() {
    }

    Boolean isLessThan(GaugeVersionInfo versionInfo) {
        return isVersionAvailable() && this.version.compareTo(versionInfo.version) < 0;
    }

    private Boolean isVersionAvailable() {
        return this.version != null;
    }
}
