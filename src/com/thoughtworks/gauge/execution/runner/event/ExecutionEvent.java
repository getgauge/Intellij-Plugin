package com.thoughtworks.gauge.execution.runner.event;

public class ExecutionEvent {
    public String type;
    public String id;
    public String filename;
    public Integer line;
    public String parentId;
    public String name;
    public String message;
    public GaugeNotification notification;
    public ExecutionResult result;
    public static final String SUITE_START = "suiteStart";
    public static final String SPEC_START = "specStart";
    public static final String SPEC_END = "specEnd";
    public static final String SCENARIO_START = "scenarioStart";
    public static final String SCENARIO_END = "scenarioEnd";
    public static final String SUITE_END = "suiteEnd";
    public static final String NOTIFICATION = "notification";
    public static final String STANDARD_OUTPUT = "out";
    public static final String FAIL = "fail";
    public static final String SKIP = "skip";
}
