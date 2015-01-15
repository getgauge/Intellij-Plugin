package com.thoughtworks.gauge.core;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.language.SpecFileType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;

public class GaugeApplicationComponent implements ApplicationComponent{
    private static final String GAUGE_TEMPLATE_NAME = "Specification";
    private static final String GAUGE_TEMPLATE_LOCATION = "/com/thoughtworks/gauge/core/specTemplate.spec";

    @Override
    public void initComponent() {
        final FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance();
        final FileTemplate template = fileTemplateManager.getTemplate(GAUGE_TEMPLATE_NAME);
        final VirtualFile file = VfsUtil.findFileByURL(getClass().getResource(GAUGE_TEMPLATE_LOCATION));

        String templateText = "";
        try {
            templateText = new String(FileUtil.adaptiveLoadText(new InputStreamReader(file.getInputStream(), file.getCharset())));
        } catch (IOException e) {
        }

        if (template == null) {
            final FileTemplate fileTemplate = fileTemplateManager.addTemplate(GAUGE_TEMPLATE_NAME, SpecFileType.INSTANCE.getDefaultExtension());
            fileTemplate.setText(templateText);
        } else if (template.getText().equals(templateText)) {
            fileTemplateManager.removeTemplate(template);
        }
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GaugeApplicationComponent";
    }
}
