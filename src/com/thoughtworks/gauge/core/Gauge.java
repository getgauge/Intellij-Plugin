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

package com.thoughtworks.gauge.core;

import com.intellij.openapi.module.Module;
import com.thoughtworks.gauge.reference.ReferenceCache;

import java.util.Hashtable;
import java.util.Iterator;

public class Gauge {
    private static Hashtable<Module, GaugeService> gaugeProjectHandle = new Hashtable<Module, GaugeService>();
    private static Hashtable<Module, ReferenceCache> moduleReferenceCaches = new Hashtable<Module, ReferenceCache>();

    public static void addModule(Module module, GaugeService gaugeService) {
        gaugeProjectHandle.put(module, gaugeService);
    }

    public static GaugeService getGaugeService(Module module, boolean moduleDependent) {
        if (module == null) {
            return null;
        }
        GaugeService service = gaugeProjectHandle.get(module);
        return (service == null && !moduleDependent) ? getGaugeService() : service;
    }

    public static ReferenceCache getReferenceCache(Module module) {
        ReferenceCache referenceCache = moduleReferenceCaches.get(module);
        if (referenceCache == null) {
            referenceCache = new ReferenceCache();
            moduleReferenceCaches.put(module, referenceCache);
        }
        return referenceCache;
    }

    private static GaugeService getGaugeService() {
        Iterator<GaugeService> iterator = gaugeProjectHandle.values().iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }
}
