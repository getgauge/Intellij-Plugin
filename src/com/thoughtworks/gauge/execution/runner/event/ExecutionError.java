package com.thoughtworks.gauge.execution.runner.event;

public class ExecutionError {
    public String text;
    public String filename;
    public String message;
    public String stackTrace;

    public String format() {
        return format(this.text, "Failed: ", "\n") +
                format(this.filename, "Filename: ", "\n") +
                format(this.message, "Message: ", "\n") +
                format(this.stackTrace, "Stack Trace:\n", "");
    }

    private String format(String text, String prefix, String suffix) {
        return text != null && !text.isEmpty() ? prefix + text + suffix : "";
    }
}
