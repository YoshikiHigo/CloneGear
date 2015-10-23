package yoshikihigo.clonegear.lexer.token;

public class CHARLITERAL extends LITERAL {

	public CHARLITERAL(final String value) {
		super("\'" + value + "\'");
	}
}
