package com.thoughtworks.gauge.execution.runner.processors;


import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.Notifications;
import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;

public class NotificationEventProcessor extends GaugeEventProcessor {

    public NotificationEventProcessor(MessageProcessor processor, TestsCache cache) {
        super(processor, cache);
    }

    @Override
    Boolean onStart(ExecutionEvent event) {
        return true;
    }

    @Override
    Boolean onEnd(ExecutionEvent event) {
        String title = event.notification.title;
        String message = event.notification.message;
        Notification notification = new Notification("Gauge", title, message, event.notification.getType(), NotificationListener.URL_OPENING_LISTENER);
        Notifications.Bus.notify(notification);
        return true;
    }

    @Override
    public Boolean canProcess(ExecutionEvent event) {
        return event.type.equalsIgnoreCase(ExecutionEvent.NOTIFICATION);
    }
}
