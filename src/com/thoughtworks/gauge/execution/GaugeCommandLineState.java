package com.thoughtworks.gauge.execution;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.ui.ConsoleView;
import org.jetbrains.annotations.NotNull;

public class GaugeCommandLineState extends CommandLineState {
    private GaugeRunProcessHandler handler;
    private GaugeRunConfiguration config;

    public GaugeCommandLineState(GaugeRunProcessHandler handler, ExecutionEnvironment env, GaugeRunConfiguration config) {
        super(env);
        this.handler = handler;
        this.config = config;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        return handler;
    }


    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();
        TestConsoleProperties properties = new GaugeConsoleProperties(config, "Gauge", executor);
        ConsoleView console = SMTestRunnerConnectionUtil.createAndAttachConsole("Gauge", processHandler, properties);
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler));
    }

}
