package com.thoughtworks.gauge.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SpecFoldingBuilder extends GaugeFoldingBuilder {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode astNode, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        addNodes(astNode, descriptors, SpecTokenTypes.SPEC_DETAIL, SpecTokenTypes.SPEC_HEADING);
        addNodes(astNode, descriptors, SpecTokenTypes.SCENARIO, SpecTokenTypes.SCENARIO_HEADING);
        addNodes(astNode, descriptors, SpecTokenTypes.TEARDOWN, SpecTokenTypes.TEARDOWN_IDENTIFIER);
        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private void addNodes(@NotNull ASTNode astNode, List<FoldingDescriptor> descriptors, IElementType pNode, IElementType cNode) {
        for (ASTNode node : astNode.getChildren(TokenSet.create(pNode)))
            addNode(descriptors, node, node.findChildByType(cNode));
    }
}
