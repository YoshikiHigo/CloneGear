package yoshikihigo.clonegear.lexer.token;

public class STRINGLITERAL extends LITERAL {

	public STRINGLITERAL(final String value) {
		super("\"" + value + "\"");
	}
}
