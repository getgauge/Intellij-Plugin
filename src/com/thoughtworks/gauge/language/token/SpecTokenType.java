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