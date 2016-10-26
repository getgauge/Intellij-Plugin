package com.thoughtworks.gauge.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.tree.TokenSet;
import com.thoughtworks.gauge.language.token.ConceptTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConceptFoldingBuilder extends GaugeFoldingBuilder {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode astNode, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        for (ASTNode node : astNode.getChildren(TokenSet.create(ConceptTokenTypes.CONCEPT)))
            if (astNode.getChildren(TokenSet.create(ConceptTokenTypes.STEP, ConceptTokenTypes.COMMENT)).length > 0)
                addNode(descriptors, node, node.findChildByType(ConceptTokenTypes.CONCEPT_HEADING));
        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    @Override
    protected int getStart(ASTNode n, ASTNode h) {
        int start = n.getStartOffset() + h.getTextLength();
        return !h.getText().endsWith("\n") ? ++start : start;
    }
}
