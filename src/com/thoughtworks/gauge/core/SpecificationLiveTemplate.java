package com.thoughtworks.gauge.core;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.thoughtworks.gauge.language.GaugeIcon;

public class SpecificationLiveTemplate implements FileTemplateGroupDescriptorFactory {
    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        FileTemplateGroupDescriptor fileTemplateGroupDescriptor = new FileTemplateGroupDescriptor("Specification", GaugeIcon.GAUGE_SPEC_FILE_ICON);
        fileTemplateGroupDescriptor.addTemplate(new FileTemplateDescriptor("/com/thoughtworks/gauge/core/specTemplate.spec", GaugeIcon.GAUGE_SPEC_FILE_ICON));
        return fileTemplateGroupDescriptor;
    }
}
