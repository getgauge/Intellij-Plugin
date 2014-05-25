package com.thoughtworks.gauge.language.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.thoughtworks.gauge.language.psi.impl.StepElementImpl;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SpecPsiImplUtil {
    public static String getStepName(StepElement element) {
        ASTNode step = element.getNode();
        return step.getText();
    }

    public static ItemPresentation getPresentation(final StepElementImpl element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return element.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return null;
            }
        };
    }
}
