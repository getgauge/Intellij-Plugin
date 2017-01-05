package com.thoughtworks.gauge.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

abstract class GaugeInspectionProvider extends LocalInspectionTool {

    ProblemDescriptor[] getDescriptors(List<GaugeError> errors, InspectionManager manager, PsiFile file) {
        List<ProblemDescriptor> descriptors = new ArrayList<>();
        for (GaugeError e : errors) {
            if (!e.isFrom(file.getVirtualFile().getPath())) continue;
            PsiElement element = getElement(file.findElementAt(e.getOffset(file.getText())));
            if (element == null) continue;
            descriptors.add(manager.createProblemDescriptor(element, e.getMessage(), null, ProblemHighlightType.ERROR, false, false));
        }
        return descriptors.toArray(new ProblemDescriptor[0]);
    }

    abstract PsiElement getElement(PsiElement element);
}

