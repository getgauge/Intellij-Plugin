/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.idea.icon.GaugeIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConceptFileType extends LanguageFileType {
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
        return Constants.CONCEPT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GaugeIcon.GAUGE_CONCEPT_FILE_ICON;
    }
}
