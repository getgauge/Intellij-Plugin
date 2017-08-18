package com.thoughtworks.gauge.execution.runner;

public class ExecutionResult {
    public String status;
    public Integer time;
    public String out;
    public ExecutionError[] errors;
    public ExecutionError beforeHookFailure;
    public ExecutionError afterHookFailure;
    public TableInfo table;
}
