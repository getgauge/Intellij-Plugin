package com.thoughtworks.gauge.settings;

import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GaugeSettings implements com.intellij.openapi.options.Configurable {

    private GaugeConfig gaugeConfig = new GaugeConfig();
    private GaugeSettingsModel model;

    @Nls
    @Override
    public String getDisplayName() {
        return "Gauge";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        model = GaugeSettingsService.getSettings();
        gaugeConfig.setValues(model);
        return gaugeConfig.getComponent();
    }

    @Override
    public boolean isModified() {
        return !model.equals(gaugeConfig.getValues());
    }

    @Override
    public void apply() throws ConfigurationException {
        model = gaugeConfig.getValues();
        GaugeSettingsService.getService().loadState(model);
    }

    @Override
    public void reset() {
        gaugeConfig.setValues(model);
    }
}
