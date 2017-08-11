// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ExternalProjectSystemRegistry;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.ProjectModelExternalSource;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.EnvironmentUtil;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.GaugeConstant;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.settings.GaugeSettingsService;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import java.io.*;
import java.util.Map;

import static com.thoughtworks.gauge.Constants.SPECS_DIR;

public class GaugeUtil {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.GaugeUtil");
    private static GaugeSettingsModel gaugeSettings = null;

    public static GaugeSettingsModel getGaugeSettings() throws GaugeNotFoundException {
        GaugeSettingsModel settings = GaugeSettingsService.getSettings();
        LOG.info(settings.toString());
        if (settings.isGaugePathSet()) {
            LOG.info("Using Gauge plugin settings to get Gauge executable path.");
            return settings;
        }
        if (gaugeSettings == null) gaugeSettings = getSettingsFromPATH(settings);
        return gaugeSettings;
    }

    private static GaugeSettingsModel getSettingsFromPATH(GaugeSettingsModel model) throws GaugeNotFoundException {
        String path = EnvironmentUtil.getValue("PATH");
        LOG.info("PATH => " + path);
        if (!StringUtils.isEmpty(path)) {
            for (String entry : path.split(File.pathSeparator)) {
                File file = new File(entry, gaugeExecutable());
                if (isValidGaugeExec(file)) {
                    LOG.info("executable path from `PATH`: " + file.getAbsolutePath());
                    return new GaugeSettingsModel(file.getAbsolutePath(), model.getHomePath());
                }
            }
        }
        String msg = "Could not find executable in `PATH`. Please make sure Gauge is installed." +
                "\nIf Gauge is installed then set the Gauge executable path in settings -> tools -> gauge.";
        throw new GaugeNotFoundException(msg);
    }

    private static boolean isValidGaugeExec(File file) {
        return file.exists() && file.isFile() && file.canExecute();
    }

    @Nullable
    private static String getBinaryPathFrom(String name, String value) {
        LOG.info(String.format("%s => %s", name, value));
        if (!StringUtils.isEmpty(value)) {
            File bin = new File(value, "bin");
            File gaugeExec = new File(bin, gaugeExecutable());
            if (gaugeExec.exists()) {
                LOG.info(String.format("executable path from `%s` : %s", name, gaugeExec.getAbsolutePath()));
                return gaugeExec.getAbsolutePath();
            }
        }
        return null;
    }

    private static String gaugeExecutable() {
        return isWindows() ? Constants.GAUGE + ".exe" : Constants.GAUGE;
    }

    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().contains("win"));
    }

    public static boolean isGaugeFile(VirtualFile file) {
        return file.getFileType() instanceof SpecFileType || file.getFileType() instanceof ConceptFileType;
    }

    public static boolean isMavenModule(Module module) {
        ProjectModelExternalSource source = ExternalProjectSystemRegistry.getInstance().getExternalSource(module);
        return source != null && "maven".equalsIgnoreCase(source.getId());
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
        if (module == null) return null;
        if (GaugeUtil.isGradleModule(module)) return GaugeUtil.getProjectDirForGradleProject(module);
        String pathname = moduleDirPath(module);
        if (pathname != null) return new File(pathname);
        String basePath = module.getProject().getBasePath();
        return basePath != null ? new File(basePath) : null;
    }

    public static String moduleDirPath(Module module) {
        return PathMacroUtil.getModuleDir(module.getModuleFilePath());
    }


    public static String classpathForModule(Module module) {
        if (GaugeUtil.isGradleModule(module)) {
            String cp = "";
            for (Module module1 : Gauge.getSubModules(module))
                cp += OrderEnumerator.orderEntries(module1).recursively().getPathsList().getPathsString() + ":";
            return cp;
        }
        return OrderEnumerator.orderEntries(module).recursively().getPathsList().getPathsString();
    }

    public static boolean isSpecFile(PsiFile file) {
        return file instanceof SpecFile;
    }


    public static boolean isSpecFile(VirtualFile selectedFile) {
        return selectedFile.getFileType().getClass().equals(SpecFileType.class);
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

    public static boolean isGradleModule(Module module) {
        ProjectModelExternalSource source = ExternalProjectSystemRegistry.getInstance().getExternalSource(module);
        return source != null && "gradle".equalsIgnoreCase(source.getId());
    }

    public static boolean isGaugeElement(PsiElement element) {
        return StepUtil.isMethod(element) ? StepUtil.getGaugeStepAnnotationValues((PsiMethod) element).size() > 0 : (StepUtil.isConcept(element) || StepUtil.isStep(element));
    }

    @NotNull
    public static File getProjectDirForGradleProject(Module module) {
        return new File(module.getModuleFilePath().contains(".idea") ? module.getModuleFilePath().split(".idea")[0] : new File(module.getModuleFilePath()).getParent());
    }

    public static String getOutput(InputStream stream, String lineSeparator) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String lastProcessStdout = "";
        String line;
        while ((line = br.readLine()) != null)
            lastProcessStdout = lastProcessStdout.concat(line).concat(lineSeparator);
        return lastProcessStdout;
    }

    public static void setGaugeEnvironmentsTo(ProcessBuilder processBuilder, GaugeSettingsModel settings) {
        Map<String, String> env = processBuilder.environment();
        env.put(Constants.GAUGE_HOME, settings.getHomePath());
    }
}
