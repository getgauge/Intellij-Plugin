package com.thoughtworks.gauge.language.token;

import com.intellij.psi.tree.IElementType;
import com.thoughtworks.gauge.language.Concept;
import com.thoughtworks.gauge.language.Specification;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ConceptTokenType extends IElementType {
    public ConceptTokenType(@NotNull @NonNls String debugName) {
        super(debugName, Concept.INSTANCE);
    }

    @Override
    public String toString() {
        return "Concept." + super.toString();
    }
}