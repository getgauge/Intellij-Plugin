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
import com.thoughtworks.gauge.util.GaugeUtil;

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeExecPath;

public class GaugeVersion {
    static GaugeVersionInfo versionInfo = getVersion();

    public static void updateVersionInfo() {
        versionInfo = getVersion();
    }

    private static GaugeVersionInfo getVersion() {
        GaugeVersionInfo gaugeVersionInfo = new GaugeVersionInfo();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(getGaugeExecPath(), "--version", "--machine-readable");
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
        return gaugeVersionInfo;
    }

    public static Boolean isLessThan(String v1) {
        return versionInfo.isLessThan(new GaugeVersionInfo(v1));
    }
}

