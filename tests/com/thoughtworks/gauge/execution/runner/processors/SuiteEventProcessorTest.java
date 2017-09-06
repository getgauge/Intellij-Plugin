package com.thoughtworks.gauge.execution.runner.processors;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;
import com.thoughtworks.gauge.execution.runner.event.ExecutionResult;
import org.junit.Test;

import static com.thoughtworks.gauge.execution.runner.processors.ScenarioEventProcessorTest.getError;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SuiteEventProcessorTest {
    @Test
    public void onEndWithoutFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SuiteEventProcessor processor = new SuiteEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SUITE_END;
        event.result = new ExecutionResult();

        processor.onEnd(event);

        verify(mockProcessor, never()).process(any(ServiceMessageBuilder.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void onEndWithBeforeHookFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SuiteEventProcessor processor = new SuiteEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SUITE_END;
        event.result = new ExecutionResult();
        event.result.beforeHookFailure = getError("text", "message", "filename", "","stacktrace");

        processor.onEnd(event);

        ServiceMessageBuilder started = ServiceMessageBuilder.testStarted("Before Suite");
        ServiceMessageBuilder failed = ServiceMessageBuilder.testFailed("Before Suite");
        failed.addAttribute("message", "Failed: text\nFilename: filename\nMessage: message\nStack Trace:\nstacktrace");
        ServiceMessageBuilder finished = ServiceMessageBuilder.testFinished("Before Suite");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(started)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(failed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }


    @Test
    public void onEndWithAfterHookFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SuiteEventProcessor processor = new SuiteEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SUITE_END;
        event.result = new ExecutionResult();
        event.result.afterHookFailure = getError("text", "message", "filename", "","stacktrace");

        processor.onEnd(event);

        ServiceMessageBuilder started = ServiceMessageBuilder.testStarted("After Suite");
        ServiceMessageBuilder failed = ServiceMessageBuilder.testFailed("After Suite");
        failed.addAttribute("message", "Failed: text\nFilename: filename\nMessage: message\nStack Trace:\nstacktrace");
        ServiceMessageBuilder finished = ServiceMessageBuilder.testFinished("After Suite");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(started)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(failed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void onEndWithBeforeAndAfterHookFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SuiteEventProcessor processor = new SuiteEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SUITE_END;
        event.result = new ExecutionResult();
        event.result.beforeHookFailure = getError("text", "message", "filename", "","stacktrace");
        event.result.afterHookFailure = getError("text1", "message1", "filename1", "","stacktrace1");

        processor.onEnd(event);

        ServiceMessageBuilder beforeStarted = ServiceMessageBuilder.testStarted("Before Suite");
        ServiceMessageBuilder beforeFailed = ServiceMessageBuilder.testFailed("Before Suite");
        beforeFailed.addAttribute("message", "Failed: text\nFilename: filename\nMessage: message\nStack Trace:\nstacktrace");
        ServiceMessageBuilder beforeFinished = ServiceMessageBuilder.testFinished("Before Suite");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeStarted)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeFailed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeFinished)), any(Integer.class), any(Integer.class));

        ServiceMessageBuilder afterStarted = ServiceMessageBuilder.testStarted("After Suite");
        ServiceMessageBuilder afterFailed = ServiceMessageBuilder.testFailed("After Suite");
        afterFailed.addAttribute("message", "Failed: text1\nFilename: filename1\nMessage: message1\nStack Trace:\nstacktrace1");
        ServiceMessageBuilder afterFinished = ServiceMessageBuilder.testFinished("After Suite");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterStarted)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterFailed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterFinished)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void canProcess() throws Exception {
        SuiteEventProcessor processor = new SuiteEventProcessor(null, null);
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SUITE_START;
        assertTrue(processor.canProcess(event));

        event.type = ExecutionEvent.SUITE_END;
        assertTrue(processor.canProcess(event));
    }

    @Test
    public void onStart() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SuiteEventProcessor processor = new SuiteEventProcessor(mockProcessor, new TestsCache());

        processor.onStart(null);

        verify(mockProcessor, never()).process(any(ServiceMessageBuilder.class), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).processLineBreak();
    }

}