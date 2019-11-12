package com.thoughtworks.gauge.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextComponentAccessor;

import javax.swing.*;

public class GaugeConfig {
    private JPanel configWindow;
    private com.intellij.openapi.ui.TextFieldWithBrowseButton gaugePath;
    private com.intellij.openapi.ui.TextFieldWithBrowseButton homePath;
    private JCheckBox useIntelliJTestRunner;

    JComponent createEditor() {
        FileChooserDescriptor homeFileDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        homePath.addBrowseFolderListener(
                "",
                "Gauge Home Path",
                null,
                homeFileDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        FileChooserDescriptor gaugePathFileDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        gaugePath.addBrowseFolderListener(
                "",
                "Gauge Binary Path",
                null,
                gaugePathFileDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        return configWindow;
    }

    void setValues(GaugeSettingsModel model) {
        this.homePath.setText(model.homePath);
        this.gaugePath.setText(model.gaugePath);
        this.useIntelliJTestRunner.setSelected(model.useIntelliJTestRunner);
    }

    GaugeSettingsModel getValues() {
        return new GaugeSettingsModel(this.gaugePath.getText(), this.homePath.getText(), this.useIntelliJTestRunner.isSelected());
    }

}
