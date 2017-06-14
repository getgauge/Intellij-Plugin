package com.thoughtworks.gauge.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.thoughtworks.gauge.language.psi.ConceptStaticArg;
import com.thoughtworks.gauge.language.psi.SpecStaticArg;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ConvertToDynamicArgIntention extends ConvertArgTypeIntentionBase {
    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Convert to Dynamic Parameter";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiElement element) {
        if (null == element) return false;
        if (!element.isWritable()) return false;
        return PsiTreeUtil.getParentOfType(element, SpecStaticArg.class) != null 
                || PsiTreeUtil.getParentOfType(element, ConceptStaticArg.class) != null;
    }

    @NotNull
    @Override
    protected String getReplacementString(String paramText) {
        return "<" + paramText + ">";
    }
}
