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

public interface ConceptTokenTypes {

  IElementType CONCEPT = new ConceptElementType("CONCEPT");
  IElementType STATIC_ARG = new ConceptElementType("STATIC_ARG");
  IElementType TABLE = new ConceptElementType("TABLE");
  IElementType TABLE_BODY = new ConceptElementType("TABLE_BODY");

  IElementType ARG = new ConceptTokenType("ARG");
  IElementType ARG_END = new ConceptTokenType("ARG_END");
  IElementType ARG_START = new ConceptTokenType("ARG_START");
  IElementType COMMENT = new ConceptTokenType("COMMENT");
  IElementType CONCEPT_HEADING = new ConceptTokenType("CONCEPT_HEADING");
  IElementType CONCEPT_HEADING_IDENTIFIER = new ConceptTokenType("CONCEPT_HEADING_IDENTIFIER");
  IElementType DYNAMIC_ARG = new ConceptTokenType("DYNAMIC_ARG");
  IElementType DYNAMIC_ARG_END = new ConceptTokenType("DYNAMIC_ARG_END");
  IElementType DYNAMIC_ARG_START = new ConceptTokenType("DYNAMIC_ARG_START");
  IElementType NEW_LINE = new ConceptTokenType("NEW_LINE");
  IElementType STEP = new ConceptTokenType("STEP");
  IElementType STEP_IDENTIFIER = new ConceptTokenType("STEP_IDENTIFIER");
  IElementType TABLE_BORDER = new ConceptTokenType("TABLE_BORDER");
  IElementType TABLE_HEADER = new ConceptTokenType("TABLE_HEADER");
  IElementType TABLE_ROW_VALUE = new ConceptTokenType("TABLE_ROW_VALUE");
  IElementType WHITESPACE = new ConceptTokenType("WHITESPACE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ARG) {
        return new ConceptArgImpl(node);
      }
      else if (type == CONCEPT) {
        return new ConceptConceptImpl(node);
      }
      else if (type == CONCEPT_HEADING) {
        return new ConceptConceptHeadingImpl(node);
      }
      else if (type == DYNAMIC_ARG) {
        return new ConceptDynamicArgImpl(node);
      }
      else if (type == STATIC_ARG) {
        return new ConceptStaticArgImpl(node);
      }
      else if (type == STEP) {
        return new ConceptStepImpl(node);
      }
      else if (type == TABLE) {
        return new ConceptTableImpl(node);
      }
      else if (type == TABLE_BODY) {
        return new ConceptTableBodyImpl(node);
      }
      else if (type == TABLE_HEADER) {
        return new ConceptTableHeaderImpl(node);
      }
      else if (type == TABLE_ROW_VALUE) {
        return new ConceptTableRowValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
