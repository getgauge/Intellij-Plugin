package com.thoughtworks.gauge.language.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;

import java.util.List;

public interface SpecStep extends SpecNamedElement {

    List<SpecArg> getArgList();

    List<SpecStaticArg> getStaticArgList();

    String getStepName();

    String getName();

    PsiElement setName(String newName);

    PsiElement getNameIdentifier();

    ItemPresentation getPresentation();

    SpecTable getInlineTable();
}
