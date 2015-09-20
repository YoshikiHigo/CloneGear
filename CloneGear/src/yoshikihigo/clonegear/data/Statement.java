package yoshikihigo.clonegear.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import yoshikihigo.clonegear.CGConfig;
import yoshikihigo.clonegear.lexer.token.ABSTRACT;
import yoshikihigo.clonegear.lexer.token.ANNOTATION;
import yoshikihigo.clonegear.lexer.token.CLASS;
import yoshikihigo.clonegear.lexer.token.COLON;
import yoshikihigo.clonegear.lexer.token.DEF;
import yoshikihigo.clonegear.lexer.token.FINAL;
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

	public static List<Statement> getJCStatements(final List<Token> allTokens)
			throws EmptyStackException {

		final List<Statement> statements = new ArrayList<Statement>();
		List<Token> tokens = new ArrayList<Token>();

		final Stack<Integer> nestLevel = new Stack<>();
		nestLevel.push(Integer.valueOf(1));
		int inAnnotationDepth = 0;
		int inParenDepth = 0;
		int inTernaryOperationDepth = 0;
		int index = 0;
		final boolean isDebug = CGConfig.getInstance().isDEBUG();

		try {
			for (final Token token : allTokens) {

				token.index = index++;
				if (0 < inAnnotationDepth) {
					final ANNOTATION annotation = new ANNOTATION(token.value);
					annotation.index = index++;
					annotation.line = token.line;
					tokens.add(annotation);
				} else {
					token.index = index++;
					tokens.add(token);
				}

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
					if (0 < inAnnotationDepth) {
						inAnnotationDepth--;
					}
				}

				if ((0 == inParenDepth)
						&& (0 == inTernaryOperationDepth)
						&& (token instanceof LEFTBRACKET
								|| token instanceof RIGHTBRACKET
								|| token instanceof SEMICOLON || token instanceof COLON)) {

					if (1 < tokens.size()) {

						if (isJCTypeDefinition(tokens)) {
							nestLevel.push(new Integer(0));
						}

						final int nestDepth = nestLevel.peek().intValue();

						final int fromLine = tokens.get(0).line;
						final int toLine = tokens.get(tokens.size() - 1).line;
						final MD5 hash = MD5.makeJCHash(tokens);
						final Statement statement = new Statement(fromLine,
								toLine, nestDepth, 1 < nestDepth, tokens, hash);
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
					nestLevel
							.push(new Integer(nestLevel.peek().intValue() + 1));
				}

				if ((0 < inTernaryOperationDepth) && (token instanceof COLON)) {
					inTernaryOperationDepth--;
				}

				if (token instanceof LEFTPAREN) {
					inParenDepth++;
					if ((1 < tokens.size())
							&& (tokens.get(tokens.size() - 2) instanceof ANNOTATION)) {
						inAnnotationDepth++;
						tokens.remove(tokens.size() - 1);
						final ANNOTATION annotation = new ANNOTATION(
								token.value);
						annotation.index = index++;
						annotation.line = token.line;
						tokens.add(annotation);
					}
				}
			}
		}

		catch (final EmptyStackException e) {
			System.err.println("parsing error has happened.");
		}

		return statements;
	}

	public static List<Statement> getJSStatements(final List<Token> allTokens)
			throws EmptyStackException {

		final List<Statement> statements = new ArrayList<Statement>();
		List<Token> tokens = new ArrayList<Token>();

		final Stack<Integer> nestLevel = new Stack<>();
		nestLevel.push(Integer.valueOf(1));
		int inTernaryOperationDepth = 0;
		int index = 0;
		final boolean isDebug = CGConfig.getInstance().isDEBUG();

		try {
			for (final Token token : allTokens) {

				token.index = index++;
				tokens.add(token);

				if (token instanceof RIGHTBRACKET) {
					nestLevel.pop();
				}

				if (token instanceof QUESTION) {
					inTernaryOperationDepth++;
				}

				if ((0 == inTernaryOperationDepth)
						&& (token instanceof LEFTBRACKET
								|| token instanceof RIGHTBRACKET
								|| token instanceof SEMICOLON || token instanceof COLON)) {

					if (1 < tokens.size()) {

						final int nestDepth = nestLevel.peek().intValue();

						final int fromLine = tokens.get(0).line;
						final int toLine = tokens.get(tokens.size() - 1).line;
						final MD5 hash = MD5.makeJCHash(tokens);
						final Statement statement = new Statement(fromLine,
								toLine, nestDepth, true, tokens, hash);
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

				if (token instanceof LEFTBRACKET) {
					nestLevel
							.push(new Integer(nestLevel.peek().intValue() + 1));
				}

				if ((0 < inTernaryOperationDepth) && (token instanceof COLON)) {
					inTernaryOperationDepth--;
				}
			}
		}

		catch (final EmptyStackException e) {
			System.err.println("parsing error has happened.");
		}

		return statements;
	}

	public static List<Statement> getPYStatements(final List<Token> allTokens)
			throws EmptyStackException {

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

		try {
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
							methodDefinitionDepth.push(Integer
									.valueOf(nestLevel));
						}

						final int fromLine = tokens.get(0).line;
						final int toLine = tokens.get(tokens.size() - 1).line;
						final boolean isTarget = (!methodDefinitionDepth
								.isEmpty() && (methodDefinitionDepth.peek()
								.intValue() < nestLevel));
						final MD5 hash = MD5.makePYHash(tokens);
						final Statement statement = new Statement(fromLine,
								toLine, nestLevel, isTarget, tokens, hash);
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
		}

		catch (final EmptyStackException e) {
			System.err.println("parsing error has happened.");
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
				if (!Arrays.equals(startStatement.hash.value,
						endStatement.hash.value)) {
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

	static List<Token> removeJCTrivialTokens(final List<Token> tokens) {
		final List<Token> nonTrivialTokens = new ArrayList<>();
		for (final Token token : tokens) {

			if (token instanceof ABSTRACT || token instanceof FINAL
					|| token instanceof PRIVATE || token instanceof PROTECTED
					|| token instanceof PUBLIC || token instanceof STATIC
					|| token instanceof STRICTFP || token instanceof TRANSIENT) {
				// not used for making hash
				continue;
			}

			else if (token instanceof ANNOTATION) {
				continue;
			}

			else {
				nonTrivialTokens.add(token);
			}
		}

		return nonTrivialTokens;
	}

	static List<Token> removePYTrivialTokens(final List<Token> tokens) {
		final List<Token> nonTrivialTokens = new ArrayList<>();
		for (final Token token : tokens) {
			nonTrivialTokens.add(token);
		}

		return nonTrivialTokens;
	}

	private static boolean isJCTypeDefinition(final List<Token> tokens) {
		final List<Token> nonTrivialTokens = Statement
				.removeJCTrivialTokens(tokens);
		final Token firstToken = nonTrivialTokens.get(0);
		if (firstToken instanceof CLASS || firstToken instanceof INTERFACE) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isPYMethodDefinition(final List<Token> tokens) {
		final List<Token> nonTrivialTokens = Statement
				.removeJCTrivialTokens(tokens);
		if (nonTrivialTokens.isEmpty()) {
			return false;
		}
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
	final public MD5 hash;

	public Statement(final int fromLine, final int toLine, final int nestLevel,
			final boolean isTarget, final List<Token> tokens, final MD5 hash) {
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
