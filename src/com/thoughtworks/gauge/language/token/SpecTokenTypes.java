// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.token;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

public interface SpecTokenTypes {


  IElementType COMMENT = new SpecTokenType("Comment");
  IElementType SCENARIO_HEADING = new SpecTokenType("Scenario Heading");
  IElementType SPEC_HEADING = new SpecTokenType("Spec Heading");
  IElementType STEP = new SpecTokenType("Step");
  IElementType TABLE_HEADER = new SpecTokenType("Table Header");
  IElementType TABLE_ROW = new SpecTokenType("Table Row");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
