package com.thoughtworks.gauge.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

abstract class GaugeFoldingBuilder implements FoldingBuilder {

    @NotNull
    protected Integer getNewLineIndex(ASTNode heading) {
        Integer newLineIndex = heading.getText().indexOf('\n');
        return newLineIndex == -1 ? heading.getTextLength() : newLineIndex;
    }

    protected void addNode(List<FoldingDescriptor> descriptors, ASTNode node, ASTNode heading) {
        if (heading == null) return;
        String text = node.getText().endsWith("\n") ? node.getText() : node.getText() + "\n";
        int startOffset = node.getStartOffset() + getNewLineIndex(heading);
        int endOffset = node.getStartOffset() + text.lastIndexOf("\n");
        TextRange textRange = new TextRange(startOffset, endOffset);
        if (textRange.getLength() < 1) return;
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
}
