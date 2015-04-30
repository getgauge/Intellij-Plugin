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

package com.thoughtworks.gauge.module;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.thoughtworks.gauge.idea.icon.GaugeIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GaugeModuleType extends ModuleType<GaugeModuleBuilder> {
    public static final String MODULE_TYPE_ID = "Gauge_Module";
    private static final String GAUGE_MODULE = "Gauge Module";

    public GaugeModuleType() {
        super(MODULE_TYPE_ID);
    }

    public static GaugeModuleType getInstance() {
        return (GaugeModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
    }

    @NotNull
    @Override
    public GaugeModuleBuilder createModuleBuilder() {
        return new GaugeModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return GAUGE_MODULE;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "module supported for writing gauge tests";
    }

    @Override
    public Icon getBigIcon() {
        return GaugeIcon.GAUGE_LOGO;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return GaugeIcon.GAUGE_LOGO;
    }
}
