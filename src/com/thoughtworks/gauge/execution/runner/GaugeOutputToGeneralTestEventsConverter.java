package com.thoughtworks.gauge.execution.runner;

import com.google.gson.GsonBuilder;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.intellij.execution.testframework.sm.runner.GeneralTestEventsProcessor;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class GaugeOutputToGeneralTestEventsConverter extends OutputToGeneralTestEventsConverter {
    private static final String BEFORE = "Before";
    private static final String AFTER = "After";
    private static final int DEFAULT_ID = 0;
    private static final String GAUGE_FILE_PREFIX = "gauge://";
    private int id = 0;
    private Key outputType;
    private ServiceMessageVisitor visitor;
    private Map<String, Integer> idCache = new HashMap<>();

    public GaugeOutputToGeneralTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        super(testFrameworkName, consoleProperties);
    }

    @Override
    public void setProcessor(@Nullable GeneralTestEventsProcessor processor) {
        super.setProcessor(processor);
        if (processor != null)
            processor.onRootPresentationAdded("Test Suite", null, null);
    }

    @Override
    protected boolean processServiceMessages(String text, Key outputType, ServiceMessageVisitor visitor) throws ParseException {
        this.outputType = outputType;
        this.visitor = visitor;
        if (text.startsWith("{")) {
            ExecutionEvent event = new GsonBuilder().create().fromJson(text, ExecutionEvent.class);
            if (event.type.equalsIgnoreCase(ExecutionEvent.SUITE_START))
                return true;
            else if (event.type.equalsIgnoreCase(ExecutionEvent.SPEC_START))
                return processSpecStarted(event);
            else if (event.type.equalsIgnoreCase(ExecutionEvent.SPEC_END))
                return processSpecEnd(event);
            else if (event.type.equalsIgnoreCase(ExecutionEvent.SCENARIO_START))
                return processScenarioStarted(event, idCache.get(event.parentId));
            else if (event.type.equalsIgnoreCase(ExecutionEvent.SCENARIO_END))
                return processScenarioEnd(event, idCache.get(event.parentId));
            else if (event.type.equalsIgnoreCase(ExecutionEvent.SUITE_END))
                return processSuiteEnd(event);
        }
        return super.processServiceMessages(text, outputType, visitor);
    }

    private boolean processSuiteEnd(ExecutionEvent event) throws ParseException {
        return finishHooks(event, BEFORE, AFTER, "", DEFAULT_ID);
    }

    private boolean processSpecStarted(ExecutionEvent event) throws ParseException {
        idCache.put(event.id, ++this.id);
        if (!idCache.containsKey(event.id.split(":")[0]))
            idCache.put(event.id.split(":")[0], this.id);
        ServiceMessageBuilder msg = ServiceMessageBuilder.testSuiteStarted(event.name);
        addLocation(event, msg);
        return processMessage(msg, this.id, DEFAULT_ID);
    }

    private boolean processSpecEnd(ExecutionEvent event) throws ParseException {
        finishHooks(event, BEFORE, AFTER, event.id, idCache.get(event.id));
        ServiceMessageBuilder msg = ServiceMessageBuilder.testSuiteFinished(event.name);
        addAttribute(msg, "duration", String.valueOf(event.result.time));
        return processMessage(msg, idCache.get(event.id), DEFAULT_ID);
    }

    private boolean processScenarioStarted(ExecutionEvent event, int parentId) throws ParseException {
        return addTest(getScenarioIdentifier(event, event.name), parentId, getScenarioIdentifier(event, event.id), event);
    }

    private boolean processScenarioEnd(ExecutionEvent event, int parentId) throws ParseException {
        int id = idCache.get(getScenarioIdentifier(event, event.id));
        String name = getScenarioIdentifier(event, event.name);
        if (event.result.status.equalsIgnoreCase(ExecutionEvent.FAIL))
            scenarioFailedMessage(ServiceMessageBuilder.testFailed(name), id, parentId, event.result);
        ServiceMessageBuilder scenarioEnd = ServiceMessageBuilder.testFinished(name);
        addAttribute(scenarioEnd, "duration", String.valueOf(event.result.time));
        return processMessage(scenarioEnd, id, parentId);
    }

    private boolean finishHooks(ExecutionEvent event, String before, String after, String prefix, int parentId) throws ParseException {
        failTest(parentId, before, event.result.beforeHookFailure, prefix + before, event);
        failTest(parentId, after, event.result.afterHookFailure, prefix + after, event);
        return true;
    }

    private void failTest(Integer parentId, String name, ExecutionError failure, String key, ExecutionEvent event) throws ParseException {
        if (failure != null) {
            addTest(name, parentId, key, event);
            ServiceMessageBuilder failed = ServiceMessageBuilder.testFailed(name);
            addErrorMessage(failed, failure);
            processMessage(failed, idCache.get(key), parentId);
            processMessage(ServiceMessageBuilder.testFinished(name), this.id, parentId);
        }
    }

    private boolean addTest(String name, Integer parentId, String key, ExecutionEvent event) throws ParseException {
        ServiceMessageBuilder test = ServiceMessageBuilder.testStarted(name);
        addLocation(event, test);
        idCache.put(key, ++this.id);
        return processMessage(test, this.id, parentId);
    }

    private void scenarioFailedMessage(ServiceMessageBuilder msg, int nodeId, int parentId, ExecutionResult result) throws ParseException {
        List<ExecutionError> errors = new ArrayList<>();
        if (result.beforeHookFailure != null) errors.add(result.beforeHookFailure);
        if (result.errors != null) errors.addAll(Arrays.asList(result.errors));
        if (result.afterHookFailure != null) errors.add(result.afterHookFailure);
        addAttribute(msg, "message", errors.stream().map(this::formatError).collect(Collectors.joining("\n\n")));
        processMessage(msg, nodeId, parentId);
    }

    private String formatError(ExecutionError e) {
        return format(e.text, "Failed: ", "\n") +
                format(e.filename, "Filename: ", "\n") +
                format(e.message, "Message: ", "\n") +
                format(e.stackTrace, "Stack Trace:\n", "");
    }

    private String format(String text, String prefix, String suffix) {
        return text != null && !text.isEmpty() ? prefix + text + suffix : "";
    }

    private void addErrorMessage(ServiceMessageBuilder msg, ExecutionError error) {
        addAttribute(msg, "message", formatError(error));
    }

    private void addLocation(ExecutionEvent event, ServiceMessageBuilder msg) {
        addAttribute(msg, "locationHint", GAUGE_FILE_PREFIX + event.filename + ":" + event.line.toString());
    }

    private boolean processMessage(ServiceMessageBuilder msg, int nodeId, int parentId) throws ParseException {
        addAttribute(msg, "nodeId", String.valueOf(nodeId));
        addAttribute(msg, "parentNodeId", String.valueOf(parentId));
        super.processServiceMessages(msg.toString(), outputType, visitor);
        return true;
    }

    private void addAttribute(ServiceMessageBuilder scenarioEnd, String duration, String value) {
        scenarioEnd.addAttribute(duration, value);
    }

    private String getScenarioIdentifier(ExecutionEvent event, String value) {
        return event.result.table != null ? value + ":" + String.valueOf(event.result.table.row + 1) : value;
    }
}
