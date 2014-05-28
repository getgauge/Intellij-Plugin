import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.thoughtworks.gauge.language.token.SpecTokenTypes.*;

%%

%{
  public _SpecLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _SpecLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%state INTABLE,INSTEP,INARG,INDYNAMIC,INTABLEHEADER,INTABLEBODY,INTABLEBODYROW

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
TableInputCharacter = [^|\r\n]
WhiteSpace = [ \t\f]
TableIdentifier = [|]
ScenarioHeading = {WhiteSpace}* "##" {InputCharacter}* {LineTerminator}? | {WhiteSpace}* {InputCharacter}* {LineTerminator} [-]+ {LineTerminator}?
SpecHeading = {WhiteSpace}* "#" {InputCharacter}* {LineTerminator}? | {WhiteSpace}* {InputCharacter}* {LineTerminator} [=]+ {LineTerminator}?
Step = {WhiteSpace}* "*" [^*] {InputCharacter}* {LineTerminator}*
TableHeader = {WhiteSpace}* ("|" {TableInputCharacter}*)+ "|" {LineTerminator} | {WhiteSpace}* ("|" {TableInputCharacter}*)+ "|" {LineTerminator} {WhiteSpace}* {TableSeparator}+ {LineTerminator}+
TableRow={WhiteSpace}* ("|" {TableInputCharacter}*)+ "|" {LineTerminator}
Comment = {InputCharacter}*? {LineTerminator}*?
TableSeparator = [-|]
%%
<YYINITIAL> {
  {ScenarioHeading}             {return SCENARIO_HEADING;}
  {SpecHeading}                 {return SPEC_HEADING;}
  "*"                           {yybegin(INSTEP);return STEP_IDENTIFIER;}
  "*" {WhiteSpace}* "*"         {return COMMENT;}
  {TableIdentifier}             {yybegin(INTABLEHEADER);return TABLE_BORDER;}
  [^]                           {return COMMENT;}
}

<INSTEP> {
  [^*<\"\r\n]*                  {yybegin(INSTEP); return STEP;}
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
    (\\\||[^-|\r\n])*                       {yybegin(INTABLEBODYROW); return TABLE_ROW;}
    [-]*                                    {yybegin(INTABLEBODYROW); return TABLE_BORDER;}
    {TableIdentifier}                       {yybegin(INTABLEBODYROW); return TABLE_BORDER;}
    {LineTerminator}{WhiteSpace}*           {yybegin(INTABLEBODY); return NEW_LINE;}
}