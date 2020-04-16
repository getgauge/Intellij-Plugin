/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.idea.icon.GaugeIcon;
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
        return Constants.SPEC_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GaugeIcon.GAUGE_SPEC_FILE_ICON;
    }
}
