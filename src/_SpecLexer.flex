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


%%
<YYINITIAL> {
  "##" {InputCharacter}* {LineTerminator}*  {return SCENARIOHEADING;}
  "#" {InputCharacter}* {LineTerminator}*  {return SPECHEADING;}
  "*" {InputCharacter}* {LineTerminator}*  {return STEP;}
  {InputCharacter}*? {LineTerminator}*     {return COMMENT;}
  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
