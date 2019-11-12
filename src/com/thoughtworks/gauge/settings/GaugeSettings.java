package com.thoughtworks.gauge.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GaugeSettings implements SearchableConfigurable, Disposable {

    private GaugeConfig gaugeConfig;
    private GaugeSettingsModel model;


    @NotNull
    @Override
    public String getId() {
        return "gauge";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Gauge";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        model = GaugeSettingsService.getSettings();
        gaugeConfig = new GaugeConfig();
        gaugeConfig.setValues(model);
        return gaugeConfig.createEditor();
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

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
    }

    @Override
    public void dispose() {
        gaugeConfig = null;
    }
}
