package com.thoughtworks.gauge.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.EnvironmentUtil;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.GaugeConstant;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.SpecFileType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import java.io.File;

import static com.thoughtworks.gauge.Constants.SPECS_DIR;

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
            File gaugeExec = new File(bin, gaugeExecutable());
            if (gaugeExec.exists()) {
                LOG.info("executable path: " + gaugeExec.getAbsolutePath());
                return gaugeExec.getAbsolutePath();
            }
        } else if (!StringUtils.isEmpty(path)) {
            for (String entry : path.split(File.pathSeparator)) {
                File gaugeExec = new File(entry, gaugeExecutable());
                if (gaugeExec.exists()) {
                    LOG.info("executable path: " + gaugeExec.getAbsolutePath());
                    return gaugeExec.getAbsolutePath();
                }
            }
        }
        LOG.warn("Could not find executable in PATH or GAUGE_ROOT");
        throw new GaugeNotFoundException("Could not find executable in PATH or GAUGE_ROOT. Gauge is not installed.");
    }

    private static String gaugeExecutable() {
        if (isWindows()) {
            return GaugeConstant.GAUGE + ".exe";
        } else {
            return GaugeConstant.GAUGE;
        }

    }

    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().contains("win"));
    }

    public static boolean isGaugeFile(VirtualFile file) {
        return file.getFileType() instanceof SpecFileType || file.getFileType() instanceof ConceptFileType;
    }

    public static boolean isGaugeProjectDir(File dir) {
        return containsManifest(dir) && containsSpecsDir(dir);
    }

    private static boolean containsSpecsDir(File projectDir) {
        File specDir = new File(projectDir, SPECS_DIR);
        return specDir.exists() && specDir.isDirectory();
    }

    private static boolean containsManifest(File projectDir) {
        return new File(projectDir, Constants.MANIFEST_FILE).exists();
    }

    public static File moduleDir(Module module) {
        return new File(moduleDirPath(module));
    }

    public static String moduleDirPath(Module module) {
        return PathMacroUtil.getModuleDir(module.getModuleFilePath());
    }


    public static String classpathForModule(Module module) {
        return OrderEnumerator.orderEntries(module).recursively().getPathsList().getPathsString();
    }

    public static boolean isSpecFile(PsiFile file) {
            return file instanceof SpecFile;
    }

    public static boolean isGaugeModule(Module module) {
        return isGaugeProjectDir(moduleDir(module));
    }

    public static Module moduleForPsiElement(PsiElement element) {
        PsiFile file = element.getContainingFile();
        if (file == null) {
            return ModuleUtil.findModuleForPsiElement(element);
        }
        return ModuleUtil.findModuleForPsiElement(file);
    }
}
