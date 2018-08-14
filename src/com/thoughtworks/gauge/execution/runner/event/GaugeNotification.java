package com.thoughtworks.gauge.execution.runner.event;


import com.intellij.notification.NotificationType;

public class GaugeNotification {
    public String title;
    public String message;
    public String type;

    public NotificationType getType() {
        switch (type) {
            case "error":
                return NotificationType.ERROR;
            case "warning":
                return NotificationType.WARNING;
            case "info":
                return NotificationType.INFORMATION;
            default:
                return NotificationType.INFORMATION;

        }

    }
}
