package com.thoughtworks.gauge.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.thoughtworks.gauge.language.token.ConceptTokenTypes.*;

%%

%{
  public _ConceptLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _ConceptLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%caseless
%state INSTEP,INARG,INDYNAMIC,INTABLEHEADER,INTABLEBODY,INTABLEBODYROW,INDYNAMICTABLEITEM

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = [ \t\f]
TableIdentifier = [|]
StepIdentifier = [*]
NonWhiteSpaceAndIdentifierCharacter = [^ \r\n\t\f#*|]
ConceptHeading = {WhiteSpace}* "#" {InputCharacter}* {LineTerminator}? | {WhiteSpace}* {InputCharacter}* {LineTerminator} [=]+ {LineTerminator}?

%%
<YYINITIAL> {
  {ConceptHeading}              {return CONCEPT_HEADING;}
  {StepIdentifier}              {yybegin(INSTEP);return STEP_IDENTIFIER;}
  {TableIdentifier}             {yybegin(INTABLEHEADER);return TABLE_BORDER;}
  {LineTerminator}? {WhiteSpace}* {NonWhiteSpaceAndIdentifierCharacter}+ {WhiteSpace}* ({StepIdentifier} | [##] | {TableIdentifier}) {InputCharacter}* {LineTerminator}? {return COMMENT;}
  [^]                           {return COMMENT;}
}

<INSTEP> {
  [^<\"\r\n]*                   {yybegin(INSTEP); return STEP;}
  [\"]                          {yybegin(INARG); return ARG_START; }
  [<]                           {yybegin(INDYNAMIC); return DYNAMIC_ARG_START;}
  {LineTerminator}?             {yybegin(YYINITIAL); return STEP;}
}

<INARG> {
  (\\\"|[^\"])*                 {return ARG; }
  [\"]                          {yybegin(INSTEP); return ARG_END;}
}

<INDYNAMIC> {
  (\\<|\\>|[^\>])*              {return DYNAMIC_ARG; }
  [>]                           {yybegin(INSTEP); return DYNAMIC_ARG_END;}
}

<INDYNAMICTABLEITEM> {
  (\\<|\\>|[^\>])*              {return DYNAMIC_ARG; }
  [>]                           {yybegin(INTABLEBODYROW); return DYNAMIC_ARG_END;}
}

<INTABLEHEADER> {
    (\\\||[^|\r\n])*                        {yybegin(INTABLEHEADER); return TABLE_HEADER;}
    {TableIdentifier}                       {yybegin(INTABLEHEADER); return TABLE_BORDER;}
    {LineTerminator}{WhiteSpace}*           {yybegin(INTABLEBODY);return NEW_LINE;}
}
<INTABLEBODY> {
    {TableIdentifier}                       {yybegin(INTABLEBODYROW); return TABLE_BORDER;}
    [^]                                     {yypushback(1); yybegin(YYINITIAL);}
}


<INTABLEBODYROW> {
    {WhiteSpace}*                           {yybegin(INTABLEBODYROW); return WHITESPACE;}
    (\\\||[^-<|\r\n])*                      {yybegin(INTABLEBODYROW); return TABLE_ROW_VALUE;}
    [-]*                                    {yybegin(INTABLEBODYROW); return TABLE_BORDER;}
    [<]                                     {yybegin(INDYNAMICTABLEITEM); return DYNAMIC_ARG_START;}
    {TableIdentifier}                       {yybegin(INTABLEBODYROW); return TABLE_BORDER;}
    {LineTerminator}{WhiteSpace}*           {yybegin(INTABLEBODY); return NEW_LINE;}
}
