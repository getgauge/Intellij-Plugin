package com.thoughtworks.gauge.language.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.thoughtworks.gauge.GaugeConnection;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

public class SpecPsiImplUtil {

    public static StepValue getStepValue(SpecStep element) {
        ASTNode step = element.getNode();
        String stepText = step.getText().trim();
        int newLineIndex = stepText.indexOf("\n");
        int endIndex = newLineIndex == -1 ? stepText.length() : newLineIndex;
        SpecTable inlineTable = element.getInlineTable();
        stepText = stepText.substring(1, endIndex).trim();
        Module moduleForElement = ModuleUtil.findModuleForPsiElement(element);
        GaugeService gaugeService = Gauge.getGaugeService(moduleForElement);
        if (gaugeService == null) {
            return getDefaultStepValue(element);
        }
        GaugeConnection apiConnection = gaugeService.getGaugeConnection();
        if (apiConnection == null) {
            return getDefaultStepValue(element);
        }
        if (inlineTable != null) {
            return apiConnection.getStepValue(stepText, true);
        } else {
            return apiConnection.getStepValue(stepText);
        }

    }

    private static StepValue getDefaultStepValue(SpecStep element) {
        return new StepValue(element.getText(), element.getText(), new ArrayList<String>());
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
