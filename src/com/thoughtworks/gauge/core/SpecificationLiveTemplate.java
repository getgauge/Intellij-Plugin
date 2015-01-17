package com.thoughtworks.gauge.core;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.GaugeIcon;
import com.thoughtworks.gauge.language.SpecFileType;

public class SpecificationLiveTemplate implements FileTemplateGroupDescriptorFactory {
    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        
        FileTemplateGroupDescriptor descriptor = new FileTemplateGroupDescriptor("Specification", GaugeIcon.GAUGE_SPEC_FILE_ICON);
        descriptor.addTemplate(new FileTemplateDescriptor("Specification.spec", SpecFileType.INSTANCE.getIcon()));
        descriptor.addTemplate(new FileTemplateDescriptor("Concept.cpt", ConceptFileType.INSTANCE.getIcon()));
        return descriptor;
    }
}