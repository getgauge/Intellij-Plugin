package com.thoughtworks.gauge.execution;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GaugeExecutionConfigurationSettingsEditor extends SettingsEditor<GaugeRunConfiguration> {
    private JTextField specification;
    private JTextField environment;
    private JPanel configWindow;
    private JTextField tags;

    @Override
    protected void resetEditorFrom(GaugeRunConfiguration config) {
        specification.setText(config.getSpecsToExecute());
        environment.setText(config.getEnvironment());
        tags.setText(config.getTags());
    }

    @Override
    protected void applyEditorTo(GaugeRunConfiguration config) throws ConfigurationException {
        config.setSpecsToExecute(specification.getText());
        config.setEnvironment(environment.getText());
        config.setTags(tags.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return configWindow;
    }
}
