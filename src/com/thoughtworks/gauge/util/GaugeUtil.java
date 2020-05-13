/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

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
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.settings.GaugeSettingsService;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.serialization.PathMacroUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

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
                    return new GaugeSettingsModel(file.getAbsolutePath(), model.getHomePath(), model.useIntelliJTestRunner());
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

    /**
     * Returns whether or not the given file is a Gauge project directory. A file is a Gauge project directory if it
     * is a directory that contains both a manifest file and a specs directory.
     * @param dir the file to be examined.
     * @return whether or not the given file is a Gauge project directory.
     */
    public static boolean isGaugeProjectDir(File dir) {
        return containsManifest(dir);
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
                cp += OrderEnumerator.orderEntries(module1).recursively().getPathsList().getPathsString() + Constants.CLASSPATH_DELIMITER;
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

        if (module.getModuleFilePath().contains(".idea/modules")) {
            final String[] parts = module.getModuleFilePath().split("[.]\\bidea/modules\\b");

            return new File(parts[0] + parts[1].replaceAll("/[^/]+[.]\\biml\\b", ""));
        }

        return new File(new File(module.getModuleFilePath()).getParent());
    }

    public static String getOutput(InputStream stream, String lineSeparator) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String lastProcessStdout = "";
        String line;
        while ((line = br.readLine()) != null)
            if (!line.startsWith("[DEPRECATED]"))
                lastProcessStdout = lastProcessStdout.concat(line).concat(lineSeparator);
        return lastProcessStdout;
    }

    public static void setGaugeEnvironmentsTo(ProcessBuilder processBuilder, GaugeSettingsModel settings) {
        Map<String, String> env = processBuilder.environment();
        env.put(Constants.GAUGE_HOME, settings.getHomePath());
    }
}
