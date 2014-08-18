package com.thoughtworks.gauge.util;

import com.intellij.util.EnvironmentUtil;
import com.thoughtworks.gauge.GaugeConstant;
import org.apache.commons.lang.StringUtils;

import java.io.File;

public class GaugeUtil {
    public static String getGaugeExecPath() {
        String path = EnvironmentUtil.getValue("PATH");
        String gaugeHome = EnvironmentUtil.getValue("GAUGE_ROOT");
        if (!StringUtils.isEmpty(gaugeHome)) {
            File bin = new File(gaugeHome, "bin");
            File gaugeExec = new File(bin, GaugeConstant.GAUGE);
            if (gaugeExec.exists()) {
                return gaugeExec.getAbsolutePath();
            }
        } else if (!StringUtils.isEmpty(path)) {
            for (String entry : path.split(File.pathSeparator)) {
                if (entry.contains(GaugeConstant.GAUGE)) {
                    if (new File(entry, GaugeConstant.GAUGE).exists()) {
                        return new File(entry, GaugeConstant.GAUGE).getAbsolutePath();
                    }
                }
            }
        }
        return GaugeConstant.GAUGE;
    }

}
