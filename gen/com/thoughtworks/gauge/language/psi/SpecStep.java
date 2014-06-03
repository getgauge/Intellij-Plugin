package com.thoughtworks.gauge.language.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.StepValue;

import java.util.List;

public interface SpecStep extends SpecNamedElement {

    List<SpecArg> getArgList();

    List<SpecStaticArg> getStaticArgList();

    StepValue getStepValue();

    String getName();

    PsiElement setName(String newName);

    PsiElement getNameIdentifier();

    ItemPresentation getPresentation();

    SpecTable getInlineTable();
}
