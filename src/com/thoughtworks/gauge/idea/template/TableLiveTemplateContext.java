package com.thoughtworks.gauge.idea.template;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFileType;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

public class TableLiveTemplateContext extends TemplateContextType {
    protected TableLiveTemplateContext() {
        super("GAUGE", "Gauge");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile psiFile, int i) {
        return psiFile.getFileType() instanceof SpecFileType || psiFile.getFileType() instanceof ConceptFileType;
    }
}
