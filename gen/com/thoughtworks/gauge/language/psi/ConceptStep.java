// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.StepValue;

public interface ConceptStep extends ConceptNamedElement {

  @NotNull
  List<ConceptArg> getArgList();

  @Nullable
  ConceptTable getTable();

  StepValue getStepValue();

}
