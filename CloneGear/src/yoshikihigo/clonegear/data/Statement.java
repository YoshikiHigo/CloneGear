package yoshikihigo.clonegear.data;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.lexer.token.IDENTIFIER;
import yoshikihigo.clonegear.lexer.token.LINEEND;
import yoshikihigo.clonegear.lexer.token.LINEINTERRUPTION;
import yoshikihigo.clonegear.lexer.token.SEMICOLON;
import yoshikihigo.clonegear.lexer.token.TAB;
import yoshikihigo.clonegear.lexer.token.Token;

public class Statement {

	public static List<Statement> getJCStatements(final List<Token> allTokens) {

		final List<Statement> statements = new ArrayList<Statement>();
		List<Token> tokens = new ArrayList<Token>();
		int nestLevel = 0;
		int index = 0;
		for (final Token token : allTokens) {

			if (!token.value.equals("{") && !token.value.equals("}")) {
				token.index = index++;
				tokens.add(token);
			}

			if (token.value.equals("}")) {
				nestLevel--;
			}

			if (token.value.equals("{") || token.value.equals("}")
					|| token.value.equals(";") || token.value.startsWith("@")) {
				if (!tokens.isEmpty()) {
					final int fromLine = tokens.get(0).line;
					final int toLine = tokens.get(tokens.size() - 1).line;
					final byte[] hash = makeHash(tokens);
					final Statement statement = new Statement(fromLine, toLine,
							nestLevel, tokens, hash);
					statements.add(statement);
					tokens = new ArrayList<Token>();
				}
			}

			if (token.value.equals("{")) {
				nestLevel++;
			}
		}

		return statements;
	}

	public static List<Statement> getPYStatements(final List<Token> allTokens) {

		final List<Statement> statements = new ArrayList<Statement>();
		List<Token> tokens = new ArrayList<Token>();
		int nestLevel = 0;
		int index = 0;
		boolean interrupted = false;
		for (final Token token : allTokens) {

			if (token instanceof TAB) {
				if (!interrupted) {
					nestLevel++;
				}
			}

			else if (token instanceof LINEINTERRUPTION) {
				interrupted = false;
			}

			else if ((token instanceof LINEEND) || (token instanceof SEMICOLON)) {
				if (!tokens.isEmpty()) {
					final int fromLine = tokens.get(0).line;
					final int toLine = tokens.get(tokens.size() - 1).line;
					final byte[] hash = makeHash(tokens);
					final Statement statement = new Statement(fromLine, toLine,
							nestLevel, tokens, hash);
					statements.add(statement);
					tokens = new ArrayList<Token>();
				}
				if (token instanceof LINEEND) {
					nestLevel = 0;
					interrupted = false;
				}
			}

			else {
				token.index = index++;
				tokens.add(token);
			}
		}

		return statements;

	}

	public static List<Statement> getFoldedStatements(
			final List<Statement> statements) {

		final List<Statement> folds = new ArrayList<>();
		for (int startIndex = 0; startIndex < statements.size();) {

			final Statement startStatement = statements.get(startIndex);
			final List<Token> foldedTokens = new ArrayList<>(
					startStatement.tokens);
			final List<Statement> foldedStatements = new ArrayList<>();
			foldedStatements.add(startStatement);
			final int startNestLevel = startStatement.nestLevel;

			int endIndex = startIndex;
			Statement endStatement = statements.get(endIndex);
			while ((endIndex + 1) < statements.size()) {
				endStatement = statements.get(endIndex + 1);
				if (!Arrays.equals(startStatement.hash, endStatement.hash)) {
					break;
				}

				final int endNestLevel = endStatement.nestLevel;
				if (startNestLevel != endNestLevel) {
					break;
				}
				foldedStatements.add(endStatement);
				foldedTokens.addAll(endStatement.tokens);
				endIndex++;
			}

			if (startIndex == endIndex) {
				folds.add(startStatement);
			}

			else {
				final ConsecutiveStatement consecutive = new ConsecutiveStatement(
						startStatement.fromLine, endStatement.toLine,
						startStatement.nestLevel, foldedTokens,
						startStatement.hash, foldedStatements);
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
			final byte[] data = text.getBytes("UTF-8");
			md.update(data);
			final byte[] digest = md.digest();
			return digest;
		} catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	final public int fromLine;
	final public int toLine;
	final public int nestLevel;
	final public List<Token> tokens;
	final public byte[] hash;

	public Statement(final int fromLine, final int toLine, final int nestLevel,
			final List<Token> tokens, final byte[] hash) {
		this.fromLine = fromLine;
		this.toLine = toLine;
		this.nestLevel = nestLevel;
		this.tokens = Collections.unmodifiableList(tokens);
		this.hash = hash;
	}

	public int getNumberOfTokens() {
		return this.tokens.size();
	}
}
