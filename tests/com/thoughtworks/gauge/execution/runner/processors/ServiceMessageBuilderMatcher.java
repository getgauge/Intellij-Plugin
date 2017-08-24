package com.thoughtworks.gauge.execution.runner.processors;

import org.mockito.ArgumentMatcher;

public class ServiceMessageBuilderMatcher<ServiceMessageBuilder> extends ArgumentMatcher<ServiceMessageBuilder> {
    private ServiceMessageBuilder builder;

    public ServiceMessageBuilderMatcher(ServiceMessageBuilder builder) {
        this.builder = builder;
    }

    @Override
    public boolean matches(Object argument) {
        return argument.toString().equals(builder.toString());
    }
}
