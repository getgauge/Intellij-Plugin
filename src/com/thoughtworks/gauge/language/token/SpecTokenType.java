/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language.token;

import com.intellij.psi.tree.IElementType;
import com.thoughtworks.gauge.language.Specification;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SpecTokenType extends IElementType {
    public SpecTokenType(@NotNull @NonNls String debugName) {
        super(debugName, Specification.INSTANCE);
    }

    @Override
    public String toString() {
        return "Specification." + super.toString();
    }
}
