/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.annotator;

import com.intellij.ide.actions.CreateClassAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import java.util.Map;

public class GaugeCreateClassAction extends CreateClassAction {
    private PsiClass createdElement;

    @Override
    protected void postProcess(PsiClass createdElement, String templateName, Map<String, String> customProperties) {
        super.postProcess(createdElement, templateName, customProperties);
        this.createdElement = createdElement;
    }


    public VirtualFile getCreatedFile() {
        if (createdElement == null) {
            return null;
        }
        return createdElement.getContainingFile().getVirtualFile();
    }

}
