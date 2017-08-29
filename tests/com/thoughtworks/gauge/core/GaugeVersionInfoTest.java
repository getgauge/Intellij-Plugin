package com.thoughtworks.gauge.core;

import org.junit.Assert;
import org.junit.Test;

public class GaugeVersionInfoTest {
    @Test
    public void testIsGreaterThanWithGreaterVersionsToInstalledVersion() throws Exception {
        Assert.assertTrue(new GaugeVersionInfo("0.4.7").isGreaterOrEqual(new GaugeVersionInfo("0.4.4")));
        Assert.assertTrue(new GaugeVersionInfo("0.4.6").isGreaterOrEqual(new GaugeVersionInfo("0.3.6")));
        Assert.assertTrue(new GaugeVersionInfo("2.4.0").isGreaterOrEqual(new GaugeVersionInfo("1.6.3")));
        Assert.assertTrue(new GaugeVersionInfo("0.4.0").isGreaterOrEqual(new GaugeVersionInfo("0.4.0")));
    }

    @Test
    public void testIsGreaterThanWithLowerVersionsToInstalledVersion() throws Exception {
        Assert.assertFalse(new GaugeVersionInfo("0.4.2").isGreaterOrEqual(new GaugeVersionInfo("0.4.3")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.1.nightly-2016-05-12").isGreaterOrEqual(new GaugeVersionInfo("0.4.2")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.2").isGreaterOrEqual(new GaugeVersionInfo("0.5.0")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.0").isGreaterOrEqual(new GaugeVersionInfo("0.5.5")));
        Assert.assertFalse(new GaugeVersionInfo("2.4.0").isGreaterOrEqual(new GaugeVersionInfo("5.5.3")));
    }

}