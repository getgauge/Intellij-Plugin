// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.token;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.thoughtworks.gauge.language.psi.impl.SpecArgImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecScenarioImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecTableImpl;

public interface SpecTokenTypes {

    IElementType COMMENT = new SpecTokenType("Comment");
    IElementType SCENARIO = new SpecTokenType("Scenario");
    IElementType SCENARIO_HEADING = new SpecTokenType("Scenario Heading");
    IElementType SPEC_HEADING = new SpecTokenType("Spec Heading");
    IElementType STEP_IDENTIFIER = new SpecTokenType("Step Identifier");
    IElementType STEP = new SpecTokenType("Step");
    IElementType TABLE = new SpecTokenType("Table");
    IElementType TABLE_HEADER = new SpecTokenType("Table Header");
    IElementType TABLE_ROW = new SpecTokenType("Table Row");
    IElementType ARG_START = new SpecTokenType("Step arg start");
    IElementType ARG_END = new SpecTokenType("Step arg end");
    IElementType ARG = new SpecTokenType("Step arg");
    IElementType DYNAMIC_ARG_START = new SpecTokenType("Step dynamic arg start");
    IElementType DYNAMIC_ARG_END = new SpecTokenType("Step dynamic arg end");
    IElementType DYNAMIC_ARG = new SpecTokenType("Step dynamic arg");

    class Factory {
        public static PsiElement createElement(ASTNode node) {
            IElementType type = node.getElementType();
            if (type == STEP) {
                return new SpecStepImpl(node);
            } else if (type == SCENARIO) {
                return new SpecScenarioImpl(node);
            } else if (type == TABLE) {
                return new SpecTableImpl(node);
            } else if (type == ARG) {
                return new SpecArgImpl(node);
            }
            throw new AssertionError("Unknown element type: " + type);
        }
    }
}
