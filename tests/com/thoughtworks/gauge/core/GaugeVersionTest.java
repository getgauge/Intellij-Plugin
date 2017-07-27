package com.thoughtworks.gauge.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GaugeVersionTest {

    private List<Plugin> plugins;

    @Before
    public void setup() {
        Plugin java = new Plugin();
        java.name = "java";
        Plugin spectacle = new Plugin();
        spectacle.name = "spectacle";
        plugins = Arrays.asList(java, spectacle);
    }

    @Test
    public void testIsPluginInstalledForInstalledPlugin() throws Exception {
        Assert.assertTrue(new GaugeVersionInfo("0.4.0", plugins).isPluginInstalled(plugins.get(0).name));
    }

    @Test
    public void testIsPluginInstalledForUnInstalledPlugin() throws Exception {
        Assert.assertFalse(new GaugeVersionInfo("0.4.0", plugins).isPluginInstalled("haha"));
    }
}