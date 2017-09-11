package com.thoughtworks.gauge.execution.runner.processors;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionError;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;

import java.text.ParseException;

abstract class GaugeEventProcessor implements EventProcessor {
    private static final String FILE_PREFIX = "gauge://";
    private MessageProcessor processor;
    private TestsCache cache;

    GaugeEventProcessor(MessageProcessor processor, TestsCache cache) {
        this.processor = processor;
        this.cache = cache;
    }

    abstract Boolean onStart(ExecutionEvent event) throws ParseException;

    abstract Boolean onEnd(ExecutionEvent event) throws ParseException;

    public Boolean process(ExecutionEvent event) throws ParseException {
        return event.type.endsWith("Start") ? onStart(event) : onEnd(event);
    }

    MessageProcessor getProcessor() {
        return processor;
    }

    TestsCache getCache() {
        return cache;
    }

    boolean addHooks(ExecutionEvent event, String before, String after, String prefix, Integer parentId) throws ParseException {
        failTest(parentId, before, event.result.beforeHookFailure, prefix + before, event);
        failTest(parentId, after, event.result.afterHookFailure, prefix + after, event);
        return true;
    }

    boolean addTest(String name, Integer parentId, String key, ExecutionEvent event) throws ParseException {
        ServiceMessageBuilder test = ServiceMessageBuilder.testStarted(name);
        addLocation(event, test);
        getCache().setId(key);
        return getProcessor().process(test, getCache().getId(key), parentId);
    }

    void addLocation(ExecutionEvent event, ServiceMessageBuilder msg) {
        if (event.filename != null && event.line != null)
            msg.addAttribute("locationHint", FILE_PREFIX + event.filename + Constants.SPEC_SCENARIO_DELIMITER + event.line.toString());
    }

    private void failTest(Integer parentId, String name, ExecutionError failure, String key, ExecutionEvent event) throws ParseException {
        if (failure != null) {
            addTest(name, parentId, key, event);
            ServiceMessageBuilder failed = ServiceMessageBuilder.testFailed(name);
            failed.addAttribute("message", failure.format("Failed"));
            getProcessor().process(failed, getCache().getId(key), parentId);
            getProcessor().process(ServiceMessageBuilder.testFinished(name), getCache().getId(key), parentId);
        }
    }
}
