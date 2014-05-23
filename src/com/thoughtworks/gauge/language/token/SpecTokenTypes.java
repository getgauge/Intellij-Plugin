// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.token;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

public interface SpecTokenTypes {


  IElementType COMMENT = new SpecTokenType("Comment");
  IElementType SCENARIOHEADING = new SpecTokenType("Scenario Heading");
  IElementType SPECHEADING = new SpecTokenType("Spec Heading");
  IElementType STEP = new SpecTokenType("Step");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
