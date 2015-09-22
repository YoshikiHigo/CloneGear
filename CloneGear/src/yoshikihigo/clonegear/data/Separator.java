package yoshikihigo.clonegear.data;

import java.util.ArrayList;

import yoshikihigo.clonegear.lexer.token.Token;

public class Separator extends Statement {

	public Separator() {
		super(0, 0, 0, false, new ArrayList<Token>(), MD5.getMD5(""));
	}
}
