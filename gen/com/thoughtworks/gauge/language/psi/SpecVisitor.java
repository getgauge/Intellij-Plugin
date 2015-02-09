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

import com.thoughtworks.gauge.language.psi.impl.SpecDetailImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecTagsImpl;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class SpecVisitor extends PsiElementVisitor {

    public void visitArg(@NotNull SpecArg o) {
        visitPsiElement(o);
    }

    public void visitScenario(@NotNull SpecScenario o) {
        visitPsiElement(o);
    }

    public void visitStep(@NotNull SpecStep o) {
        visitNamedElement(o);
    }

    public void visitTable(@NotNull SpecTable o) {
        visitPsiElement(o);
    }

    public void visitTableBody(@NotNull SpecTableBody o) {
        visitPsiElement(o);
    }

    public void visitTableHeader(@NotNull SpecTableHeader o) {
        visitPsiElement(o);
    }

    public void visitNamedElement(@NotNull SpecNamedElement o) {
        visitPsiElement(o);
    }

    public void visitPsiElement(@NotNull PsiElement o) {
        visitElement(o);
    }

    public void visitSpecDetail(SpecDetailImpl o) {
        visitPsiElement(o);
    }

    public void visitTags(SpecTagsImpl o) {
        visitPsiElement(o);
    }
}
