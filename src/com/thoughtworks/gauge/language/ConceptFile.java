/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;


public class ConceptFile extends PsiFileBase {
    public ConceptFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, Concept.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ConceptFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Concept File";
    }

}
