package com.thoughtworks.gauge.execution.runner.event;

public class ExecutionError {
    public String text;
    public String filename;
    public String lineNo;
    public String message;
    public String stackTrace;

    public String format() {
        return format(this.text, "Failed: ", "\n") +
                format(getFileNameWithLineNo(), "Filename: ", "\n") +
                format(this.message, "Message: ", "\n") +
                format(this.stackTrace, "Stack Trace:\n", "");
    }

    private String getFileNameWithLineNo() {
        return lineNo.isEmpty() ? filename : format(":", filename, lineNo);

    }

    private String format(String text, String prefix, String suffix) {
        return text != null && !text.isEmpty() ? prefix + text + suffix : "";
    }
}
