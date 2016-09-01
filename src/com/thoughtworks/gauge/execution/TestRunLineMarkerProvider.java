package com.thoughtworks.gauge.execution;

import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Function;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;


public class TestRunLineMarkerProvider extends RunLineMarkerContributor {
    private static final Function<PsiElement, String> TOOLTIP_PROVIDER = psiElement -> "Run Element";
    private ModuleHelper helper;

    public TestRunLineMarkerProvider(ModuleHelper helper) {
        this.helper = helper;
    }

    public TestRunLineMarkerProvider() {
        this.helper = new ModuleHelper();
    }

    @Nullable
    @Override
    public Info getInfo(PsiElement psiElement) {
        if (!this.helper.isGaugeModule(psiElement)) return null;
        List<IElementType> types = Arrays.asList(SpecTokenTypes.SPEC_HEADING, SpecTokenTypes.SCENARIO_HEADING);
        if (psiElement instanceof LeafPsiElement && types.contains(((LeafPsiElement) psiElement).getElementType()))
            return new Info(AllIcons.RunConfigurations.TestState.Run, TOOLTIP_PROVIDER, ExecutorAction.getActions(1));
        else return null;
    }
}
