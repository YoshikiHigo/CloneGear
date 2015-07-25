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
import java.util.Stack;

import yoshikihigo.clonegear.CGConfig;
import yoshikihigo.clonegear.lexer.token.ABSTRACT;
import yoshikihigo.clonegear.lexer.token.ANNOTATION;
import yoshikihigo.clonegear.lexer.token.CLASS;
import yoshikihigo.clonegear.lexer.token.COLON;
import yoshikihigo.clonegear.lexer.token.DEF;
import yoshikihigo.clonegear.lexer.token.FINAL;
import yoshikihigo.clonegear.lexer.token.IDENTIFIER;
import yoshikihigo.clonegear.lexer.token.INTERFACE;
import yoshikihigo.clonegear.lexer.token.LEFTBRACKET;
import yoshikihigo.clonegear.lexer.token.LEFTPAREN;
import yoshikihigo.clonegear.lexer.token.LEFTSQUAREBRACKET;
import yoshikihigo.clonegear.lexer.token.LINEEND;
import yoshikihigo.clonegear.lexer.token.LINEINTERRUPTION;
import yoshikihigo.clonegear.lexer.token.PRIVATE;
import yoshikihigo.clonegear.lexer.token.PROTECTED;
import yoshikihigo.clonegear.lexer.token.PUBLIC;
import yoshikihigo.clonegear.lexer.token.QUESTION;
import yoshikihigo.clonegear.lexer.token.RIGHTBRACKET;
import yoshikihigo.clonegear.lexer.token.RIGHTPAREN;
import yoshikihigo.clonegear.lexer.token.RIGHTSQUAREBRACKET;
import yoshikihigo.clonegear.lexer.token.SEMICOLON;
import yoshikihigo.clonegear.lexer.token.STATIC;
import yoshikihigo.clonegear.lexer.token.STRICTFP;
import yoshikihigo.clonegear.lexer.token.TAB;
import yoshikihigo.clonegear.lexer.token.TRANSIENT;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.clonegear.lexer.token.WHITESPACE;

public class Statement {

	public static List<Statement> getJCStatements(final List<Token> allTokens) {

		final List<Statement> statements = new ArrayList<Statement>();
		List<Token> tokens = new ArrayList<Token>();

		final Stack<Integer> nestLevel = new Stack<>();
		nestLevel.push(new Integer(1));
		int inParenDepth = 0;
		int inTernaryOperationDepth = 0;
		int index = 0;
		final boolean isDebug = CGConfig.getInstance().isDEBUG();

		for (final Token token : allTokens) {

			token.index = index++;
			tokens.add(token);

			if ((0 == inParenDepth) && (token instanceof RIGHTBRACKET)) {
				if (0 == nestLevel.peek().intValue()) {
					nestLevel.pop();
					nestLevel.pop();
				} else {
					nestLevel.pop();
				}
			}

			if (token instanceof QUESTION) {
				inTernaryOperationDepth++;
			}

			if (token instanceof RIGHTPAREN) {
				inParenDepth--;
			}

			if ((0 == inParenDepth)
					&& (0 == inTernaryOperationDepth)
					&& (token instanceof LEFTBRACKET
							|| token instanceof RIGHTBRACKET
							|| token instanceof SEMICOLON
							|| token instanceof COLON || token instanceof ANNOTATION)) {

				if (1 < tokens.size()) {

					if (isJCTypeDefinition(tokens)) {
						nestLevel.push(new Integer(0));
					}
					final int nestDepth = nestLevel.peek().intValue();

					final int fromLine = tokens.get(0).line;
					final int toLine = tokens.get(tokens.size() - 1).line;
					final byte[] hash = makeJCHash(tokens);
					final Statement statement = new Statement(fromLine, toLine,
							nestDepth, 1 < nestDepth, tokens, hash);
					statements.add(statement);
					tokens = new ArrayList<Token>();

					if (isDebug) {
						System.out.println(statement.toString());
					}
				}

				else {
					tokens.clear();
				}
			}

			if ((0 == inParenDepth) && (token instanceof LEFTBRACKET)) {
				nestLevel.push(new Integer(nestLevel.peek().intValue() + 1));
			}

			if ((0 < inTernaryOperationDepth) && (token instanceof COLON)) {
				inTernaryOperationDepth--;
			}

			if (token instanceof LEFTPAREN) {
				inParenDepth++;
			}

		}

		return statements;
	}

