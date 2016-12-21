package com.thoughtworks.gauge.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

abstract class GaugeFoldingBuilder implements FoldingBuilder {

    protected void addNode(List<FoldingDescriptor> descriptors, ASTNode node, ASTNode heading) {
        if (heading == null) return;
        String text = node.getText().endsWith("\n") ? node.getText() : node.getText() + "\n";
        int startOffset = node.getStartOffset() + getLength(heading);
        int endOffset = node.getStartOffset() + text.lastIndexOf("\n");
        if (text.endsWith("\n\n")) endOffset--;
        if (endOffset <= startOffset) return;
        TextRange textRange = new TextRange(startOffset, endOffset);
        descriptors.add(new FoldingDescriptor(node, textRange));
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

    @NotNull
    private Integer getLength(ASTNode heading) {
        return heading.getText().length();
    }
}
