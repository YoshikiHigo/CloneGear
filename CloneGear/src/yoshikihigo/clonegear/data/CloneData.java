package yoshikihigo.clonegear.data;

import java.util.Collections;
import java.util.List;

import yoshikihigo.clonegear.lexer.token.Token;

abstract public class CloneData {

	final public CloneHash hash;
	final public List<Token> tokens;

	protected CloneData(final CloneHash hash, final List<Token> tokens) {
		this.hash = hash;
		this.tokens = Collections.unmodifiableList(tokens);
	}

	abstract public int getID();
}
