// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.language;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.thoughtworks.gauge.Constants;
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
        return Constants.CONCEPT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GaugeIcon.GAUGE_CONCEPT_FILE_ICON;
    }
}
