// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.thoughtworks.gauge.language.token.ConceptTokenTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.thoughtworks.gauge.language.psi.*;

public class ConceptArgImpl extends ASTWrapperPsiElement implements ConceptArg {

  public ConceptArgImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ConceptVisitor) ((ConceptVisitor)visitor).visitArg(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ConceptDynamicArg getDynamicArg() {
    return findChildByClass(ConceptDynamicArg.class);
  }

  @Override
  @Nullable
  public ConceptStaticArg getStaticArg() {
    return findChildByClass(ConceptStaticArg.class);
  }

}
