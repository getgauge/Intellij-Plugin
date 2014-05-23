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

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = [ \t\f]
ScenarioHeading = {WhiteSpace}* "##" {InputCharacter}* {LineTerminator}*
SpecHeading = {WhiteSpace}* "#" {InputCharacter}* {LineTerminator}*
Step = {WhiteSpace}* "*" {InputCharacter}* {LineTerminator}*
Comment = {InputCharacter}*? {LineTerminator}*?
%%
<YYINITIAL> {
  {ScenarioHeading} {return SCENARIO_HEADING;}
  {SpecHeading}     {return SPEC_HEADING;}
  {Step}            {return STEP;}
  {Comment}         {return COMMENT;}
}
