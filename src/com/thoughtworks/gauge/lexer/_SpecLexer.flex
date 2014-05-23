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
%state INTABLE

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
TableInputCharacter = [^|\r\n]
WhiteSpace = [ \t\f]
ScenarioHeading = {WhiteSpace}* "##" {InputCharacter}* {LineTerminator}+ | {WhiteSpace}* {InputCharacter}* {LineTerminator} [-]+ {LineTerminator}+
SpecHeading = {WhiteSpace}* "#" {InputCharacter}* {LineTerminator}+ | {WhiteSpace}* {InputCharacter}* {LineTerminator} [=]+ {LineTerminator}+
Step = {WhiteSpace}* "*" {InputCharacter}* {LineTerminator}*
TableHeader = {WhiteSpace}* ("|" {TableInputCharacter}*)+ "|" {LineTerminator} | {WhiteSpace}* ("|" {TableInputCharacter}*)+ "|" {LineTerminator} {WhiteSpace}* {TableSeparator}+ {LineTerminator}+
TableRow={WhiteSpace}* ("|" {TableInputCharacter}*)+ "|" {LineTerminator}
Comment = {InputCharacter}*? {LineTerminator}*?
TableSeparator = [-|]
%%
<YYINITIAL> {
  {ScenarioHeading} {return SCENARIO_HEADING;}
  {SpecHeading}     {return SPEC_HEADING;}
  {Step}            {return STEP;}
  {TableHeader}     {yybegin(INTABLE);return TABLE_HEADER;}
  {Comment}         {return COMMENT;}
}

<INTABLE> {
  {TableRow}         {yybegin(INTABLE);return TABLE_ROW;}
  [^]                {yypushback(1); yybegin(YYINITIAL);}
}
