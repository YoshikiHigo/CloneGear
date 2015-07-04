package yoshikihigo.clonegear.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.lexer.token.IDENTIFIER;
import yoshikihigo.clonegear.lexer.token.Token;

public class Statement {

	public static List<Statement> getStatements(final List<Token> allTokens) {

		final List<Statement> statements = new ArrayList<Statement>();
		List<Token> tokens = new ArrayList<Token>();

		for (final Token token : allTokens) {

			tokens.add(token);

			if (token.value.equals("{") || token.value.equals("}")
					|| token.value.equals(";") || token.value.startsWith("@")) {
				final int fromLine = tokens.get(0).line;
				final int toLine = tokens.get(tokens.size() - 1).line;
				final Statement statement = new Statement(fromLine, toLine,
						tokens);
				statements.add(statement);
				tokens = new ArrayList<Token>();
			}
		}

		return statements;
	}

	public static List<Statement> getFoldedStatements(
			final List<Statement> statements) {

		final List<Statement> folds = new ArrayList<>();
		for (int startIndex = 0; startIndex < statements.size();) {

			final Statement startStatement = statements.get(startIndex);

			int endIndex = startIndex;
			Statement endStatement = statements.get(endIndex);
			while ((endIndex + 1) < statements.size()) {
				endStatement = statements.get(endIndex + 1);
				if (!Arrays.equals(startStatement.hash, endStatement.hash)) {
					break;
				}
			}

			if (startIndex == endIndex) {
				folds.add(startStatement);
			}

			else {
				final int duplication = endIndex - startIndex + 1;
				final ConsecutiveStatement consecutive = new ConsecutiveStatement(
						startStatement.fromLine, endStatement.toLine,
						startStatement.tokens, duplication);
				folds.add(consecutive);
			}

			startIndex = endIndex + 1;
		}

		return folds;
	}

	private static byte[] makeHash(final List<Token> tokens) {

		final StringBuilder builder = new StringBuilder();
		final Map<String, String> identifiers = new HashMap<>();

		for (final Token token : tokens) {

			if (token instanceof IDENTIFIER) {
				final String name = token.value;
				String normalizedName = identifiers.get(name);
				if (null == normalizedName) {
					normalizedName = "$" + identifiers.size();
					identifiers.put(name, normalizedName);
				}
				builder.append(normalizedName);
			}

			else {
				builder.append(token.value);
			}

			builder.append(" ");
		}

		final String text = builder.toString();
		final byte[] md5 = getMD5(text);
		return md5;
	}

	private static byte[] getMD5(final String text) {
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			final byte[] data = text.getBytes();
			md.update(data);
			final byte[] digest = md.digest();
			return digest;
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	final public int fromLine;
	final public int toLine;
	final public List<Token> tokens;
	final public byte[] hash;

	public Statement(final int fromLine, final int toLine,
			final List<Token> tokens) {
		this.fromLine = fromLine;
		this.toLine = toLine;
		this.tokens = Collections.unmodifiableList(tokens);
		this.hash = makeHash(this.tokens);
	}

	public int getNumberOfTokens() {
		return this.tokens.size();
	}
}
