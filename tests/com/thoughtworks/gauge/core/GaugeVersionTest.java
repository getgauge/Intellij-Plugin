package com.thoughtworks.gauge.core;

import org.junit.Assert;
import org.junit.Test;

public class GaugeVersionTest {
    @Test
    public void testIsGreaterThanWithHigherVersionsToInstalledVersion() throws Exception {
        Assert.assertTrue(new GaugeVersionInfo("0.4.2").isGreaterThan(new GaugeVersionInfo("0.4.0")));
        Assert.assertTrue(new GaugeVersionInfo("0.4.1.nightly-2016-05-12").isGreaterThan(new GaugeVersionInfo("0.4.0")));
        Assert.assertTrue(new GaugeVersionInfo("0.4.2").isGreaterThan(new GaugeVersionInfo("0.4.0")));
        Assert.assertTrue(new GaugeVersionInfo("0.4.0").isGreaterThan(new GaugeVersionInfo("0.3.5")));
        Assert.assertTrue(new GaugeVersionInfo("2.4.0").isGreaterThan(new GaugeVersionInfo("1.5.3")));
    }

    @Test
    public void testIsGreaterThanWithLowerVersionsToInstalledVersion() throws Exception {
        Assert.assertFalse(new GaugeVersionInfo("0.4.0").isGreaterThan(new GaugeVersionInfo("0.4.0")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.0").isGreaterThan(new GaugeVersionInfo("0.4.4")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.6").isGreaterThan(new GaugeVersionInfo("0.5.2")));
        Assert.assertFalse(new GaugeVersionInfo("2.4.0").isGreaterThan(new GaugeVersionInfo("3.6.3")));
    }

    @Test
    public void testIsLessThanWithLowerVersionsToInstalledVersion() throws Exception {
        Assert.assertTrue(new GaugeVersionInfo("0.4.0").isLessThan(new GaugeVersionInfo("0.4.4")));
        Assert.assertTrue(new GaugeVersionInfo("0.4.6").isLessThan(new GaugeVersionInfo("0.5.2")));
        Assert.assertTrue(new GaugeVersionInfo("2.4.0").isLessThan(new GaugeVersionInfo("3.6.3")));
    }

    @Test
    public void testIsLessThanWithHigherVersionsToInstalledVersion() throws Exception {
        Assert.assertFalse(new GaugeVersionInfo("0.4.0").isLessThan(new GaugeVersionInfo("0.4.0")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.2").isLessThan(new GaugeVersionInfo("0.4.0")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.1.nightly-2016-05-12").isLessThan(new GaugeVersionInfo("0.4.0")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.2").isLessThan(new GaugeVersionInfo("0.4.0")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.0").isLessThan(new GaugeVersionInfo("0.3.5")));
        Assert.assertFalse(new GaugeVersionInfo("2.4.0").isLessThan(new GaugeVersionInfo("1.5.3")));
    }
}