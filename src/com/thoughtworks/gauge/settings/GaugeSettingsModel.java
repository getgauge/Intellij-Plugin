package com.thoughtworks.gauge.settings;

import com.thoughtworks.gauge.Constants;

public class GaugeSettingsModel {
    public String gaugePath;
    public String homePath;
    public Boolean useIntelliJTestRunner;

    public GaugeSettingsModel(String gaugePath, String homePath, Boolean useIntelliJTestRunner) {
        this.gaugePath = gaugePath;
        this.homePath = homePath;
        this.useIntelliJTestRunner = useIntelliJTestRunner;
    }

    public GaugeSettingsModel() {
        this("", "", true);
    }

    public String getGaugePath() {
        return gaugePath;
    }

    public String getHomePath() {
        return homePath == null ? System.getenv(Constants.GAUGE_HOME) : homePath;
    }

    public Boolean useIntelliJTestRunner() {
        return useIntelliJTestRunner;
    }

    public boolean isGaugePathSet() {
        return gaugePath != null && !gaugePath.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GaugeSettingsModel that = (GaugeSettingsModel) o;
        return (gaugePath != null ? gaugePath.equals(that.gaugePath) : that.gaugePath == null)
                && (homePath != null ? homePath.equals(that.homePath) : that.homePath == null) &&
                useIntelliJTestRunner() == that.useIntelliJTestRunner();
    }

    @Override
    public String toString() {
        return "GaugeSettingsModel{" +
                "gaugePath='" + gaugePath + '\'' +
                ", homePath='" + homePath + '\'' +
                ", useIntelliJTestRunner=" + useIntelliJTestRunner +
                '}';
    }
}
