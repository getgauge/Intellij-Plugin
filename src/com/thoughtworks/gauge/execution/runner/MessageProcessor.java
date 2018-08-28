package com.thoughtworks.gauge.execution.runner;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;

import java.text.ParseException;

public interface MessageProcessor {
    boolean process(ServiceMessageBuilder msg, Integer nodeId, Integer parentId) throws ParseException;

    void process(String text);

    boolean processLineBreak();
}
