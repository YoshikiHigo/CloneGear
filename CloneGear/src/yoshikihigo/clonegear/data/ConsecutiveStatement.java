package yoshikihigo.clonegear.data;

import java.util.List;

import yoshikihigo.clonegear.lexer.token.Token;

public class ConsecutiveStatement extends Statement {

	final int duplication;

	public ConsecutiveStatement(final int fromLine, final int toLine,
			final int nestLevel, final List<Token> tokens, final int duplication) {
		super(fromLine, toLine, nestLevel, tokens);
		this.duplication = duplication;
	}
}
