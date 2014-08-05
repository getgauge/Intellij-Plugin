package com.thoughtworks.gauge.module;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.GaugeConnection;
import com.thoughtworks.gauge.GaugeModuleComponent;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.gauge.GaugeConstant.GAUGE;
import static com.thoughtworks.gauge.GaugeConstant.INIT_FLAG;

public class GaugeModuleBuilder extends JavaModuleBuilder {

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        super.setupRootModel(modifiableRootModel);
        gaugeInit(modifiableRootModel);
        addGaugeLibToModule(modifiableRootModel);
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, this, new Condition<SdkTypeId>() {
            @Override
            public boolean value(SdkTypeId sdkType) {
                return isSuitableSdkType(sdkType);
            }
        });
    }

    private void gaugeInit(ModifiableRootModel modifiableRootModel) {
        final String path = getPathForInitialization(modifiableRootModel);

        final String[] init = {
                GAUGE,
                INIT_FLAG, getLanguage()
        };
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(init);
            processBuilder.directory(new File(path, getName()));
            Process process = processBuilder.start();
            final int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Failed to initialize project");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addGaugeLibToModule(ModifiableRootModel modifiableRootModel) {
        File installationRoot;
        try {
            Module module = modifiableRootModel.getModule();
            GaugeService gaugeService = GaugeModuleComponent.createGaugeService(module);
            Gauge.addModule(module, gaugeService);
            GaugeConnection gaugeConnection = gaugeService.getGaugeConnection();
            if (gaugeConnection == null) {
                throw new IOException("Gauge api connection not established");
            }
            installationRoot = gaugeConnection.getInstallationRoot();
        } catch (IOException e) {
            System.out.println("Could not add gauge lib, add it manually: " + e.getMessage());
            return;
        }
        final Library library = modifiableRootModel.getModuleLibraryTable().createLibrary("gauge-lib");
        final File libsDir = new File(installationRoot.getAbsolutePath(), File.separator + "lib" + File.separator + GAUGE + File.separator + "java");
        final VirtualFile libDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(libsDir);
        final Library.ModifiableModel libModel = library.getModifiableModel();
        libModel.addJarDirectory(libDir, true);
        libModel.commit();
    }

    private String getLanguage() {
        return "java";
    }

    private String getPathForInitialization(ModifiableRootModel modifiableRootModel) {
        final Module module = modifiableRootModel.getModule();
        final boolean initialized = module.getProject().isInitialized();
        final String path;
        if (initialized) {
            path = module.getProject().getBasePath();
        } else {
            path = module.getProject().getBaseDir().getParent().getPath();
        }
        return path;
    }

    @Override
    public ModuleType getModuleType() {
        return GaugeModuleType.getInstance();
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        final List<Pair<String, String>> paths = new ArrayList<Pair<String, String>>();
        @NonNls final String path = getContentEntryPath() + File.separator + "src" + File.separator + "test" + File.separator + "java";
        paths.add(Pair.create(path, ""));
        return paths;
    }
}
