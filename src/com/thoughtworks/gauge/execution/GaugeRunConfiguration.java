package com.thoughtworks.gauge.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.jgoodies.common.base.Strings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.thoughtworks.gauge.GaugeConstant.*;

public class GaugeRunConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {

    public static final String JAVA_DEBUG_PORT = "50005";
    private String specsToExecute;
    private Module module;
    private String environment;

    public GaugeRunConfiguration(String name, Project project, ConfigurationFactoryEx configurationFactory) {
        super(project, configurationFactory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new GaugeExecutionConfigurationSettingsEditor();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment env) throws ExecutionException {
        return new CommandLineState(env) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                GeneralCommandLine commandLine = new GeneralCommandLine();
                commandLine.setExePath(GAUGE);
                commandLine.setWorkDirectory(getProject().getBaseDir().getPath());
                if (!Strings.isBlank(environment)) {
                    commandLine.addParameters(ENV_FLAG, environment);
                }
                commandLine.addParameter(specsToExecute);
                if (DefaultDebugExecutor.EXECUTOR_ID.equals(env.getExecutor().getId())) {
                    commandLine.getEnvironment().put(GAUGE_DEBUG_OPTS_ENV, JAVA_DEBUG_PORT);
                }
                return GaugeRunProcessHandler.runCommandLine(commandLine);
            }
        };
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        environment = JDOMExternalizer.readString(element, "environment");
        specsToExecute = JDOMExternalizer.readString(element, "specsToExecute");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizer.write(element, "environment", environment);
        JDOMExternalizer.write(element, "specsToExecute", specsToExecute);
    }

    @NotNull
    @Override
    public Module[] getModules() {
        return new Module[]{getModule()};
    }

    public void setSpecsToExecute(String specsToExecute) {
        this.specsToExecute = specsToExecute;
    }

    public String getSpecsToExecute() {
        return specsToExecute;
    }


    public void setModule(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
