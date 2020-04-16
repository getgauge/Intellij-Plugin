/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.idea.template;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.thoughtworks.gauge.idea.icon.GaugeIcon;
import com.thoughtworks.gauge.language.ConceptFileType;
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
