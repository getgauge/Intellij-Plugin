package com.thoughtworks.gauge.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.thoughtworks.gauge.language.psi.SpecScenario;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SpecInspectionProvider extends GaugeInspectionProvider {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (isOnTheFly) return new ProblemDescriptor[0];
        File dir = new File(file.getContainingDirectory().getVirtualFile().getPath());
        return getDescriptors(GaugeInspectionHelper.getErrors(file.getName(), dir), manager, file);
    }

    PsiElement getElement(PsiElement element) {
        if (element == null) return null;
        if (element instanceof SpecScenario || element instanceof SpecStep) return element;
        List<IElementType> types = Arrays.asList(SpecTokenTypes.SPEC_HEADING, SpecTokenTypes.SCENARIO_HEADING);
        if (element instanceof LeafPsiElement && types.contains(((LeafPsiElement) element).getElementType()))
            return element;
        return getElement(element.getParent());
    }
}
