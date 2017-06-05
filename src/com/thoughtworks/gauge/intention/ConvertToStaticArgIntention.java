package com.thoughtworks.gauge.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.thoughtworks.gauge.language.psi.SpecArg;
import com.thoughtworks.gauge.language.psi.SpecDynamicArg;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ConvertToStaticArgIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Convert to Static Parameter";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }


    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        SpecArg specArg = PsiTreeUtil.getParentOfType(element, SpecArg.class);
        if (specArg == null) return;
        String text = specArg.getText();
        String paramText = StringUtils.substring(text, 1, text.length() - 1);
        String newText = "\"" + paramText + "\"";
        editor.getDocument().replaceString(specArg.getTextOffset(), specArg.getTextOffset() + specArg.getTextLength(), newText);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiElement element) {
        if (null == element) return false;
        if (!element.isWritable()) return false;
        return PsiTreeUtil.getParentOfType(element, SpecDynamicArg.class) != null;
    }
}
