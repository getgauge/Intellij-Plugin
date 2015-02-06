// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.token;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.thoughtworks.gauge.language.psi.impl.*;

public interface SpecTokenTypes {

    IElementType SCENARIO = new SpecElementType("SCENARIO");
    IElementType SPEC_DETAIL = new SpecElementType("SPEC_DETAIL");
    IElementType TABLE = new SpecElementType("TABLE");
    IElementType TABLE_BODY = new SpecElementType("TABLE_BODY");
    IElementType ARG = new SpecTokenType("ARG");
    IElementType STATIC_ARG = new SpecTokenType("STATIC_ARG");
    IElementType ARG_END = new SpecTokenType("ARG_END");
    IElementType ARG_START = new SpecTokenType("ARG_START");
    IElementType COMMENT = new SpecTokenType("COMMENT");
    IElementType DYNAMIC_ARG = new SpecTokenType("DYNAMIC_ARG");
    IElementType DYNAMIC_ARG_END = new SpecTokenType("DYNAMIC_ARG_END");
    IElementType DYNAMIC_ARG_START = new SpecTokenType("DYNAMIC_ARG_START");
    IElementType NEW_LINE = new SpecTokenType("NEW_LINE");
    IElementType SCENARIO_HEADING = new SpecTokenType("SCENARIO_HEADING");
    IElementType SPEC_HEADING = new SpecTokenType("SPEC_HEADING");
    IElementType STEP = new SpecTokenType("STEP");
    IElementType TAGS = new SpecTokenType("TAGS");
    IElementType STEP_IDENTIFIER = new SpecTokenType("STEP_IDENTIFIER");
    IElementType TABLE_BORDER = new SpecTokenType("TABLE_BORDER");
    IElementType TABLE_HEADER = new SpecTokenType("TABLE_HEADER");
    IElementType TABLE_ROW_VALUE = new SpecTokenType("TABLE_ROW_VALUE");
    IElementType WHITESPACE = new SpecTokenType("WHITESPACE");

    class Factory {
        public static PsiElement createElement(ASTNode node) {
            IElementType type = node.getElementType();
            if (type == ARG) {
                return new SpecArgImpl(node);
            }else if (type == STATIC_ARG) {
                return new SpecStaticArgImpl(node);
            }else if (type == DYNAMIC_ARG) {
                return new SpecDynamicArgImpl(node);
            } else if (type == SCENARIO) {
                return new SpecScenarioImpl(node);
            } else if (type == SPEC_DETAIL) {
                return new SpecDetailImpl(node);
            } else if (type == STEP) {
                return new SpecStepImpl(node);
            } else if (type == TABLE) {
                return new SpecTableImpl(node);
            } else if (type == TAGS) {
                return new SpecTagsImpl(node);
            } else if (type == TABLE_BODY) {
                return new SpecTableBodyImpl(node);
            } else if (type == TABLE_HEADER) {
                return new SpecTableHeaderImpl(node);
            } else if (type == TABLE_ROW_VALUE) {
                return new SpecTableRowValueImpl(node);
            }
            throw new AssertionError("Unknown element type: " + type);
        }
    }
}
