package com.thoughtworks.gauge.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;

public class GaugeRunProcessHandler extends ColoredProcessHandler {

    private GaugeRunProcessHandler(Process process, String commandLineString) {
        super(process, commandLineString);
    }

    public static GaugeRunProcessHandler runCommandLine(final GeneralCommandLine commandLine) throws ExecutionException {
        final GaugeRunProcessHandler twistAppProcess = new GaugeRunProcessHandler(commandLine.createProcess(),commandLine.getCommandLineString());
        ProcessTerminatedListener.attach(twistAppProcess);
        return twistAppProcess;
    }
}
