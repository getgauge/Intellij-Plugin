// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class ConceptVisitor extends PsiElementVisitor {

  public void visitArg(@NotNull ConceptArg o) {
    visitPsiElement(o);
  }

  public void visitConcept(@NotNull ConceptConcept o) {
    visitPsiElement(o);
  }

  public void visitConceptHeading(@NotNull ConceptConceptHeading o) {
    visitPsiElement(o);
  }

  public void visitDynamicArg(@NotNull ConceptDynamicArg o) {
    visitPsiElement(o);
  }

  public void visitStaticArg(@NotNull ConceptStaticArg o) {
    visitPsiElement(o);
  }

  public void visitStep(@NotNull ConceptStep o) {
    visitNamedElement(o);
  }

  public void visitTable(@NotNull ConceptTable o) {
    visitPsiElement(o);
  }

  public void visitTableBody(@NotNull ConceptTableBody o) {
    visitPsiElement(o);
  }

  public void visitTableHeader(@NotNull ConceptTableHeader o) {
    visitPsiElement(o);
  }

  public void visitTableRowValue(@NotNull ConceptTableRowValue o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull ConceptNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
