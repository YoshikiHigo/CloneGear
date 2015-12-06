package yoshikihigo.clonegear.tfidf;

import java.util.Arrays;

import yoshikihigo.clonegear.lexer.token.Token;

public class NGram {

	final public Token[] tokens;

	public NGram(final Token[] tokens) {
		this.tokens = Arrays.copyOf(tokens, tokens.length);
	}

	@Override
	public int hashCode() {
		return tokens[0].getClass().hashCode()
				+ tokens[tokens.length - 1].getClass().hashCode();
	}

	@Override
	public boolean equals(final Object o) {

		if (!(o instanceof NGram)) {
			return false;
		}

		final NGram target = (NGram) o;
		if (this.tokens.length != target.tokens.length) {
			return false;
		}

		final int n = this.tokens.length;
		for (int index = 0; index < n; index++) {
			if (this.tokens[index].getClass() != target.tokens[index]
					.getClass()) {
				return false;
			}
		}

		return true;
	}
}
