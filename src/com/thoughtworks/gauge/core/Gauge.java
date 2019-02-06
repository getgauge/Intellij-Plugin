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
import com.intellij.openapi.module.ModuleManager;
import com.thoughtworks.gauge.reference.ReferenceCache;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Gauge {
    private static Hashtable<Module, GaugeService> gaugeProjectHandle = new Hashtable<>();
    private static HashMap<String, HashSet<Module>> linkedModulesMap = new HashMap<>();
    private static Hashtable<Module, ReferenceCache> moduleReferenceCaches = new Hashtable<>();

    public static void addModule(Module module, GaugeService gaugeService) {
        HashSet<Module> modules = getSubModules(module);
        if (modules.isEmpty()) modules.add(module);
        modules.forEach(m -> gaugeProjectHandle.put(m, gaugeService));
    }

    public static GaugeService getGaugeService(Module module, boolean moduleDependent) {
        if (module == null) {
            return null;
        }
        GaugeService service = gaugeProjectHandle.get(module);
        if (service != null) return service;
        Set<Module> modules = getSubModules(module);
        for (Module m : modules) {
            service = gaugeProjectHandle.get(m);
            if (service != null) {
                addModule(module, service);
                return service;
            }
        }
        return moduleDependent ? null : getGaugeService();
    }

    public static ReferenceCache getReferenceCache(Module module) {
        ReferenceCache referenceCache = moduleReferenceCaches.get(module);
        if (referenceCache == null) {
            referenceCache = new ReferenceCache();
            moduleReferenceCaches.put(module, referenceCache);
        }
        return referenceCache;
    }

    public static HashSet<Module> getSubModules(Module module) {
        String value = getProjectGroupValue(module);
        HashSet<Module> modules = linkedModulesMap.get(value);
        if (modules != null) return modules;
        modules = new HashSet<>();
        for (Module m : ModuleManager.getInstance(module.getProject()).getModules())
            if (getProjectGroupValue(m).contains(value)) {
                modules.add(m);
                addToModulesMap(m, value);
            }
        return modules;
    }

    private static void addToModulesMap(Module module, String name) {
        if (!linkedModulesMap.containsKey(name))
            linkedModulesMap.put(name, new HashSet<>());
        linkedModulesMap.get(name).add(module);
    }

    @NotNull
    private static String getProjectGroupValue(Module module) {
        String[] values = ModuleManager.getInstance(module.getProject()).getModuleGroupPath(module);
        return values == null || values.length < 1 ? module.getName() : values[0];
    }

    private static GaugeService getGaugeService() {
        Iterator<GaugeService> iterator = gaugeProjectHandle.values().iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    public static void disposeComponent(Module module) {
        if (module == null) return;
        String value = getProjectGroupValue(module);
        linkedModulesMap.remove(value);
        GaugeService service = gaugeProjectHandle.get(module);
        if (service != null && service.getGaugeProcess().isAlive()) service.getGaugeProcess().destroy();
    }
}
