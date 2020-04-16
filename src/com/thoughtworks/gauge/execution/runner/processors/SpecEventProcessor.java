/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.execution.runner.processors;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;

import java.text.ParseException;

public class SpecEventProcessor extends GaugeEventProcessor {
    private static final String BEFORE_SPEC = "Before Specification";
    private static final String AFTER_SPEC = "After Specification";

    public SpecEventProcessor(MessageProcessor processor, TestsCache cache) {
        super(processor, cache);
    }

    @Override
    Boolean onStart(ExecutionEvent event) throws ParseException {
        if (getCache().getCurrentId() == SuiteEventProcessor.SUITE_ID) getProcessor().processLineBreak();
        getCache().setId(event.id);
        if (getCache().getId(event.id.split(Constants.SPEC_SCENARIO_DELIMITER)[0]) == null)
            getCache().setId(event.id.split(Constants.SPEC_SCENARIO_DELIMITER)[0], getCache().getId(event.id));
        ServiceMessageBuilder msg = ServiceMessageBuilder.testSuiteStarted(event.name);
        super.addLocation(event, msg);
        return getProcessor().process(msg, getCache().getId(event.id), SuiteEventProcessor.SUITE_ID);
    }

    @Override
    Boolean onEnd(ExecutionEvent event) throws ParseException {
        super.addHooks(event, BEFORE_SPEC, AFTER_SPEC, event.id, getCache().getId(event.id));
        ServiceMessageBuilder msg = ServiceMessageBuilder.testSuiteFinished(event.name);
        msg.addAttribute("duration", String.valueOf(event.result.time));
        return getProcessor().process(msg, getCache().getId(event.id), SuiteEventProcessor.SUITE_ID);
    }

    @Override
    public Boolean canProcess(ExecutionEvent event) throws ParseException {
        return event.type.equalsIgnoreCase(ExecutionEvent.SPEC_START) ||
                event.type.equalsIgnoreCase(ExecutionEvent.SPEC_END);
    }
}
