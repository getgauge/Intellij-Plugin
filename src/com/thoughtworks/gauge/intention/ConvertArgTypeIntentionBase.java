package com.thoughtworks.gauge.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.thoughtworks.gauge.language.psi.ConceptArg;
import com.thoughtworks.gauge.language.psi.SpecArg;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

abstract class ConvertArgTypeIntentionBase extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiElement arg = PsiTreeUtil.getParentOfType(element, SpecArg.class);
        if (arg == null)
            arg = PsiTreeUtil.getParentOfType(element, ConceptArg.class);
        if (arg == null)
            return;
        String text = arg.getText();
        String paramText = StringUtils.substring(text, 1, text.length() - 1);
        String newText = getReplacementString(paramText);
        int startOffset = arg.getTextOffset();
        int endOffset = startOffset + arg.getTextLength();
        editor.getDocument().replaceString(startOffset, endOffset, newText);
        editor.getSelectionModel().setSelection(startOffset + 1, endOffset - 1);
    }

    @NotNull
    protected abstract String getReplacementString(String paramText);

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }
}
