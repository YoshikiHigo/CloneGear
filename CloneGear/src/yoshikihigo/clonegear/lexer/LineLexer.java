package yoshikihigo.clonegear.lexer;

import java.util.List;

import yoshikihigo.clonegear.lexer.token.Token;

public interface LineLexer {

	List<Token> lexFile(final String text);

	List<Token> lexLine(final String line);
}
