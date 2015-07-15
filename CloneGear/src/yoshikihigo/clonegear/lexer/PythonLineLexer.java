package yoshikihigo.clonegear.lexer;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import yoshikihigo.clonegear.lexer.token.AND;
import yoshikihigo.clonegear.lexer.token.AND2;
import yoshikihigo.clonegear.lexer.token.ANDEQUAL;
import yoshikihigo.clonegear.lexer.token.ANNOTATION;
import yoshikihigo.clonegear.lexer.token.ASSERT;
import yoshikihigo.clonegear.lexer.token.ASSIGN;
import yoshikihigo.clonegear.lexer.token.BACKQUOTELITERAL;
import yoshikihigo.clonegear.lexer.token.BACKSLASH;
import yoshikihigo.clonegear.lexer.token.CHARLITERAL;
import yoshikihigo.clonegear.lexer.token.CLASS;
import yoshikihigo.clonegear.lexer.token.COLON;
import yoshikihigo.clonegear.lexer.token.COMMA;
import yoshikihigo.clonegear.lexer.token.CONTINUE;
import yoshikihigo.clonegear.lexer.token.DEF;
import yoshikihigo.clonegear.lexer.token.DEL;
import yoshikihigo.clonegear.lexer.token.DIVIDE;
import yoshikihigo.clonegear.lexer.token.DIVIDEDIVIDEEQUAL;
import yoshikihigo.clonegear.lexer.token.DIVIDEEQUAL;
import yoshikihigo.clonegear.lexer.token.DOT;
import yoshikihigo.clonegear.lexer.token.ELIF;
import yoshikihigo.clonegear.lexer.token.ELSE;
import yoshikihigo.clonegear.lexer.token.EQUAL;
import yoshikihigo.clonegear.lexer.token.EXCEPT;
import yoshikihigo.clonegear.lexer.token.EXCLUSIVEOR;
import yoshikihigo.clonegear.lexer.token.EXCLUSIVEOREQUAL;
import yoshikihigo.clonegear.lexer.token.FALSE2;
import yoshikihigo.clonegear.lexer.token.FINALLY;
import yoshikihigo.clonegear.lexer.token.FOR;
import yoshikihigo.clonegear.lexer.token.FROM;
import yoshikihigo.clonegear.lexer.token.GLOBAL;
import yoshikihigo.clonegear.lexer.token.GREAT;
import yoshikihigo.clonegear.lexer.token.GREATEQUAL;
import yoshikihigo.clonegear.lexer.token.IDENTIFIER;
import yoshikihigo.clonegear.lexer.token.IF;
import yoshikihigo.clonegear.lexer.token.IMPORT;
import yoshikihigo.clonegear.lexer.token.IN;
import yoshikihigo.clonegear.lexer.token.IS;
import yoshikihigo.clonegear.lexer.token.LAMBDA;
import yoshikihigo.clonegear.lexer.token.LEFTBRACKET;
import yoshikihigo.clonegear.lexer.token.LEFTPAREN;
import yoshikihigo.clonegear.lexer.token.LEFTSHIFTEQUAL;
import yoshikihigo.clonegear.lexer.token.LEFTSQUAREBRACKET;
import yoshikihigo.clonegear.lexer.token.LESS;
import yoshikihigo.clonegear.lexer.token.LESSEQUAL;
import yoshikihigo.clonegear.lexer.token.MINUS;
import yoshikihigo.clonegear.lexer.token.MINUSEQUAL;
import yoshikihigo.clonegear.lexer.token.MOD;
import yoshikihigo.clonegear.lexer.token.MODEQUAL;
import yoshikihigo.clonegear.lexer.token.NONE;
import yoshikihigo.clonegear.lexer.token.NONLOCAL;
import yoshikihigo.clonegear.lexer.token.NOT;
import yoshikihigo.clonegear.lexer.token.NOT2;
import yoshikihigo.clonegear.lexer.token.NOTEQUAL;
import yoshikihigo.clonegear.lexer.token.NUMBERLITERAL;
import yoshikihigo.clonegear.lexer.token.OR;
import yoshikihigo.clonegear.lexer.token.OR2;
import yoshikihigo.clonegear.lexer.token.OREQUAL;
import yoshikihigo.clonegear.lexer.token.PASS;
import yoshikihigo.clonegear.lexer.token.PLUS;
import yoshikihigo.clonegear.lexer.token.PLUSEQUAL;
import yoshikihigo.clonegear.lexer.token.QUESTION;
import yoshikihigo.clonegear.lexer.token.RAISE;
import yoshikihigo.clonegear.lexer.token.RETURN;
import yoshikihigo.clonegear.lexer.token.RIGHTBRACKET;
import yoshikihigo.clonegear.lexer.token.RIGHTPAREN;
import yoshikihigo.clonegear.lexer.token.RIGHTSHIFTEQUAL;
import yoshikihigo.clonegear.lexer.token.RIGHTSQUAREBRACKET;
import yoshikihigo.clonegear.lexer.token.SEMICOLON;
import yoshikihigo.clonegear.lexer.token.STAR;
import yoshikihigo.clonegear.lexer.token.STAREQUAL;
import yoshikihigo.clonegear.lexer.token.STARSTAREQUAL;
import yoshikihigo.clonegear.lexer.token.STRINGLITERAL;
import yoshikihigo.clonegear.lexer.token.TILDA;
import yoshikihigo.clonegear.lexer.token.TRUE2;
import yoshikihigo.clonegear.lexer.token.TRY;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.clonegear.lexer.token.WHILE;
import yoshikihigo.clonegear.lexer.token.WITH;
import yoshikihigo.clonegear.lexer.token.YIELD;

