package com.thoughtworks.gauge.execution.runner.processors;

import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;
import org.apache.commons.lang3.StringUtils;

public class StandardOutputEventProcessor extends GaugeEventProcessor {

    public StandardOutputEventProcessor(MessageProcessor processor, TestsCache cache) {
        super(processor, cache);
    }

    @Override
    Boolean onStart(ExecutionEvent event) {
        return true;
    }

    @Override
    Boolean onEnd(ExecutionEvent event) {
        getProcessor().process(StringUtils.appendIfMissing(event.message, "\n"));
        getProcessor().processLineBreak();
        return true;
    }

    @Override
    public Boolean canProcess(ExecutionEvent event) {
        return event.type.equalsIgnoreCase(ExecutionEvent.STANDARD_OUTPUT);
    }
}
