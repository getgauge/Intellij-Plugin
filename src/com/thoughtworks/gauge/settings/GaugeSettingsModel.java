package com.thoughtworks.gauge.settings;

import com.thoughtworks.gauge.Constants;

public class GaugeSettingsModel {
    public String gaugePath;
    public String homePath;
    public String rootPath;

    public GaugeSettingsModel(String gaugePath, String homePath, String rootPath) {
        this.gaugePath = gaugePath;
        this.homePath = homePath;
        this.rootPath = rootPath;
    }

    public GaugeSettingsModel() {
        this("", "", "");
    }

    public String getGaugePath() {
        return gaugePath;
    }

    public String getHomePath() {
        return homePath == null ? System.getenv(Constants.GAUGE_HOME) : homePath;
    }

    public String getRootPath() {
        return rootPath == null ? System.getenv(Constants.GAUGE_ROOT) : rootPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GaugeSettingsModel that = (GaugeSettingsModel) o;
        if (gaugePath != null ? !gaugePath.equals(that.gaugePath) : that.gaugePath != null) return false;
        if (homePath != null ? !homePath.equals(that.homePath) : that.homePath != null) return false;
        return rootPath != null ? rootPath.equals(that.rootPath) : that.rootPath == null;
    }

    public boolean isGaugePathSet() {
        return gaugePath != null && !gaugePath.isEmpty();
    }

    @Override
    public String toString() {
        return "GaugeSettingsModel{" +
                "gaugePath='" + gaugePath + '\'' +
                ", homePath='" + homePath + '\'' +
                ", rootPath='" + rootPath + '\'' +
                '}';
    }
}
