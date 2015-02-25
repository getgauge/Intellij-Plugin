package com.thoughtworks.gauge.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EnvironmentUtil;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.GaugeConstant;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import org.apache.commons.lang.StringUtils;

import java.io.File;

import static com.thoughtworks.gauge.Constants.*;

public class GaugeUtil {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.GaugeUtil");
    private static String gaugePath = null;

    public static String getGaugeExecPath() throws GaugeNotFoundException {
        if (gaugePath == null) {
            gaugePath = getPath();
        }
        return gaugePath;
    }

    private static String getPath() throws GaugeNotFoundException {
        String path = EnvironmentUtil.getValue("PATH");
        LOG.info("PATH => " + path);
        String gaugeHome = EnvironmentUtil.getValue("GAUGE_ROOT");
        LOG.info("GAUGE_ROOT => " + gaugeHome);
        if (!StringUtils.isEmpty(gaugeHome)) {
            File bin = new File(gaugeHome, "bin");
            File gaugeExec = new File(bin, GaugeConstant.GAUGE);
            if (gaugeExec.exists()) {
                LOG.info("executable path: " + gaugeExec.getAbsolutePath());
                return gaugeExec.getAbsolutePath();
            }
        } else if (!StringUtils.isEmpty(path)) {
            for (String entry : path.split(File.pathSeparator)) {
                File gaugeExec = new File(entry, GaugeConstant.GAUGE);
                if (gaugeExec.exists()) {
                    LOG.info("executable path: " + gaugeExec.getAbsolutePath());
                    return gaugeExec.getAbsolutePath();
                }
            }
        }
        LOG.warn("Could not find executable in PATH or GAUGE_ROOT");
        throw new GaugeNotFoundException("Could not find executable in PATH or GAUGE_ROOT. Gauge is not installed.");
    }

    public static boolean isGaugeFile(VirtualFile file) {
        String extension = file.getExtension().toLowerCase();
        if (extension.equals(SPEC_EXTENSION) || extension.equals(MD_EXTENSION) || extension.equals(CONCEPT_EXTENSION)) {
            return true;
        }
        return false;
    }

    public static boolean isGaugeProjectDir(VirtualFile dir) {
        File projectDir = new File(dir.getPath());
        if (containsManifest(projectDir) && containsSpecsDir(projectDir)) {
            return true;
        }
        return false;
    }

    private static boolean containsSpecsDir(File projectDir) {
        File specDir = new File(projectDir, Constants.SPECS_DIR);
        return specDir.exists() && specDir.isDirectory();
    }

    private static boolean containsManifest(File projectDir) {
        return new File(projectDir, Constants.MANIFEST_FILE).exists();
    }

    public static File moduleDirFromModule(Module module) {
        return new File(module.getModuleFilePath()).getParentFile();
    }
}
