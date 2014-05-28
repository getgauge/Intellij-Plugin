// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.thoughtworks.gauge.language.psi.SpecTableHeader;
import com.thoughtworks.gauge.language.psi.SpecVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SpecTableHeaderImpl extends ASTWrapperPsiElement implements SpecTableHeader {

    public SpecTableHeaderImpl(ASTNode node) {
        super(node);
    }

    public List<String> getHeaders() {
        SpecTableHeader[] headers = findChildrenByClass(SpecTableHeader.class);
        List<String> headerValue = new ArrayList<String>();
        for (int i = 0; i < headers.length; i++) {
            headerValue.add(headers[i].getText());

        }
        return headerValue;
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof SpecVisitor) ((SpecVisitor) visitor).visitTableHeader(this);
        else super.accept(visitor);
    }

}
