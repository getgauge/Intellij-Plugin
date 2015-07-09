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

package com.thoughtworks.gauge.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.thoughtworks.gauge.language.psi.ConceptStep;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.thoughtworks.gauge.util.GaugeUtil.moduleForPsiElement;

public class ConceptReference extends PsiReferenceBase<ConceptStep> {


    public ConceptReference(@NotNull ConceptStep element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        SpecStepImpl step = new SpecStepImpl(this.myElement.getNode());
        step.setConcept(true);
        return StepUtil.findStepImpl(step, moduleForPsiElement(this.myElement));
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public TextRange getRangeInElement() {
        return new TextRange(0, myElement.getTextLength());
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            return StepUtil.isMatch(method, this.myElement.getStepValue().getStepText(), moduleForPsiElement(element));
        } else {
            return false;
        }
    }

}
