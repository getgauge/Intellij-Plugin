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

import static com.thoughtworks.gauge.util.GaugeUtil.getGaugeSettings;

public class GaugeModuleBuilder extends JavaModuleBuilder {


    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        checkGaugeIsInstalled();
        super.setupRootModel(modifiableRootModel);
        gaugeInit(modifiableRootModel);
        new GaugeLibHelper(modifiableRootModel.getModule()).checkDeps();
    }

    private void checkGaugeIsInstalled() {
        try {
            getGaugeSettings();
        } catch (GaugeNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, this, this::isSuitableSdkType);
    }

    private void gaugeInit(final ModifiableRootModel modifiableRootModel) {
        ProgressManager.getInstance().run(new Task.Modal(modifiableRootModel.getProject(), "Initializing gauge-" + getLanguage() + " project", false) {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Installing gauge-" + getLanguage() + " plugin if not installed");
                String failureMessage = "Project initialization unsuccessful";
                try {
                    GaugeSettingsModel settings = getGaugeSettings();
                    final String[] init = {
                            settings.getGaugePath(),
                            Constants.INIT_COMMAND, getLanguage()
                    };
                    ProcessBuilder processBuilder = new ProcessBuilder(init);
                    processBuilder.directory(new File(getModuleFileDirectory()));
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
