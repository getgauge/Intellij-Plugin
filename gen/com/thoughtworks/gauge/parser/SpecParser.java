// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static com.thoughtworks.gauge.language.token.SpecTokenTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class SpecParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("com.thoughtworks.gauge.parser.SpecParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == ARG) {
      result_ = arg(builder_, 0);
    }
    else if (root_ == DYNAMIC_ARG) {
      result_ = dynamicArg(builder_, 0);
    }
    else if (root_ == SCENARIO) {
      result_ = scenario(builder_, 0);
    }
    else if (root_ == SPEC_DETAIL) {
      result_ = specDetail(builder_, 0);
    }
    else if (root_ == STATIC_ARG) {
      result_ = staticArg(builder_, 0);
    }
    else if (root_ == STEP) {
      result_ = step(builder_, 0);
    }
    else if (root_ == TABLE) {
      result_ = table(builder_, 0);
    }
    else if (root_ == TABLE_BODY) {
      result_ = tableBody(builder_, 0);
    }
    else if (root_ == TABLE_HEADER) {
      result_ = tableHeader(builder_, 0);
    }
    else if (root_ == TABLE_ROW_VALUE) {
      result_ = tableRowValue(builder_, 0);
    }
    else if (root_ == TAGS) {
      result_ = tags(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return specFile(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // dynamicArg | staticArg
  public static boolean arg(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg")) return false;
    if (!nextTokenIs(builder_, "<arg>", ARG_START, DYNAMIC_ARG_START)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<arg>");
    result_ = dynamicArg(builder_, level_ + 1);
    if (!result_) result_ = staticArg(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, ARG, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // COMMENT
  static boolean comment(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, COMMENT);
  }

  /* ********************************************************** */
  // DYNAMIC_ARG_START DYNAMIC_ARG DYNAMIC_ARG_END
  public static boolean dynamicArg(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dynamicArg")) return false;
    if (!nextTokenIs(builder_, DYNAMIC_ARG_START)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, DYNAMIC_ARG_START, DYNAMIC_ARG, DYNAMIC_ARG_END);
    exit_section_(builder_, marker_, DYNAMIC_ARG, result_);
    return result_;
  }

  /* ********************************************************** */
  // scenarioHeading tags? (step | comment)*
  public static boolean scenario(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scenario")) return false;
    if (!nextTokenIs(builder_, SCENARIO_HEADING)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = scenarioHeading(builder_, level_ + 1);
    result_ = result_ && scenario_1(builder_, level_ + 1);
    result_ = result_ && scenario_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, SCENARIO, result_);
    return result_;
  }

  // tags?
  private static boolean scenario_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scenario_1")) return false;
    tags(builder_, level_ + 1);
    return true;
  }

  // (step | comment)*
  private static boolean scenario_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scenario_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!scenario_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "scenario_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // step | comment
  private static boolean scenario_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scenario_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = step(builder_, level_ + 1);
    if (!result_) result_ = comment(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // SCENARIO_HEADING
  static boolean scenarioHeading(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, SCENARIO_HEADING);
  }

  /* ********************************************************** */
  // (comment)*  specHeading tags? table? (step|comment)*
  public static boolean specDetail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specDetail")) return false;
    if (!nextTokenIs(builder_, "<spec detail>", COMMENT, SPEC_HEADING)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<spec detail>");
    result_ = specDetail_0(builder_, level_ + 1);
    result_ = result_ && specHeading(builder_, level_ + 1);
    result_ = result_ && specDetail_2(builder_, level_ + 1);
    result_ = result_ && specDetail_3(builder_, level_ + 1);
    result_ = result_ && specDetail_4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, SPEC_DETAIL, result_, false, null);
    return result_;
  }

  // (comment)*
  private static boolean specDetail_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specDetail_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!specDetail_0_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "specDetail_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // (comment)
  private static boolean specDetail_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specDetail_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = comment(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // tags?
  private static boolean specDetail_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specDetail_2")) return false;
    tags(builder_, level_ + 1);
    return true;
  }

  // table?
  private static boolean specDetail_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specDetail_3")) return false;
    table(builder_, level_ + 1);
    return true;
  }

  // (step|comment)*
  private static boolean specDetail_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specDetail_4")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!specDetail_4_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "specDetail_4", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // step|comment
  private static boolean specDetail_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specDetail_4_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = step(builder_, level_ + 1);
    if (!result_) result_ = comment(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // specDetail scenario+
  static boolean specFile(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specFile")) return false;
    if (!nextTokenIs(builder_, "", COMMENT, SPEC_HEADING)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = specDetail(builder_, level_ + 1);
    result_ = result_ && specFile_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // scenario+
  private static boolean specFile_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specFile_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = scenario(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!scenario(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "specFile_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // SPEC_HEADING
  static boolean specHeading(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, SPEC_HEADING);
  }

  /* ********************************************************** */
  // ARG_START ARG? ARG_END
  public static boolean staticArg(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "staticArg")) return false;
    if (!nextTokenIs(builder_, ARG_START)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ARG_START);
    result_ = result_ && staticArg_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ARG_END);
    exit_section_(builder_, marker_, STATIC_ARG, result_);
    return result_;
  }

  // ARG?
  private static boolean staticArg_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "staticArg_1")) return false;
    consumeToken(builder_, ARG);
    return true;
  }

  /* ********************************************************** */
  // STEP_IDENTIFIER (arg|STEP)+ table?
  public static boolean step(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "step")) return false;
    if (!nextTokenIs(builder_, STEP_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, STEP_IDENTIFIER);
    result_ = result_ && step_1(builder_, level_ + 1);
    result_ = result_ && step_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, STEP, result_);
    return result_;
  }

  // (arg|STEP)+
  private static boolean step_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "step_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = step_1_0(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!step_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "step_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // arg|STEP
  private static boolean step_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "step_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = arg(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, STEP);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // table?
  private static boolean step_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "step_2")) return false;
    table(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // tableHeader tableBody
  public static boolean table(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "table")) return false;
    if (!nextTokenIs(builder_, TABLE_BORDER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = tableHeader(builder_, level_ + 1);
    result_ = result_ && tableBody(builder_, level_ + 1);
    exit_section_(builder_, marker_, TABLE, result_);
    return result_;
  }

  /* ********************************************************** */
  // (TABLE_BORDER (WHITESPACE* tableRowValue? WHITESPACE* TABLE_BORDER)+ NEW_LINE)*
  public static boolean tableBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableBody")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<table body>");
    int pos_ = current_position_(builder_);
    while (true) {
      if (!tableBody_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "tableBody", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, level_, marker_, TABLE_BODY, true, false, null);
    return true;
  }

  // TABLE_BORDER (WHITESPACE* tableRowValue? WHITESPACE* TABLE_BORDER)+ NEW_LINE
  private static boolean tableBody_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableBody_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TABLE_BORDER);
    result_ = result_ && tableBody_0_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, NEW_LINE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (WHITESPACE* tableRowValue? WHITESPACE* TABLE_BORDER)+
  private static boolean tableBody_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableBody_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = tableBody_0_1_0(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!tableBody_0_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "tableBody_0_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // WHITESPACE* tableRowValue? WHITESPACE* TABLE_BORDER
  private static boolean tableBody_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableBody_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = tableBody_0_1_0_0(builder_, level_ + 1);
    result_ = result_ && tableBody_0_1_0_1(builder_, level_ + 1);
    result_ = result_ && tableBody_0_1_0_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TABLE_BORDER);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // WHITESPACE*
  private static boolean tableBody_0_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableBody_0_1_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, WHITESPACE)) break;
      if (!empty_element_parsed_guard_(builder_, "tableBody_0_1_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // tableRowValue?
  private static boolean tableBody_0_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableBody_0_1_0_1")) return false;
    tableRowValue(builder_, level_ + 1);
    return true;
  }

  // WHITESPACE*
  private static boolean tableBody_0_1_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableBody_0_1_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, WHITESPACE)) break;
      if (!empty_element_parsed_guard_(builder_, "tableBody_0_1_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TABLE_BORDER (TABLE_HEADER TABLE_BORDER)+ NEW_LINE ((TABLE_BORDER)* NEW_LINE)?
  public static boolean tableHeader(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableHeader")) return false;
    if (!nextTokenIs(builder_, TABLE_BORDER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TABLE_BORDER);
    result_ = result_ && tableHeader_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, NEW_LINE);
    result_ = result_ && tableHeader_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, TABLE_HEADER, result_);
    return result_;
  }

  // (TABLE_HEADER TABLE_BORDER)+
  private static boolean tableHeader_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableHeader_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = tableHeader_1_0(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!tableHeader_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "tableHeader_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // TABLE_HEADER TABLE_BORDER
  private static boolean tableHeader_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableHeader_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, TABLE_HEADER, TABLE_BORDER);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ((TABLE_BORDER)* NEW_LINE)?
  private static boolean tableHeader_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableHeader_3")) return false;
    tableHeader_3_0(builder_, level_ + 1);
    return true;
  }

  // (TABLE_BORDER)* NEW_LINE
  private static boolean tableHeader_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableHeader_3_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = tableHeader_3_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, NEW_LINE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (TABLE_BORDER)*
  private static boolean tableHeader_3_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableHeader_3_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, TABLE_BORDER)) break;
      if (!empty_element_parsed_guard_(builder_, "tableHeader_3_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // TABLE_ROW_VALUE | DYNAMIC_ARG_START DYNAMIC_ARG DYNAMIC_ARG_END
  public static boolean tableRowValue(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tableRowValue")) return false;
    if (!nextTokenIs(builder_, "<table row value>", DYNAMIC_ARG_START, TABLE_ROW_VALUE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<table row value>");
    result_ = consumeToken(builder_, TABLE_ROW_VALUE);
    if (!result_) result_ = parseTokens(builder_, 0, DYNAMIC_ARG_START, DYNAMIC_ARG, DYNAMIC_ARG_END);
    exit_section_(builder_, level_, marker_, TABLE_ROW_VALUE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // TAGS
  public static boolean tags(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tags")) return false;
    if (!nextTokenIs(builder_, TAGS)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TAGS);
    exit_section_(builder_, marker_, TAGS, result_);
    return result_;
  }

}
