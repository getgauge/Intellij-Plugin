package com.thoughtworks.gauge.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.thoughtworks.gauge.language.psi.ConceptStep;
import com.thoughtworks.gauge.language.token.ConceptTokenTypes;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ConceptInspectionProvider extends GaugeInspectionProvider {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (isOnTheFly) return new ProblemDescriptor[0];
        File dir = GaugeUtil.moduleDir(GaugeUtil.moduleForPsiElement(file));
        if (dir == null) return new ProblemDescriptor[0];
        return getDescriptors(GaugeInspectionHelper.getErrors(dir.getAbsolutePath(), dir), manager, file);
    }

    PsiElement getElement(PsiElement element) {
        if (element == null) return null;
        if (element instanceof ConceptStep) return element;
        if (element instanceof LeafPsiElement && ((LeafPsiElement) element).getElementType().equals(ConceptTokenTypes.CONCEPT_HEADING))
            return element;
        return getElement(element.getParent());
    }
}
