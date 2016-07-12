package com.thoughtworks.gauge.execution;

import com.intellij.execution.configurations.RunProfile;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class GaugeTestRunnerTest {
    @Test
    public void shouldRunOnlyGaugeRunConfiguration() {
        RunProfile profile = mock(GaugeRunConfiguration.class);
        GaugeTestRunner runner = new GaugeTestRunner();

        assertTrue("Should run only GaugeRunConfiguration Expected: true, Actual: false", runner.canRun("", profile));
    }

    @Test
    public void shouldNotRunNonGaugeRunConfigurationProfile() {
        RunProfile profile = mock(RunProfile.class);
        GaugeTestRunner runner = new GaugeTestRunner();

        assertFalse("Should run only GaugeRunConfiguration Expected: false, Actual: true", runner.canRun("", profile));
    }
}