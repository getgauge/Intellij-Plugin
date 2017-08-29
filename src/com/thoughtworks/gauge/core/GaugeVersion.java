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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

public class GaugeVersion {
    private static GaugeVersionInfo versionInfo = getVersion(true);

    public static GaugeVersionInfo getVersion(Boolean update) {
        if (!update) return versionInfo;
        GaugeVersionInfo gaugeVersionInfo = new GaugeVersionInfo();
        try {
            GaugeSettingsModel settings = getGaugeSettings();
            ProcessBuilder processBuilder = new ProcessBuilder(settings.getGaugePath(), Constants.VERSION, Constants.MACHINE_READABLE);
            GaugeUtil.setGaugeEnvironmentsTo(processBuilder, settings);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                String output = GaugeUtil.getOutput(process.getInputStream(), "\n");
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                gaugeVersionInfo = gson.fromJson(output, GaugeVersionInfo.class);
            }
        } catch (Exception ignored) {
        }
        versionInfo = gaugeVersionInfo;
        return gaugeVersionInfo;
    }
}

