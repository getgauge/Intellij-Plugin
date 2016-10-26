package com.thoughtworks.gauge.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

abstract class GaugeFoldingBuilder implements FoldingBuilder {

    protected void addNode(List<FoldingDescriptor> d, ASTNode n, ASTNode h) {
        if (h == null) return;
        int start = getStart(n, h);
        int end = n.getStartOffset() + n.getText().lastIndexOf("\n");
        if (!n.getText().endsWith("\n")) end = n.getStartOffset() + n.getTextLength();
        TextRange t = new TextRange(start, end);
        if (t.getLength() < 1) return;
        d.add(new FoldingDescriptor(n, t));
    }

    protected int getStart(ASTNode n, ASTNode h) {
        return n.getStartOffset() + h.getTextLength();
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return " ...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return false;
    }
}
