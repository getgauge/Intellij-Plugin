package com.thoughtworks.gauge.language.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.thoughtworks.gauge.StepValueExtractor;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SpecPsiImplUtil {

    private static StepValueExtractor stepValueExtractor = new StepValueExtractor();

    public static String getStepName(SpecStep element) {
        ASTNode step = element.getNode();
        String stepText = step.getText().trim();
        int newLineIndex = stepText.indexOf("\n");
        int endIndex = newLineIndex == -1 ? stepText.length() : newLineIndex;
        SpecTable inlineTable = element.getInlineTable();
        stepText = stepText.substring(1, endIndex).trim();
        if (inlineTable != null) {
            return stepValueExtractor.getValueWithTable(stepText).getValue();
        } else {
            return stepValueExtractor.getValue(stepText).getValue();
        }

    }

    public static ItemPresentation getPresentation(final SpecStepImpl element) {
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
