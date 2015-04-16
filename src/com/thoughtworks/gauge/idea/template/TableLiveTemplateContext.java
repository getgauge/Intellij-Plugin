package com.thoughtworks.gauge.idea.template;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

public class TableLiveTemplateContext extends TemplateContextType {
    protected TableLiveTemplateContext(String id, String presentableName) {
        super(id, presentableName);
    }

    @Override
    public boolean isInContext(@NotNull PsiFile psiFile, int i) {
        return GaugeUtil.isGaugeFile(psiFile.getVirtualFile());
    }
}
