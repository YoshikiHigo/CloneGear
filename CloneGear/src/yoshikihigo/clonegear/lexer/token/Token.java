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
}
