package com.thoughtworks.gauge.execution.runner.processors;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;

import java.text.ParseException;

public class UnexpectedEndProcessor extends GaugeEventProcessor {
    public UnexpectedEndProcessor(MessageProcessor processor, TestsCache cache) {
        super(processor, cache);
    }

    @Override
    Boolean onStart(ExecutionEvent event) throws ParseException {
        return true;
    }

    @Override
    Boolean onEnd(ExecutionEvent event) throws ParseException {
        String name = "Failed";
        ServiceMessageBuilder msg = ServiceMessageBuilder.testFailed(name);
        if (event.result.skipped()) {
            name = "Ignored";
            msg = ServiceMessageBuilder.testIgnored(name);
        }
        getProcessor().process(ServiceMessageBuilder.testStarted(name), 1, SuiteEventProcessor.SUITE_ID);
        msg.addAttribute("message", " ");
        getProcessor().process(msg, 1, 0);
        getProcessor().process(ServiceMessageBuilder.testFinished(name), 1, SuiteEventProcessor.SUITE_ID);
        return false;
    }

    @Override
    public Boolean process(ExecutionEvent event) throws ParseException {
        return onEnd(event);
    }

    @Override
    public Boolean canProcess(ExecutionEvent event) throws ParseException {
        return getCache().getCurrentId() == SuiteEventProcessor.SUITE_ID;
    }
}
