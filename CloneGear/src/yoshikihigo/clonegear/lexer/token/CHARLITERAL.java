package yoshikihigo.clonegear.lexer.token;

public class CHARLITERAL extends Token {

	public CHARLITERAL(final String value) {
		super("\'" + value + "\'");
	}
}
