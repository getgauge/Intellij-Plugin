/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

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
