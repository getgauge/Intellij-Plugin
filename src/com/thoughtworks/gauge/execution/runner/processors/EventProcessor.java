package com.thoughtworks.gauge.execution.runner.processors;

import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;

import java.text.ParseException;

public interface EventProcessor {
    Boolean canProcess(ExecutionEvent event) throws ParseException;

    Boolean process(ExecutionEvent event) throws ParseException;
}
