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

package com.thoughtworks.gauge.language.psi;

import com.intellij.lang.ASTNode;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.language.psi.impl.ConceptConceptImpl;

public class ConceptPsiImplUtil extends SpecPsiImplUtil {

    public static StepValue getStepValue(ConceptStep element) {
        ASTNode step = element.getNode();
        String stepText = step.getText().trim();
        int newLineIndex = stepText.indexOf("\n");
        int endIndex = newLineIndex == -1 ? stepText.length() : newLineIndex;
        ConceptTable inlineTable = element.getTable();
        int index = 0;
        if (stepText.trim().charAt(0) == '#')
            index = 1;
        stepText = stepText.substring(index, endIndex).trim();
        return getStepValueFor(element, stepText, inlineTable != null);
    }

    public static StepValue getStepValue(ConceptConceptImpl conceptConcept) {
        String conceptHeadingText = conceptConcept.getConceptHeading().getText();
        conceptHeadingText = conceptHeadingText.trim().split("\n")[0];
        String text = conceptHeadingText.trim().replaceFirst("#", "");
        return getStepValueFor(conceptConcept, text, false);
    }

}
