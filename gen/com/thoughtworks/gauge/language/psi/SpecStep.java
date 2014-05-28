package com.thoughtworks.gauge.language.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SpecStep extends SpecNamedElement {

    @NotNull
    List<SpecArg> getArgList();

    String getStepName();

    String getName();

    PsiElement setName(String newName);

    PsiElement getNameIdentifier();

    ItemPresentation getPresentation();

    SpecTable getInlineTable();
}
