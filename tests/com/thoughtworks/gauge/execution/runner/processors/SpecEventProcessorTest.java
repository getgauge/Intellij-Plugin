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

public class SpecEventProcessorTest {
    @Test
    public void onEndWithoutFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SpecEventProcessor processor = new SpecEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SPEC_END;
        event.name = "name";
        event.result = new ExecutionResult();
        event.result.time = 15;

        processor.onEnd(event);

        ServiceMessageBuilder finished = ServiceMessageBuilder.testSuiteFinished("name");
        finished.addAttribute("duration", "15");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void onEndWithBeforeHookFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SpecEventProcessor processor = new SpecEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SPEC_END;
        event.name = "name";
        event.result = new ExecutionResult();
        event.result.time = 10;
        event.result.beforeHookFailure = getError("text", "message", "filename", "stacktrace");

        processor.onEnd(event);

        ServiceMessageBuilder beforeStarted = ServiceMessageBuilder.testStarted("Before Specification");
        ServiceMessageBuilder beforeFailed = ServiceMessageBuilder.testFailed("Before Specification");
        beforeFailed.addAttribute("message", "Failed: text\nFilename: filename\nMessage: message\nStack Trace:\nstacktrace");
        ServiceMessageBuilder beforeFinished = ServiceMessageBuilder.testFinished("Before Specification");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeStarted)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeFailed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeFinished)), any(Integer.class), any(Integer.class));

        ServiceMessageBuilder finished = ServiceMessageBuilder.testSuiteFinished("name");
        finished.addAttribute("duration", "10");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }


    @Test
    public void onEndWithAfterHookFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SpecEventProcessor processor = new SpecEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SPEC_END;
        event.name = "name";
        event.result = new ExecutionResult();
        event.result.time = 10;
        event.result.afterHookFailure = getError("text1", "message1", "filename1", "stacktrace1");

        processor.onEnd(event);

        ServiceMessageBuilder afterStarted = ServiceMessageBuilder.testStarted("After Specification");
        ServiceMessageBuilder afterFailed = ServiceMessageBuilder.testFailed("After Specification");
        afterFailed.addAttribute("message", "Failed: text1\nFilename: filename1\nMessage: message1\nStack Trace:\nstacktrace1");
        ServiceMessageBuilder afterFinished = ServiceMessageBuilder.testFinished("After Specification");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterStarted)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterFailed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterFinished)), any(Integer.class), any(Integer.class));

        ServiceMessageBuilder finished = ServiceMessageBuilder.testSuiteFinished("name");
        finished.addAttribute("duration", "10");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void onEndWithBeforeAndAfterHookFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SpecEventProcessor processor = new SpecEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SPEC_END;
        event.name = "name";
        event.result = new ExecutionResult();
        event.result.time = 10;
        event.result.beforeHookFailure = getError("text", "message", "filename", "stacktrace");
        event.result.afterHookFailure = getError("text1", "message1", "filename1", "stacktrace1");

        processor.onEnd(event);

        ServiceMessageBuilder beforeStarted = ServiceMessageBuilder.testStarted("Before Specification");
        ServiceMessageBuilder beforeFailed = ServiceMessageBuilder.testFailed("Before Specification");
        beforeFailed.addAttribute("message", "Failed: text\nFilename: filename\nMessage: message\nStack Trace:\nstacktrace");
        ServiceMessageBuilder beforeFinished = ServiceMessageBuilder.testFinished("Before Specification");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeStarted)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeFailed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(beforeFinished)), any(Integer.class), any(Integer.class));

        ServiceMessageBuilder afterStarted = ServiceMessageBuilder.testStarted("After Specification");
        ServiceMessageBuilder afterFailed = ServiceMessageBuilder.testFailed("After Specification");
        afterFailed.addAttribute("message", "Failed: text1\nFilename: filename1\nMessage: message1\nStack Trace:\nstacktrace1");
        ServiceMessageBuilder afterFinished = ServiceMessageBuilder.testFinished("After Specification");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterStarted)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterFailed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(afterFinished)), any(Integer.class), any(Integer.class));

        ServiceMessageBuilder finished = ServiceMessageBuilder.testSuiteFinished("name");
        finished.addAttribute("duration", "10");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void canProcess() throws Exception {
        SpecEventProcessor processor = new SpecEventProcessor(null, null);
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SPEC_START;
        assertTrue(processor.canProcess(event));

        event.type = ExecutionEvent.SPEC_END;
        assertTrue(processor.canProcess(event));
    }

    @Test
    public void onStart() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        SpecEventProcessor processor = new SpecEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SPEC_START;
        event.name = "name";
        event.id = "name";

        processor.onStart(event);

        ServiceMessageBuilder started = ServiceMessageBuilder.testSuiteStarted("name");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(started)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).processLineBreak();
    }

    @Test
    public void onStartForSecondSpec() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        TestsCache cache = new TestsCache();
        cache.setId("fsd");
        SpecEventProcessor processor = new SpecEventProcessor(mockProcessor, cache);
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SPEC_START;
        event.name = "name";
        event.id = "name";

        processor.onStart(event);

        ServiceMessageBuilder started = ServiceMessageBuilder.testSuiteStarted("name");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(started)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, never()).processLineBreak();
    }
}