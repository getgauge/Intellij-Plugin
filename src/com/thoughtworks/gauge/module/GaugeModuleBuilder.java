/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.module;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.core.GaugeVersion;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.module.lib.GaugeLibHelper;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.gauge.Constants.MIN_GAUGE_VERSION;
import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

public class GaugeModuleBuilder extends JavaModuleBuilder {

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        checkGaugeIsInstalled();
        super.setupRootModel(modifiableRootModel);
        gaugeInit(modifiableRootModel);
        new GaugeLibHelper(modifiableRootModel.getModule()).checkDeps();
    }

    private void checkGaugeIsInstalled() throws ConfigurationException {
        try {
            getGaugeSettings();
            if (!GaugeVersion.isGreaterOrEqual(MIN_GAUGE_VERSION, false)) {
                throw new ConfigurationException(String.format("This version of Gauge Intellij plugin only works with Gauge version >= %s", MIN_GAUGE_VERSION), "Unsupported Gauge Version");
            }
        } catch (GaugeNotFoundException e) {
            throw new ConfigurationException(e.getMessage(), "Gauge Not Found");
        }
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, this, this::isSuitableSdkType);
    }

    private void gaugeInit(final ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        File directory = new File(getModuleFileDirectory());
        if(GaugeUtil.isGaugeProjectDir(directory)){
            throw  new ConfigurationException("Given location is already a Gauge Project. Please try to initialize a Gauge project in a different location.");
        }
        ProgressManager.getInstance().run(new Task.Modal(modifiableRootModel.getProject(), "Initializing gauge-" + getLanguage() + " project", true) {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Installing gauge-" + getLanguage() + " plugin if not installed");
                String failureMessage = "Project initialization unsuccessful";
                try {
                    GaugeSettingsModel settings = getGaugeSettings();
                    final String[] init = {
                            settings.getGaugePath(),
                            Constants.INIT_FLAG, getLanguage()
                    };
                    ProcessBuilder processBuilder = new ProcessBuilder(init);
                    processBuilder.directory(directory);
                    GaugeUtil.setGaugeEnvironmentsTo(processBuilder, settings);
                    Process process = processBuilder.start();
                    final int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        throw new RuntimeException(failureMessage);
                    }
                    VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(failureMessage, e);
                } catch (GaugeNotFoundException e) {
                    throw new RuntimeException(String.format("%s: %s", failureMessage, e.getMessage()), e);
                }
            }
        });
    }

    private String getLanguage() {
        return "java";
    }

    @Override
    public ModuleType getModuleType() {
        return GaugeModuleType.getInstance();
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        final List<Pair<String, String>> paths = new ArrayList<>();
        @NonNls final String path = getContentEntryPath() + File.separator + "src" + File.separator + "test" + File.separator + "java";
        paths.add(Pair.create(path, ""));
        return paths;
    }
}
