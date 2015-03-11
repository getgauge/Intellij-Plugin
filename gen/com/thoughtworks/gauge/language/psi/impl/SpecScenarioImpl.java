// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.thoughtworks.gauge.language.token.SpecTokenTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.thoughtworks.gauge.language.psi.*;

public class SpecScenarioImpl extends ASTWrapperPsiElement implements SpecScenario {

    public SpecScenarioImpl(ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof SpecVisitor) ((SpecVisitor) visitor).visitScenario(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public List<SpecStep> getStepList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, SpecStep.class);
  }

  @Override
  @Nullable
  public SpecTags getTags() {
    return findChildByClass(SpecTags.class);
    }

}