	public static List<Statement> getPYStatements(final List<Token> allTokens) {

		final List<Statement> statements = new ArrayList<Statement>();
		List<Token> tokens = new ArrayList<Token>();

		final Stack<Integer> methodDefinitionDepth = new Stack<>();

		int nestLevel = 0;
		int index = 0;
		int inParenDepth = 0;
		int inBracketDepth = 0;
		int inSquareBracketDepth = 0;
		boolean interrupted = false;
		boolean isIndent = true;
		final boolean isDebug = CGConfig.getInstance().isDEBUG();

		for (final Token token : allTokens) {

			if ((token instanceof TAB) || (token instanceof WHITESPACE)) {
				if (isIndent && !interrupted) {
					nestLevel++;
				}
			} else {
				isIndent = false;
			}

			if (!(token instanceof TAB) && !(token instanceof WHITESPACE)
					&& !(token instanceof LINEEND)) {
				token.index = index++;
			}

			if (!(token instanceof TAB) && !(token instanceof WHITESPACE)
					&& !(token instanceof LINEEND)
					&& !(token instanceof SEMICOLON)
					&& !(token instanceof LINEINTERRUPTION)) {
				tokens.add(token);
			}

			if (token instanceof RIGHTPAREN) {
				inParenDepth--;
			}

			if (token instanceof LEFTPAREN) {
				inParenDepth++;
			}

			if (token instanceof RIGHTBRACKET) {
				inBracketDepth--;
			}

			if (token instanceof LEFTBRACKET) {
				inBracketDepth++;
			}

			if (token instanceof RIGHTSQUAREBRACKET) {
				inSquareBracketDepth--;
			}

			if (token instanceof LEFTSQUAREBRACKET) {
				inSquareBracketDepth++;
			}

			if (token instanceof LINEINTERRUPTION) {
				interrupted = true;
			} else if (token instanceof LINEEND) {
				// do nothing
			} else {
				interrupted = false;
			}

			// make a statement
			if (!interrupted
					&& (0 == inParenDepth)
					&& (0 == inBracketDepth)
					&& (0 == inSquareBracketDepth)
					&& ((token instanceof LINEEND) || (token instanceof SEMICOLON))) {
				if (!tokens.isEmpty()) {

					if (!methodDefinitionDepth.isEmpty()
							&& (nestLevel <= methodDefinitionDepth.peek()
									.intValue())) {
						methodDefinitionDepth.pop();
					}

					if (isPYMethodDefinition(tokens)) {
						methodDefinitionDepth.push(new Integer(nestLevel));
					}

					final int fromLine = tokens.get(0).line;
					final int toLine = tokens.get(tokens.size() - 1).line;
					final boolean isTarget = (!methodDefinitionDepth.isEmpty() && (methodDefinitionDepth
							.peek().intValue() < nestLevel));
					final byte[] hash = makePYHash(tokens);
					final Statement statement = new Statement(fromLine, toLine,
							nestLevel, isTarget, tokens, hash);
					statements.add(statement);
					tokens = new ArrayList<Token>();

					if (isDebug) {
						System.out.println(statement.toString());
					}
				}
				if (token instanceof LINEEND) {
					nestLevel = 0;
					isIndent = true;
				}
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
					endStatement = statements.get(endIndex);
					break;
				}

				final int endNestLevel = endStatement.nestLevel;
				if (startNestLevel != endNestLevel) {
					endStatement = statements.get(endIndex);
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
						startStatement.nestLevel, startStatement.isTarget,
						foldedTokens, startStatement.hash, foldedStatements);
				folds.add(consecutive);
			}

			startIndex = endIndex + 1;
		}

