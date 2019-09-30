package com.thoughtworks.gauge.inspection;

import com.intellij.openapi.diagnostic.Logger;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

class GaugeInspectionHelper {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.inspection.GaugeInspectionHelper");
    @NotNull
    static List<GaugeError> getErrors(File directory) {
        try {
            GaugeSettingsModel settings = getGaugeSettings();
            ProcessBuilder processBuilder = new ProcessBuilder(settings.getGaugePath(), Constants.VALIDATE);
            GaugeUtil.setGaugeEnvironmentsTo(processBuilder, settings);
            processBuilder.directory(directory);
            Process process = processBuilder.start();
            process.waitFor();
            String[] errors = GaugeUtil.getOutput(process.getInputStream(), "\n").split("\n");
            return Arrays.stream(errors).map(GaugeError::getInstance).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException | InterruptedException | GaugeNotFoundException e) {
            LOG.debug(e);
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
