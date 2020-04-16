/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.execution;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunProfileWithCompileBeforeLaunchOption;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.jgoodies.common.base.Strings;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.core.GaugeVersion;
import com.thoughtworks.gauge.settings.GaugeSettingsService;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.gauge.execution.GaugeDebugInfo.isDebugExecution;

public class GaugeRunConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.execution.GaugeRunConfiguration");
    public static final String TEST_RUNNER_SUPPORT_VERSION = "0.9.2";
    private String specsToExecute;
    private Module module;
    private String environment;
    private String tags;
    private boolean execInParallel;
    private String parallelNodes;
    public ApplicationConfiguration programParameters;
    private String rowsRange;
    private String moduleName;

    public GaugeRunConfiguration(String name, Project project, ConfigurationFactoryEx configurationFactory) {
        super(project, configurationFactory, name);
        this.programParameters = new ApplicationConfiguration(name, project, ApplicationConfigurationType.getInstance());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GaugeExecutionConfigurationSettingsEditor();

    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment env) throws ExecutionException {
        GeneralCommandLine commandLine = GaugeCommandLine.getInstance(getModule(), getProject());
        addFlags(commandLine, env);
        return new GaugeCommandLineState(commandLine, getProject(), env, this);
    }

    private void addFlags(GeneralCommandLine commandLine, ExecutionEnvironment env) {
        commandLine.addParameter(Constants.RUN);
        if (GaugeVersion.isGreaterOrEqual(TEST_RUNNER_SUPPORT_VERSION, true)
                && GaugeSettingsService.getSettings().useIntelliJTestRunner()) {
            LOG.info("Using IntelliJ Test Runner");
            commandLine.addParameter(Constants.MACHINE_READABLE);
            commandLine.addParameter(Constants.HIDE_SUGGESTION);
        }
        commandLine.addParameter(Constants.SIMPLE_CONSOLE);
        if (!Strings.isBlank(tags)) {
            commandLine.addParameter(Constants.TAGS);
            commandLine.addParameter(tags);
        }
        if (!Strings.isBlank(environment)) {
            commandLine.addParameters(Constants.ENV_FLAG, environment);
        }
        addTableRowsRangeFlags(commandLine);
        addParallelExecFlags(commandLine, env);
        addProgramArguments(commandLine);
        if (!Strings.isBlank(specsToExecute)) {
            addSpecs(commandLine, specsToExecute);
        }
    }

    private void addTableRowsRangeFlags(GeneralCommandLine commandLine) {
        if (!Strings.isBlank(rowsRange)) {
            commandLine.addParameter(Constants.TABLE_ROWS);
            commandLine.addParameter(rowsRange);
        }
    }

    private void addProgramArguments(GeneralCommandLine commandLine) {
        if (programParameters == null) {
            return;
        }
        String parameters = programParameters.getProgramParameters();
        if (!Strings.isEmpty(parameters)) {
            commandLine.addParameters(programParameters.getProgramParameters().split(" "));
        }
        Map<String, String> envs = programParameters.getEnvs();
        if (!envs.isEmpty()) {
            commandLine.withEnvironment(envs);
        }
        if (Strings.isNotEmpty(programParameters.getWorkingDirectory())) {
            commandLine.setWorkDirectory(new File(programParameters.getWorkingDirectory()));
        }
    }

    private void addParallelExecFlags(GeneralCommandLine commandLine, ExecutionEnvironment env) {
        if (parallelExec(env)) {
            commandLine.addParameter(Constants.PARALLEL);
            try {
                if (!Strings.isEmpty(parallelNodes)) {
                    Integer.parseInt(this.parallelNodes);
                    commandLine.addParameters(Constants.PARALLEL_NODES, parallelNodes);
                }
            } catch (NumberFormatException e) {
                System.err.println("Incorrect number of parallel execution streams specified: " + parallelNodes);
                LOG.debug("Incorrect number of parallel execution streams specified: " + parallelNodes);
                e.printStackTrace();
            }
        }
    }

    private boolean parallelExec(ExecutionEnvironment env) {
        return execInParallel && !isDebugExecution(env);
    }

    private void addSpecs(GeneralCommandLine commandLine, String specsToExecute) {
        String[] specNames = specsToExecute.split(Constants.SPEC_FILE_DELIMITER_REGEX);
        for (String specName : specNames) {
            if (!specName.isEmpty()) {
                commandLine.addParameter(specName.trim());
            }
        }
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        environment = JDOMExternalizer.readString(element, "environment");
        specsToExecute = JDOMExternalizer.readString(element, "specsToExecute");
        tags = JDOMExternalizer.readString(element, "tags");
        parallelNodes = JDOMExternalizer.readString(element, "parallelNodes");
        execInParallel = JDOMExternalizer.readBoolean(element, "execInParallel");
        programParameters.setProgramParameters(JDOMExternalizer.readString(element, "programParameters"));
        programParameters.setWorkingDirectory(JDOMExternalizer.readString(element, "workingDirectory"));
        this.moduleName = JDOMExternalizer.readString(element, "moduleName");
        HashMap<String, String> envMap = new HashMap<>();
        JDOMExternalizer.readMap(element, envMap, "envMap", "envMap");
        programParameters.setEnvs(envMap);
        rowsRange = JDOMExternalizer.readString(element, "rowsRange");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizer.write(element, "environment", environment);
        JDOMExternalizer.write(element, "specsToExecute", specsToExecute);
        JDOMExternalizer.write(element, "tags", tags);
        JDOMExternalizer.write(element, "parallelNodes", parallelNodes);
        JDOMExternalizer.write(element, "execInParallel", execInParallel);
        JDOMExternalizer.write(element, "programParameters", programParameters.getProgramParameters());
        JDOMExternalizer.write(element, "workingDirectory", programParameters.getWorkingDirectory());
        JDOMExternalizer.write(element, "moduleName", moduleName);
        JDOMExternalizer.writeMap(element, programParameters.getEnvs(), "envMap", "envMap");
        JDOMExternalizer.write(element, "rowsRange", rowsRange);
    }

    @NotNull
    @Override
    public Module[] getModules() {
        return ModuleManager.getInstance(getProject()).getModules();
    }

    public void setSpecsToExecute(String specsToExecute) {
        this.specsToExecute = specsToExecute;
    }

    public String getSpecsToExecute() {
        return specsToExecute;
    }

    public void setModule(Module module) {
        this.module = module;
        this.moduleName = module.getName();
    }

    public Module getModule() {
        if (module == null)
            return ModuleManager.getInstance(getProject()).findModuleByName(this.moduleName);
        return module;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setSpecsArrayToExecute(List<String> specsArrayToExecute) {
        StringBuilder builder = new StringBuilder("");
        for (String specName : specsArrayToExecute) {
            builder.append(specName);
            if (specsArrayToExecute.indexOf(specName) != specsArrayToExecute.size() - 1) {
                builder.append(Constants.SPEC_FILE_DELIMITER);
            }
        }
        setSpecsToExecute(builder.toString());
    }

    public void setExecInParallel(boolean execInParallel) {
        this.execInParallel = execInParallel;
    }

    public boolean getExecInParallel() {
        return execInParallel;
    }

    public void setParallelNodes(String parallelNodes) {
        this.parallelNodes = parallelNodes;
    }

    public String getParallelNodes() {
        return parallelNodes;
    }

    public CommonProgramRunConfigurationParameters getProgramParameters() {
        return programParameters;
    }

    public String getRowsRange() {
        return rowsRange;
    }

    public void setRowsRange(String rowsRange) {
        this.rowsRange = rowsRange;
    }
}
