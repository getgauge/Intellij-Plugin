package com.thoughtworks.gauge.execution;

import com.intellij.execution.ui.CommonProgramParametersPanel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GaugeExecutionConfigurationSettingsEditor extends SettingsEditor<GaugeRunConfiguration> {
    private JTextField specification;
    private JTextField environment;
    private JPanel configWindow;
    private JTextField tags;
    private JRadioButton inParallel;
    private JTextField numberOfParallelNodes;
    private CommonProgramParametersPanel commonProgramParameters;
    private JTextField rowsRange;

    @Override
    protected void resetEditorFrom(GaugeRunConfiguration config) {
        specification.setText(config.getSpecsToExecute());
        environment.setText(config.getEnvironment());
        tags.setText(config.getTags());
        inParallel.setSelected(config.getExecInParallel());
        numberOfParallelNodes.setText(config.getParallelNodes());
        commonProgramParameters.reset(config.programParameters);
        rowsRange.setText(config.getRowsRange());
    }

    @Override
    protected void applyEditorTo(GaugeRunConfiguration config) throws ConfigurationException {
        config.setSpecsToExecute(specification.getText());
        config.setEnvironment(environment.getText());
        config.setTags(tags.getText());
        config.setExecInParallel(inParallel.isSelected());
        config.setParallelNodes(numberOfParallelNodes.getText());
        commonProgramParameters.applyTo(config.programParameters);
        config.setRowsRange(rowsRange.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return configWindow;
    }
}
