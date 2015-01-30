package com.thoughtworks.gauge.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.jgoodies.common.base.Strings;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.thoughtworks.gauge.GaugeConstant.ENV_FLAG;
import static com.thoughtworks.gauge.GaugeConstant.GAUGE_DEBUG_OPTS_ENV;

public class GaugeRunConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {

    public static final String JAVA_DEBUG_PORT = "50005";
    public static final String SIMPLE_CONSOLE_FLAG = "--simple-console";
    public static final String TAGS_FLAG = "--tags";
    private String specsToExecute;
    private Module module;
    private String environment;
    private String tags;

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
                commandLine.setExePath(GaugeUtil.getGaugeExecPath());
                commandLine.addParameter(SIMPLE_CONSOLE_FLAG);
                if (!Strings.isBlank(tags)) {
                    commandLine.addParameter(TAGS_FLAG);
                    commandLine.addParameter(tags);
                }
                commandLine.setWorkDirectory(getProject().getBaseDir().getPath());
                if (!Strings.isBlank(environment)) {
                    commandLine.addParameters(ENV_FLAG, environment);
                }
                if (!Strings.isBlank(specsToExecute)) {
                    addSpecs(commandLine, specsToExecute);
                }
                if (DefaultDebugExecutor.EXECUTOR_ID.equals(env.getExecutor().getId())) {
                    commandLine.getEnvironment().put(GAUGE_DEBUG_OPTS_ENV, JAVA_DEBUG_PORT);
                }
                return GaugeRunProcessHandler.runCommandLine(commandLine);
            }
        };
    }

    private void addSpecs(GeneralCommandLine commandLine, String specsToExecute) {
        String[] specNames = specsToExecute.split(",");
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
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizer.write(element, "environment", environment);
        JDOMExternalizer.write(element, "specsToExecute", specsToExecute);
        JDOMExternalizer.write(element, "tags", tags);
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setSpecsArrayToExecute(ArrayList<String> specsArrayToExecute) {
        StringBuilder builder = new StringBuilder("");
        for (String specName : specsArrayToExecute) {
            builder.append(specName);
            if (specsArrayToExecute.indexOf(specName) != specsArrayToExecute.size() - 1) {
                builder.append(",");
            }
        }
        setSpecsToExecute(builder.toString());
    }
}
