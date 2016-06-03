package com.thoughtworks.gauge.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.gauge.util.GaugeUtil;

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeExecPath;

public class GaugeVersion {
    private static GaugeVersionInfo versionInfo = getVersion();

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
                String output = GaugeUtil.getOutput(process);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                gaugeVersionInfo = gson.fromJson(output, GaugeVersionInfo.class);
            }
        } catch (Exception ignored) {
        }
        return gaugeVersionInfo;
    }

    public static Boolean isGreaterThan(String v1) {
        return versionInfo.isGreaterThan(new GaugeVersionInfo(v1));
    }
}

