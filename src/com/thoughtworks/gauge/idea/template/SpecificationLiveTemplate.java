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