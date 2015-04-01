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

package com.thoughtworks.gauge.module.lib;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.GaugeConnection;
import com.thoughtworks.gauge.GaugeModuleComponent;
import com.thoughtworks.gauge.PluginNotInstalledException;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.thoughtworks.gauge.util.GaugeUtil.moduleDirFromModule;

public class GaugeLibHelper {
    public static final String PROJECT_LIB = "project-lib";
    public static final String GAUGE_LIB = "gauge-lib";
    public static final String JAVA = "java";
    private static final String SRC_DIR = new File(new File("src", "test"), JAVA).getPath();
    public static final String LIBS = "libs";

    public void checkGaugeLibs(final Module module) {
        final ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();

        if (!gaugeJavaLibIsAdded(modifiableModel)) {
            addGaugeJavaLib(modifiableModel);
        } else {
            updateGaugeJavaLibIfNeeded(modifiableModel);
        }
        addProjectLibIfNeeded(modifiableModel);
        checkProjectSourceAndOutputDirectory(modifiableModel);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                modifiableModel.commit();
            }
        });

    }

    private void checkProjectSourceAndOutputDirectory(ModifiableRootModel modifiableModel) {
        VirtualFile[] sourceRoots = modifiableModel.getSourceRoots();
        if (sourceRoots.length < 1) {
            ContentEntry contentEntry = modifiableModel.addContentEntry(modifiableModel.getProject().getBaseDir());
            VirtualFile srcPath = srcPath(modifiableModel);
            if (srcPath != null) {
                contentEntry.addSourceFolder(srcPath, false);
            }
            CompilerModuleExtension compilerModuleExtension = modifiableModel.getModuleExtension(CompilerModuleExtension.class);
            compilerModuleExtension.setCompilerOutputPath(outputPath(modifiableModel.getProject()));
            compilerModuleExtension.setCompilerOutputPathForTests(testOutputPath(modifiableModel.getProject()));
            compilerModuleExtension.inheritCompilerOutputPath(false);
            compilerModuleExtension.commit();
        }
    }

    private VirtualFile testOutputPath(Project project) {
        File outputDir = new File(String.format("%s%sout%stest%s%s", project.getBasePath(), File.separator, File.separator, File.separator, project.getBaseDir().getName()));
        outputDir.mkdirs();
        return  LocalFileSystem.getInstance().refreshAndFindFileByIoFile(outputDir);
    }

    private VirtualFile outputPath(Project project) {
        File outputDir = new File(String.format("%s%sout%sproduction%s%s", project.getBasePath(), File.separator, File.separator, File.separator, project.getBaseDir().getName()));
        outputDir.mkdirs();
        return  LocalFileSystem.getInstance().refreshAndFindFileByIoFile(outputDir);
    }

    private VirtualFile srcPath(ModifiableRootModel modifiableModel) {
        Project project = modifiableModel.getProject();
        return  LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(project.getBaseDir().getPath(), SRC_DIR));
    }

    private void addProjectLibIfNeeded(ModifiableRootModel model) {
        Library library = model.getModuleLibraryTable().getLibraryByName(PROJECT_LIB);
        if (library == null) {
            addProjectLib(model);
        }
    }

    private void updateGaugeJavaLibIfNeeded(ModifiableRootModel model) {
        LibraryTable libraryTable = model.getModuleLibraryTable();
        Library library = libraryTable.getLibraryByName(GAUGE_LIB);
        ProjectLib latestGaugeLib = gaugeLib(model.getModule());
        boolean updateLibEntry;
        try {
            String libEntry = library.getModifiableModel().getUrls(OrderRootType.CLASSES)[0];
            updateLibEntry = !(new URL(libEntry).getFile().equals(latestGaugeLib.getDir().getAbsolutePath()));
        } catch (MalformedURLException e) {
            updateLibEntry = true;
        }
        if (updateLibEntry) {
            libraryTable.removeLibrary(library);
            addLib(latestGaugeLib, model);
        }
    }

    private boolean gaugeJavaLibIsAdded(ModifiableRootModel model) {
        Library library = model.getModuleLibraryTable().getLibraryByName(GAUGE_LIB);
        return !(library == null);
    }

    public void addGaugeLibs(ModifiableRootModel modifiableRootModel) {
        addGaugeJavaLib(modifiableRootModel);
        addProjectLib(modifiableRootModel);
    }

    private void addGaugeJavaLib(ModifiableRootModel modifiableRootModel) {
        Module module = modifiableRootModel.getModule();
        ProjectLib gaugeLib = gaugeLib(module);
        if (gaugeLib != null) {
            addLib(gaugeLib, modifiableRootModel);
        }
    }

    private void addProjectLib(ModifiableRootModel modifiableRootModel) {
        addLib(projectLib(modifiableRootModel.getModule()), modifiableRootModel);
    }

    private void addLib(ProjectLib lib, ModifiableRootModel modifiableRootModel) {
        final Library library = modifiableRootModel.getModuleLibraryTable().createLibrary(lib.getLibName());
        final VirtualFile libDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(lib.getDir());
        if (libDir != null) {
            final Library.ModifiableModel libModel = library.getModifiableModel();
            libModel.addJarDirectory(libDir, true);
            libModel.commit();
        }
    }


    private ProjectLib projectLib(Module module) {
        return new ProjectLib(PROJECT_LIB, new File(moduleDirFromModule(module), LIBS));
    }

    private ProjectLib gaugeLib(Module module) {
        String libRoot;
        try {
            GaugeService gaugeService = Gauge.getGaugeService(module);
            if (gaugeService == null) {
                 gaugeService = GaugeModuleComponent.createGaugeService(module);
                Gauge.addModule(module, gaugeService);
            }
            GaugeConnection gaugeConnection = gaugeService.getGaugeConnection();
            if (gaugeConnection == null) {
                throw new IOException("Gauge api connection not established");
            }
            libRoot = gaugeConnection.getLibPath("java");
        } catch (IOException e) {
            System.err.println("Could not add gauge lib, add it manually: " + e.getMessage());
            return null;
        } catch (PluginNotInstalledException e) {
            throw new RuntimeException(JAVA + "could not be installed, try it manually");
        }
        return new ProjectLib(GAUGE_LIB, new File(libRoot));
    }

    private class ProjectLib {
        private String libName;
        public File dir;

        public ProjectLib(String libName, File dir) {
            this.libName = libName;
            this.dir = dir;
        }

        public String getLibName() {
            return libName;
        }

        public File getDir() {
            return dir;
        }
    }
}
