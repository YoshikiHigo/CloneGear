package yoshikihigo.clonegear.lexer.token;

public class STRINGLITERAL extends Token {

	public STRINGLITERAL(final String value) {
		super("\"" + value + "\"");
	}
}
