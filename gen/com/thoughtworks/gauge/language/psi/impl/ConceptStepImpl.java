// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.psi.impl;

import java.util.List;

import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.thoughtworks.gauge.reference.ConceptReference;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.thoughtworks.gauge.language.token.ConceptTokenTypes.*;
import com.thoughtworks.gauge.language.psi.*;
import com.thoughtworks.gauge.StepValue;

public class ConceptStepImpl extends ConceptNamedElementImpl implements ConceptStep {

  public ConceptStepImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ConceptVisitor) ((ConceptVisitor)visitor).visitStep(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ConceptArg> getArgList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ConceptArg.class);
  }

  @Override
  @Nullable
  public ConceptTable getTable() {
    return findChildByClass(ConceptTable.class);
  }

  public StepValue getStepValue() {
    return ConceptPsiImplUtil.getStepValue(this);
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return null;
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException {
    return null;
  }

  @Override
  public PsiReference getReference() {
    return new ConceptReference(this);
  }
}
