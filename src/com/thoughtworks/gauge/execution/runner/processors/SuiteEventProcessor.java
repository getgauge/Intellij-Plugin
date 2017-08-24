package com.thoughtworks.gauge.execution.runner.processors;

import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;

import java.text.ParseException;

public class SuiteEventProcessor extends GaugeEventProcessor {
    private static final String BEFORE_SUITE = "Before Suite";
    private static final String AFTER_SUITE = "After Suite";
    static final int SUITE_ID = 0;

    public SuiteEventProcessor(MessageProcessor processor, TestsCache cache) {
        super(processor, cache);
    }

    @Override
    Boolean onStart(ExecutionEvent event) throws ParseException {
        return getProcessor().processLineBreak();
    }

    @Override
    Boolean onEnd(ExecutionEvent event) throws ParseException {
        return super.addHooks(event, BEFORE_SUITE, AFTER_SUITE, "", SUITE_ID);
    }

    @Override
    public Boolean canProcess(ExecutionEvent event) throws ParseException {
        return event.type.equalsIgnoreCase(ExecutionEvent.SUITE_START) ||
                event.type.equalsIgnoreCase(ExecutionEvent.SUITE_END);
    }
}
