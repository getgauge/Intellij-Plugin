package com.thoughtworks.gauge.language.token;

import com.intellij.psi.tree.IElementType;
import com.thoughtworks.gauge.language.Specification;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SpecElementType extends IElementType {
    public SpecElementType(@NotNull @NonNls String debugName) {
        super(debugName, Specification.INSTANCE);
    }
}