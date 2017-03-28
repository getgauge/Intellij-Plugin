package com.thoughtworks.gauge.settings;

import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GaugeConfig extends SettingsEditor<ApplicationConfiguration> {
    private JPanel configWindow;
    private JButton gaugeChooser;
    private JButton homeChooser;
    private JButton rootChooser;
    private JTextField gaugePath;
    private JTextField homePath;
    private JTextField rootPath;

    public GaugeConfig() {
        gaugeChooser.addActionListener(e -> setPath(gaugePath, true));
        homeChooser.addActionListener(e -> setPath(homePath, false));
        rootChooser.addActionListener(e -> setPath(rootPath, false));
    }

    @Override
    protected void resetEditorFrom(@NotNull ApplicationConfiguration applicationConfiguration) {

    }

    @Override
    protected void applyEditorTo(@NotNull ApplicationConfiguration applicationConfiguration) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return configWindow;
    }

    private void setPath(JTextField field, boolean chooseFiles) {
        VirtualFile file = FileChooser.chooseFile(new FileChooserDescriptor(chooseFiles, !chooseFiles, false, false, false, false), null, null);
        if (file == null) return;
        field.setText(file.getPath());
    }

    public void setValues(GaugeSettingsModel model) {
        this.homePath.setText(model.homePath);
        this.rootPath.setText(model.rootPath);
        this.gaugePath.setText(model.gaugePath);
    }

    public GaugeSettingsModel getValues() {
        return new GaugeSettingsModel(this.gaugePath.getText(), this.homePath.getText(), this.rootPath.getText());
    }
}
