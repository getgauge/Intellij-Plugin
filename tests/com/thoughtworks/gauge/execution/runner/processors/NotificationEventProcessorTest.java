package com.thoughtworks.gauge.execution.runner.processors;

import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class NotificationEventProcessorTest {
    @Test
    public void canProcess() {
        NotificationEventProcessor processor = new NotificationEventProcessor(null, null);
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.NOTIFICATION;
        assertTrue(processor.canProcess(event));
    }
}