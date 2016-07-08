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
import com.thoughtworks.gauge.util.GaugeUtil;

import static com.thoughtworks.gauge.GaugeModuleComponent.isGaugeModule;

public class LibHelperFactory {
    private static final LibHelper DEFAULT = () -> {
    };

    // Check if it is a maven module first, java deps will be added via maven so project libs dont need to be changed
    public LibHelper helperFor(Module module) {
        if (GaugeUtil.isMavenModule(module) || GaugeUtil.isGradleModule(module)) {
            return new GaugeModuleLibHelper(module);
        } else if (isGaugeModule(module)) {
            return new GaugeLibHelper(module);
        }
        return LibHelperFactory.DEFAULT;
    }

    private class GaugeModuleLibHelper extends AbstractLibHelper {

        public GaugeModuleLibHelper(Module module) {
            super(module);
        }

        public void checkDeps() {

        }
    }
}