		return folds;
	}

	private static byte[] makeJCHash(final List<Token> tokens) {

		final List<Token> nonTrivialTokens = removeJCTrivialTokens(tokens);
		final StringBuilder builder = new StringBuilder();
		final Map<String, String> identifiers = new HashMap<>();

		for (int index = 0; index < nonTrivialTokens.size(); index++) {

			final Token token = nonTrivialTokens.get(index);

			if (token instanceof IDENTIFIER) {

				if (nonTrivialTokens.size() == (index + 1)
						|| !(nonTrivialTokens.get(index + 1) instanceof LEFTPAREN)) {
					final String name = token.value;
					String normalizedName = identifiers.get(name);
					if (null == normalizedName) {
						normalizedName = "$" + identifiers.size();
						identifiers.put(name, normalizedName);
					}
					builder.append(normalizedName);
				}

				// not normalize if identifier is method name
				else {
					builder.append(token.value);
				}
			}

			else {
				builder.append(token.value);
			}

			builder.append(" ");
		}

		final String text = builder.toString();
		// System.out.println(text);
		final byte[] md5 = getMD5(text);
		return md5;
	}

	private static byte[] makePYHash(final List<Token> tokens) {

		final List<Token> nonTrivialTokens = removePYTrivialTokens(tokens);
		final StringBuilder builder = new StringBuilder();
		final Map<String, String> identifiers = new HashMap<>();

		for (int index = 0; index < nonTrivialTokens.size(); index++) {

			final Token token = nonTrivialTokens.get(index);

			if (token instanceof IDENTIFIER) {

				if (nonTrivialTokens.size() == (index + 1)
						|| !(nonTrivialTokens.get(index + 1) instanceof LEFTPAREN)) {
					final String name = token.value;
					String normalizedName = identifiers.get(name);
					if (null == normalizedName) {
						normalizedName = "$" + identifiers.size();
						identifiers.put(name, normalizedName);
					}
					builder.append(normalizedName);
				}

				// not normalize if identifier is method name
				else {
					builder.append(token.value);
				}
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

	private static List<Token> removeJCTrivialTokens(final List<Token> tokens) {
		final List<Token> nonTrivialTokens = new ArrayList<>();
		for (final Token token : tokens) {

			if (token instanceof ABSTRACT || token instanceof FINAL
					|| token instanceof PRIVATE || token instanceof PROTECTED
					|| token instanceof PUBLIC || token instanceof STATIC
					|| token instanceof STRICTFP || token instanceof TRANSIENT) {
				// not used for making hash
				continue;
			}

			else {
				nonTrivialTokens.add(token);
			}
		}

		return nonTrivialTokens;
	}

	private static List<Token> removePYTrivialTokens(final List<Token> tokens) {
		final List<Token> nonTrivialTokens = new ArrayList<>();
		for (final Token token : tokens) {
			nonTrivialTokens.add(token);
		}

		return nonTrivialTokens;
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

	private static boolean isJCTypeDefinition(final List<Token> tokens) {
		final List<Token> nonTrivialTokens = removeJCTrivialTokens(tokens);
		final Token firstToken = nonTrivialTokens.get(0);
		if (firstToken instanceof CLASS || firstToken instanceof INTERFACE) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isPYMethodDefinition(final List<Token> tokens) {
		final List<Token> nonTrivialTokens = removeJCTrivialTokens(tokens);
		final Token firstToken = nonTrivialTokens.get(0);
		if (firstToken instanceof DEF) {
			return true;
		} else {
			return false;
		}
	}

	final public int fromLine;
	final public int toLine;
	final public int nestLevel;
	final public boolean isTarget;
	final public List<Token> tokens;
	final public byte[] hash;

	public Statement(final int fromLine, final int toLine, final int nestLevel,
			final boolean isTarget, final List<Token> tokens, final byte[] hash) {
		this.fromLine = fromLine;
		this.toLine = toLine;
		this.nestLevel = nestLevel;
		this.isTarget = isTarget;
		this.tokens = Collections.unmodifiableList(tokens);
		this.hash = hash;
	}

	public int getNumberOfTokens() {
		return this.tokens.size();
	}

	@Override
	public String toString() {
		final StringBuilder text = new StringBuilder();
		text.append(Integer.toString(this.nestLevel));
		text.append(" (");
		text.append(Boolean.toString(this.isTarget));
		text.append("): ");
		for (final Token token : this.tokens) {
			text.append(token.value);
			text.append(" ");
		}
		return text.toString();
	}
}
