package com.thoughtworks.gauge.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SpecFileType extends LanguageFileType {
    public static final SpecFileType INSTANCE = new SpecFileType();

    private SpecFileType() {
        super(Specification.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Specification";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Gauge specification file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "spec";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GaugeIcon.GAUGE_SPEC_FILE_ICON;
    }
}
