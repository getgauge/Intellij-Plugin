/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.module.lib;

import com.intellij.openapi.module.Module;
import com.thoughtworks.gauge.GaugeModuleComponent;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.util.GaugeUtil;

import static com.thoughtworks.gauge.GaugeModuleComponent.isGaugeProject;

public abstract class AbstractLibHelper implements LibHelper {

    private Module module;

    public AbstractLibHelper(Module module) {
        this.module = module;
        if (isGaugeProject(module)) {
            if (!GaugeUtil.isMavenModule(module) && !GaugeUtil.isGradleModule(module))
                GaugeModuleComponent.makeGaugeModuleType(module);
            if (Gauge.getGaugeService(module, true) == null)
                GaugeModuleComponent.createGaugeService(module);
        }
    }

    public Module getModule() {
        return module;
    }
}
