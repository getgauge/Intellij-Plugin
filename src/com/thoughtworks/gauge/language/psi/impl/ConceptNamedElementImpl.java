/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.thoughtworks.gauge.language.psi.ConceptNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class ConceptNamedElementImpl extends ASTWrapperPsiElement implements ConceptNamedElement {
    public ConceptNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