public class PythonLineLexer implements LineLexer {

	enum STATE {
		CODE, SINGLEQUOTELITERAL, DOUBLEQUOTELITERAL, BACKQUOTELITERAL;
	}

	@Override
	public List<Token> lexFile(final String text) {

		final List<Token> tokens = new ArrayList<Token>();

		try (final LineNumberReader reader = new LineNumberReader(
				new StringReader(text))) {

			String line;
			final PythonLineLexer lexer = new PythonLineLexer();
			while (null != (line = reader.readLine())) {
				for (final Token t : lexer.lexLine(line)) {
					t.line = reader.getLineNumber();
					tokens.add(t);
				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		return tokens;
	}

	public List<Token> lexLine(final String line) {
		final List<Token> tokenList = new ArrayList<Token>();
		this.lex(new StringBuilder(line), tokenList);
		return tokenList;
	}

	final private Stack<STATE> states;

	public PythonLineLexer() {
		this.states = new Stack<STATE>();
		this.states.push(STATE.CODE);
	}

	private void lex(final StringBuilder text, final List<Token> tokenList) {

		if (0 == text.length()) {
			return;
		}

		final String string = text.toString();
		if (STATE.CODE == this.states.peek()) {

			if (string.startsWith("**=")) {
				text.delete(0, 3);
				tokenList.add(new STARSTAREQUAL());
			} else if (string.startsWith("//=")) {
				text.delete(0, 3);
				tokenList.add(new DIVIDEDIVIDEEQUAL());
			} else if (string.startsWith("<<=")) {
				text.delete(0, 3);
				tokenList.add(new LEFTSHIFTEQUAL());
			} else if (string.startsWith(">>=")) {
				text.delete(0, 3);
				tokenList.add(new RIGHTSHIFTEQUAL());
			} else if (string.startsWith("-=")) {
				text.delete(0, 2);
				tokenList.add(new MINUSEQUAL());
			} else if (string.startsWith("+=")) {
				text.delete(0, 2);
				tokenList.add(new PLUSEQUAL());
			} else if (string.startsWith("/=")) {
				text.delete(0, 2);
				tokenList.add(new DIVIDEEQUAL());
			} else if (string.startsWith("*=")) {
				text.delete(0, 2);
				tokenList.add(new STAREQUAL());
			} else if (string.startsWith("%=")) {
				text.delete(0, 2);
				tokenList.add(new MODEQUAL());

			} else if (string.startsWith("&=")) {
				text.delete(0, 2);
				tokenList.add(new ANDEQUAL());
			} else if (string.startsWith("|=")) {
				text.delete(0, 2);
				tokenList.add(new OREQUAL());
			} else if (string.startsWith("^=")) {
				text.delete(0, 2);
				tokenList.add(new EXCLUSIVEOREQUAL());

			} else if (string.startsWith("<=")) {
				text.delete(0, 2);
				tokenList.add(new LESSEQUAL());
			} else if (string.startsWith(">=")) {
				text.delete(0, 2);
				tokenList.add(new GREATEQUAL());
			} else if (string.startsWith("==")) {
				text.delete(0, 2);
				tokenList.add(new EQUAL());
			} else if (string.startsWith("<>")) {
				text.delete(0, 2);
				tokenList.add(new NOTEQUAL());
			} else if (string.startsWith("!")) {
				text.delete(0, 1);
				tokenList.add(new NOT());
			}

			else if (string.startsWith(":")) {
				text.delete(0, 1);
				tokenList.add(new COLON());
			} else if (string.startsWith(";")) {
				text.delete(0, 1);
				tokenList.add(new SEMICOLON());
			} else if (string.startsWith("=")) {
				text.delete(0, 1);
				tokenList.add(new ASSIGN());
			} else if (string.startsWith("-")) {
				text.delete(0, 1);
				tokenList.add(new MINUS());
			} else if (string.startsWith("+")) {
				text.delete(0, 1);
				tokenList.add(new PLUS());
			} else if (string.startsWith("/")) {
				text.delete(0, 1);
				tokenList.add(new DIVIDE());
			} else if (string.startsWith("*")) {
				text.delete(0, 1);
				tokenList.add(new STAR());
			} else if (string.startsWith("%")) {
				text.delete(0, 1);
				tokenList.add(new MOD());
			} else if (string.startsWith("?")) {
				text.delete(0, 1);
				tokenList.add(new QUESTION());
			} else if (string.startsWith("<")) {
				text.delete(0, 1);
				tokenList.add(new LESS());
			} else if (string.startsWith(">")) {
				text.delete(0, 1);
				tokenList.add(new GREAT());
			} else if (string.startsWith("&")) {
				text.delete(0, 1);
				tokenList.add(new AND());
			} else if (string.startsWith("|")) {
				text.delete(0, 1);
				tokenList.add(new OR());
			} else if (string.startsWith("~")) {
				text.delete(0, 1);
				tokenList.add(new TILDA());
			} else if (string.startsWith("^")) {
				text.delete(0, 1);
				tokenList.add(new EXCLUSIVEOR());
			} else if (string.startsWith("(")) {
				text.delete(0, 1);
				tokenList.add(new LEFTPAREN());
			} else if (string.startsWith(")")) {
				text.delete(0, 1);
				tokenList.add(new RIGHTPAREN());
			} else if (string.startsWith("{")) {
				text.delete(0, 1);
				tokenList.add(new LEFTBRACKET());
			} else if (string.startsWith("}")) {
				text.delete(0, 1);
				tokenList.add(new RIGHTBRACKET());
			} else if (string.startsWith("[")) {
				text.delete(0, 1);
				tokenList.add(new LEFTSQUAREBRACKET());
			} else if (string.startsWith("]")) {
				text.delete(0, 1);
				tokenList.add(new RIGHTSQUAREBRACKET());
			} else if (string.startsWith(",")) {
				text.delete(0, 1);
				tokenList.add(new COMMA());
			} else if (string.startsWith(".")) {
				text.delete(0, 1);
				tokenList.add(new DOT());
			} else if (string.startsWith("\\")) {
				text.delete(0, 1);
				tokenList.add(new BACKSLASH());
			}

			else if ('\"' == string.charAt(0)) {
				this.states.push(STATE.DOUBLEQUOTELITERAL);
				int index = 1;
				LITERAL: while (index < string.length()) {
					if ('\"' == string.charAt(index)) {
						this.states.pop();
						break;
					} else if ('\\' == string.charAt(index)) {
						index++;
						if (index == string.length()) {
							break LITERAL;
						}
					}
					index++;
				}
				final String value = text.substring(1, index);
				text.delete(0, index + 1);
				tokenList.add(new STRINGLITERAL(value));
			}

			else if ('\'' == string.charAt(0)) {
				this.states.push(STATE.SINGLEQUOTELITERAL);
				int index = 1;
				LITERAL: while (index < string.length()) {
					if ('\'' == string.charAt(index)) {
						this.states.pop();
						break;
					} else if ('\\' == string.charAt(index)) {
						index++;
						if (index == string.length()) {
							break LITERAL;
						}
					}
					index++;
				}
				final String value = text.substring(1, index);
				text.delete(0, index + 1);
				tokenList.add(new CHARLITERAL(value));
			}

			else if ('`' == string.charAt(0)) {
				this.states.push(STATE.BACKQUOTELITERAL);
				int index = 1;
				LITERAL: while (index < string.length()) {
					if ('`' == string.charAt(index)) {
						this.states.pop();
						break;
					} else if ('\\' == string.charAt(index)) {
						index++;
						if (index == string.length()) {
							break LITERAL;
						}
					}
					index++;
				}
				final String value = text.substring(1, index);
				text.delete(0, index + 1);
				tokenList.add(new BACKQUOTELITERAL(value));
			}

			else if (isDigit(string.charAt(0))) {
				int index = 1;
				while (index < string.length()) {
					if (!isDigit(string.charAt(index))) {
						break;
					}
					index++;
				}
				text.delete(0, index);
				final String sconstant = string.substring(0, index);
				tokenList.add(new NUMBERLITERAL(sconstant));
			}

			else if (isAlphabet(string.charAt(0)) || ('_' == string.charAt(0))) {
				int index = 1;
				while (index < string.length()) {
					if (!isAlphabet(string.charAt(index))
							&& !isDigit(string.charAt(index))
							&& '_' != string.charAt(index)) {
						break;
					}
					index++;
				}
				text.delete(0, index);
				final String identifier = string.substring(0, index);

				if (identifier.equals("False")) {
					tokenList.add(new FALSE2());
				} else if (identifier.equals("None")) {
					tokenList.add(new NONE());
				} else if (identifier.equals("True")) {
					tokenList.add(new TRUE2());
				} else if (identifier.equals("and")) {
					tokenList.add(new AND2());
				} else if (identifier.equals("assert")) {
					tokenList.add(new ASSERT());
				} else if (identifier.equals("class")) {
					tokenList.add(new CLASS());
				} else if (identifier.equals("continue")) {
					tokenList.add(new CONTINUE());
				} else if (identifier.equals("def")) {
					tokenList.add(new DEF());
				} else if (identifier.equals("del")) {
					tokenList.add(new DEL());
				} else if (identifier.equals("elif")) {
					tokenList.add(new ELIF());
				} else if (identifier.equals("else")) {
					tokenList.add(new ELSE());
				} else if (identifier.equals("except")) {
					tokenList.add(new EXCEPT());
				} else if (identifier.equals("finally")) {
					tokenList.add(new FINALLY());
				} else if (identifier.equals("for")) {
					tokenList.add(new FOR());
				} else if (identifier.equals("from")) {
					tokenList.add(new FROM());
				} else if (identifier.equals("global")) {
					tokenList.add(new GLOBAL());
				} else if (identifier.equals("if")) {
					tokenList.add(new IF());
				} else if (identifier.equals("import")) {
					tokenList.add(new IMPORT());
				} else if (identifier.equals("in")) {
					tokenList.add(new IN());
				} else if (identifier.equals("is")) {
					tokenList.add(new IS());
				} else if (identifier.equals("lambda")) {
					tokenList.add(new LAMBDA());
				} else if (identifier.equals("nonlocal")) {
					tokenList.add(new NONLOCAL());
				} else if (identifier.equals("not")) {
					tokenList.add(new NOT2());
				} else if (identifier.equals("or")) {
					tokenList.add(new OR2());
				} else if (identifier.equals("pass")) {
					tokenList.add(new PASS());
				} else if (identifier.equals("raise")) {
					tokenList.add(new RAISE());
				} else if (identifier.equals("return")) {
					tokenList.add(new RETURN());
				} else if (identifier.equals("try")) {
					tokenList.add(new TRY());
				} else if (identifier.equals("while")) {
					tokenList.add(new WHILE());
				} else if (identifier.equals("with")) {
					tokenList.add(new WITH());
				} else if (identifier.equals("yield")) {
					tokenList.add(new YIELD());
				} else {
					tokenList.add(new IDENTIFIER(identifier));
				}
			}

			else if ('@' == string.charAt(0)) {

				int index = 1;
				while (index < string.length()) {
					if (' ' == string.charAt(index)
							|| '\t' == string.charAt(index)) {

					}
					index++;
				}
				text.delete(0, index);
				final String value = string.substring(0, index);
				tokenList.add(new ANNOTATION(value));
			}

			else if (' ' == string.charAt(0) || '\t' == string.charAt(0)) {
				text.deleteCharAt(0);
			}

			else {
				assert false : "unexpected situation: " + string;
				System.exit(0);
			}

		} else if (STATE.SINGLEQUOTELITERAL == this.states.peek()) {

			int index = 1;
			LITERAL: while (index < string.length()) {
				if ('\'' == string.charAt(index)) {
					this.states.pop();
					break;
				} else if ('\\' == string.charAt(index)) {
					index++;
					if (index == string.length()) {
						break LITERAL;
					}
				}
				index++;
			}
			final String value = text.substring(1, index);
			text.delete(0, index + 1);
			tokenList.add(new CHARLITERAL(value));

		} else if (STATE.DOUBLEQUOTELITERAL == this.states.peek()) {

			int index = 1;
			LITERAL: while (index < string.length()) {
				if ('\"' == string.charAt(index)) {
					this.states.pop();
					break;
				} else if ('\\' == string.charAt(index)) {
					index++;
					if (index == string.length()) {
						break LITERAL;
					}
				}
				index++;
			}
			final String value = text.substring(1, index);
			text.delete(0, index + 1);
			tokenList.add(new STRINGLITERAL(value));
		}

		else {
			assert false : "unexpected situation: " + string;
			System.exit(0);
		}

		this.lex(text, tokenList);
	}

	private static boolean isAlphabet(final char c) {
		return Character.isLowerCase(c) || Character.isUpperCase(c);
	}

	private static boolean isDigit(final char c) {
		return '0' == c || '1' == c || '2' == c || '3' == c || '4' == c
				|| '5' == c || '6' == c || '7' == c || '8' == c || '9' == c;
	}
}
