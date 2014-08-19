package com.thoughtworks.gauge.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.EnvironmentUtil;
import com.thoughtworks.gauge.GaugeConstant;
import org.apache.commons.lang.StringUtils;

import java.io.File;

public class GaugeUtil {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.GaugeUtil");

    public static String getGaugeExecPath() {
        String path = EnvironmentUtil.getValue("PATH");
        LOG.info("PATH => " + path);
        String gaugeHome = EnvironmentUtil.getValue("GAUGE_ROOT");
        LOG.info("GAUGE_ROOT => " + gaugeHome);
        if (!StringUtils.isEmpty(gaugeHome)) {
            File bin = new File(gaugeHome, "bin");
            File gaugeExec = new File(bin, GaugeConstant.GAUGE);
            if (gaugeExec.exists()) {
                return gaugeExec.getAbsolutePath();
            }
        } else if (!StringUtils.isEmpty(path)) {
            for (String entry : path.split(File.pathSeparator)) {
                File gaugeExec = new File(entry, GaugeConstant.GAUGE);
                if (gaugeExec.exists()) {
                    return gaugeExec.getAbsolutePath();
                }
            }
        }
        return GaugeConstant.GAUGE;
    }

}
