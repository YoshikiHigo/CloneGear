package yoshikihigo.clonegear.lexer.token;

public abstract class Token {

	final public String value;
	public int line;
	public int index;

	Token(final String value) {
		this.value = value;
		this.line = 0;
		this.index = 0;
	}

	@Override
	final public int hashCode() {
		return this.getClass().hashCode();
	}

	@Override
	final public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}
		return this.getClass() == o.getClass();
	}
}
