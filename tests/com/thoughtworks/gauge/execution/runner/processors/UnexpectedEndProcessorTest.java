package com.thoughtworks.gauge.execution.runner.processors;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UnexpectedEndProcessorTest {

    @Test
    public void canProcess() throws Exception {
        UnexpectedEndProcessor processor = new UnexpectedEndProcessor(null, new TestsCache());
        assertTrue(processor.canProcess(null));

        TestsCache cache = new TestsCache();
        cache.setId("sdsdf");
        processor = new UnexpectedEndProcessor(null, cache);
        assertFalse(processor.canProcess(null));
    }

    @Test
    public void onEnd() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        UnexpectedEndProcessor processor = new UnexpectedEndProcessor(mockProcessor, new TestsCache());

        processor.onEnd(null);

        ServiceMessageBuilder started = ServiceMessageBuilder.testStarted("Failed");
        ServiceMessageBuilder failed = ServiceMessageBuilder.testFailed("Failed");
        failed.addAttribute("message", "");
        ServiceMessageBuilder finished = ServiceMessageBuilder.testFinished("Failed");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(started)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(failed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }
}