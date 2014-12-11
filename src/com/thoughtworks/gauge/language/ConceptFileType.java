package com.thoughtworks.gauge.language;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConceptFileType extends LanguageFileType{
    public static final FileType INSTANCE = new ConceptFileType();

    public ConceptFileType() {
        super(Concept.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Concept";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Gauge Concept";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return ".cpt";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GaugeIcon.GAUGE_CONCEPT_FILE_ICON;
    }
}
