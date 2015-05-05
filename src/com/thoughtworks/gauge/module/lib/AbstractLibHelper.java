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

package com.thoughtworks.gauge.module.lib;

import com.intellij.openapi.module.Module;
import com.thoughtworks.gauge.GaugeModuleComponent;
import com.thoughtworks.gauge.core.Gauge;

import static com.thoughtworks.gauge.GaugeModuleComponent.isGaugeModule;

public abstract class AbstractLibHelper implements LibHelper {

    private Module module;

    public AbstractLibHelper(Module module) {
        this.module = module;
        if (isGaugeModule(module)) {
            GaugeModuleComponent.makeGaugeModuleType(module);
            if (Gauge.getGaugeService(module) == null) {
                GaugeModuleComponent.createGaugeService(module);
            }
        }
    }

    public Module getModule() {
        return module;
    }
}
