package yoshikihigo.clonegear.lexer;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import yoshikihigo.clonegear.CGConfig;
import yoshikihigo.clonegear.lexer.token.*;

public class PHPLineLexer implements LineLexer {

	enum STATE {
		CODE, SINGLEQUOTELITERAL, DOUBLEQUOTELITERAL;
	}

	@Override
	public List<Token> lexFile(final String text) {

		final List<Token> tokens = new ArrayList<Token>();

		try (final LineNumberReader reader = new LineNumberReader(
				new StringReader(text))) {

			String line;
			final PHPLineLexer lexer = new PHPLineLexer();
			while (null != (line = reader.readLine())) {
				for (final Token t : lexer.lexLine(line)) {
					t.line = reader.getLineNumber();
					tokens.add(t);
				}
			}

			if (CGConfig.getInstance().isDEBUG()) {
				for (final Token t : tokens) {
					final String name = t.getClass().getName();
					final String className = name.substring(name
							.lastIndexOf('.') + 1);
					System.out.println(className + " : " + t.value);
				}
			}

		} catch (final Exception e) {
			System.err.println(e.getMessage());
		}

		return tokens;
	}

	final private Stack<STATE> states;

	public PHPLineLexer() {
		this.states = new Stack<STATE>();
		this.states.push(STATE.CODE);
	}

	public List<Token> lexLine(final String line) {

		final List<Token> tokenList = new ArrayList<Token>();
		final StringBuilder text = new StringBuilder(line);

		while (0 < text.length()) {

			final String string = text.toString();

			if (STATE.CODE == this.states.peek()) {

				if (string.startsWith("<<=")) {
					text.delete(0, 3);
					tokenList.add(new LEFTSHIFTEQUAL());
				} else if (string.startsWith(">>=")) {
					text.delete(0, 3);
					tokenList.add(new RIGHTSHIFTEQUAL());
				} else if (string.startsWith("===")) {
					text.delete(0, 3);
					tokenList.add(new EQUAL2());
				} else if (string.startsWith("!==")) {
					text.delete(0, 3);
					tokenList.add(new NOTEQUAL3());
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
				} else if (string.startsWith(".=")) {
					text.delete(0, 2);
					tokenList.add(new DOTEQUAL());
				} else if (string.startsWith("++")) {
					text.delete(0, 2);
					tokenList.add(new INCREMENT());
				} else if (string.startsWith("--")) {
					text.delete(0, 2);
					tokenList.add(new DECREMENT());
				} else if (string.startsWith("<=")) {
					text.delete(0, 2);
					tokenList.add(new LESSEQUAL());
				} else if (string.startsWith(">=")) {
					text.delete(0, 2);
					tokenList.add(new GREATEQUAL());
				} else if (string.startsWith("==")) {
					text.delete(0, 2);
					tokenList.add(new EQUAL());
				} else if (string.startsWith("!=")) {
					text.delete(0, 2);
					tokenList.add(new NOTEQUAL());
				} else if (string.startsWith("&=")) {
					text.delete(0, 2);
					tokenList.add(new ANDEQUAL());
				} else if (string.startsWith("|=")) {
					text.delete(0, 2);
					tokenList.add(new OREQUAL());
				} else if (string.startsWith("^=")) {
					text.delete(0, 2);
					tokenList.add(new EXCLUSIVEOREQUAL());
				} else if (string.startsWith("&&")) {
					text.delete(0, 2);
					tokenList.add(new ANDAND());
				} else if (string.startsWith("||")) {
					text.delete(0, 2);
					tokenList.add(new OROR());
				} else if (string.startsWith("<<")) {
					text.delete(0, 2);
					tokenList.add(new LEFTSHIFT());
				} else if (string.startsWith(">>")) {
					text.delete(0, 2);
					tokenList.add(new RIGHTSHIFT());
				} else if (string.startsWith("->")) {
					text.delete(0, 2);
					tokenList.add(new RIGHTARROW());
				}

				else if (string.startsWith("!")) {
					text.delete(0, 1);
					tokenList.add(new NOT());
				} else if (string.startsWith(":")) {
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
				} else if (string.startsWith("^")) {
					text.delete(0, 1);
					tokenList.add(new EXCLUSIVEOR());
				} else if (string.startsWith("~")) {
					text.delete(0, 1);
					tokenList.add(new TILDA());
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

				else if (string.startsWith("0x")) {
					int index = 2;
					while (index < string.length()) {
						if ((!isDigit(string.charAt(index)))
								&& (!isAlphabet(string.charAt(index)))) {
							break;
						}
						index++;
					}
					text.delete(0, index);
					final String sconstant = string.substring(0, index);
					tokenList.add(new NUMBERLITERAL(sconstant));
				}

				else if (string.startsWith("0b")) {
					int index = 2;
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

				else if (isAlphabet(string.charAt(0))
						|| '_' == string.charAt(0) || '$' == string.charAt(0)) {
					int index = 1;
					while (index < string.length()) {
						if (!isAlphabet(string.charAt(index))
								&& !isDigit(string.charAt(index))
								&& '_' != string.charAt(index)
								&& '$' != string.charAt(index)) {
							break;
						}
						index++;
					}
					text.delete(0, index);
					final String identifier = string.substring(0, index);

					if (identifier.equals("abstract")) {
						tokenList.add(new ABSTRACT());
					} else if (identifier.equals("and")) {
						tokenList.add(new AND2());
					} else if (identifier.equals("array")) {
						tokenList.add(new ARRAY());
					} else if (identifier.equals("as")) {
						tokenList.add(new AS());
					} else if (identifier.equals("break")) {
						tokenList.add(new BREAK());
					} else if (identifier.equals("callable")) {
						tokenList.add(new CALLABLE());
					} else if (identifier.equals("case")) {
						tokenList.add(new CASE());
					} else if (identifier.equals("catch")) {
						tokenList.add(new CATCH());
					} else if (identifier.equals("class")) {
						tokenList.add(new CLASS());
					} else if (identifier.equals("clone")) {
						tokenList.add(new CLONE());
					} else if (identifier.equals("const")) {
						tokenList.add(new CONST());
					} else if (identifier.equals("continue")) {
						tokenList.add(new CONTINUE());
					} else if (identifier.equals("declare")) {
						tokenList.add(new DECLARE());
					} else if (identifier.equals("default")) {
						tokenList.add(new DEFAULT());
					} else if (identifier.equals("die")) {
						tokenList.add(new DIE());
					} else if (identifier.equals("do")) {
						tokenList.add(new DO());
					} else if (identifier.equals("echo")) {
						tokenList.add(new ECHO());
					} else if (identifier.equals("else")) {
						tokenList.add(new ELSE());
					} else if (identifier.equals("elseif")) {
						tokenList.add(new ELSEIF());
					} else if (identifier.equals("empty")) {
						tokenList.add(new EMPTY());
					} else if (identifier.equals("enddeclare")) {
						tokenList.add(new ENDDECLARE());
					} else if (identifier.equals("endfor")) {
						tokenList.add(new ENDFOR());
					} else if (identifier.equals("endforeach")) {
						tokenList.add(new ENDFOREACH());
					} else if (identifier.equals("endif")) {
						tokenList.add(new ENDIF());
					} else if (identifier.equals("endswitch")) {
						tokenList.add(new ENDSWITCH());
					} else if (identifier.equals("endwhile")) {
						tokenList.add(new ENDWHILE());
					} else if (identifier.equals("eval")) {
						tokenList.add(new EVAL());
					} else if (identifier.equals("exit")) {
						tokenList.add(new EXIT());
					} else if (identifier.equals("extends")) {
						tokenList.add(new EXTENDS());
					} else if (identifier.equals("final")) {
						tokenList.add(new FINAL());
					} else if (identifier.equals("finally")) {
						tokenList.add(new FINALLY());
					} else if (identifier.equals("for")) {
						tokenList.add(new FOR());
					} else if (identifier.equals("foreach")) {
						tokenList.add(new FOREACH());
					} else if (identifier.equals("function")) {
						tokenList.add(new FUNCTION());
					} else if (identifier.equals("global")) {
						tokenList.add(new GLOBAL());
					} else if (identifier.equals("goto")) {
						tokenList.add(new GOTO());
					} else if (identifier.equals("if")) {
						tokenList.add(new IF());
					} else if (identifier.equals("implements")) {
						tokenList.add(new IMPLEMENTS());
					} else if (identifier.equals("include")) {
						tokenList.add(new INCLUDE());
					} else if (identifier.equals("include_once")) {
						tokenList.add(new INCLUDEONCE());
					} else if (identifier.equals("instanceof")) {
						tokenList.add(new INSTANCEOF());
					} else if (identifier.equals("insteadof")) {
						tokenList.add(new INSTEADOF());
					} else if (identifier.equals("interface")) {
						tokenList.add(new INTERFACE());
					} else if (identifier.equals("isset")) {
						tokenList.add(new ISSET());
					} else if (identifier.equals("list")) {
						tokenList.add(new LIST());
					} else if (identifier.equals("namespace")) {
						tokenList.add(new NAMESPACE());
					} else if (identifier.equals("new")) {
						tokenList.add(new NEW());
					} else if (identifier.equals("or")) {
						tokenList.add(new OR2());
					} else if (identifier.equals("print")) {
						tokenList.add(new PRINT());
					} else if (identifier.equals("private")) {
						tokenList.add(new PRIVATE());
					} else if (identifier.equals("protected")) {
						tokenList.add(new PROTECTED());
					} else if (identifier.equals("public")) {
						tokenList.add(new PUBLIC());
					} else if (identifier.equals("require")) {
						tokenList.add(new REQUIRE());
					} else if (identifier.equals("require_once")) {
						tokenList.add(new REQUIREONCE());
					} else if (identifier.equals("return")) {
						tokenList.add(new RETURN());
					} else if (identifier.equals("static")) {
						tokenList.add(new STATIC());
					} else if (identifier.equals("switch")) {
						tokenList.add(new SWITCH());
					} else if (identifier.equals("throw")) {
						tokenList.add(new THROW());
					} else if (identifier.equals("trait")) {
						tokenList.add(new TRAIT());
					} else if (identifier.equals("try")) {
						tokenList.add(new TRY());
					} else if (identifier.equals("unset")) {
						tokenList.add(new UNSET());
					} else if (identifier.equals("use")) {
						tokenList.add(new USE());
					} else if (identifier.equals("var")) {
						tokenList.add(new VAR());
					} else if (identifier.equals("while")) {
						tokenList.add(new WHILE());
					} else if (identifier.equals("xor")) {
						tokenList.add(new XOR());
					} else if (identifier.equals("yield")) {
						tokenList.add(new YIELD());
					} else if (identifier.equals("__halt_compiler")) {
						tokenList.add(new HALTCOMPILER());
					} else if (identifier.equals("__CLASS__")) {
						tokenList.add(new UNDERSCORE_CLASS());
					} else if (identifier.equals("__DIR__")) {
						tokenList.add(new UNDERSCORE_DIR());
					} else if (identifier.equals("__FILE__")) {
						tokenList.add(new UNDERSCORE_FILE());
					} else if (identifier.equals("__FUNCTION__")) {
						tokenList.add(new UNDERSCORE_FUNCTION());
					} else if (identifier.equals("__LINE__")) {
						tokenList.add(new UNDERSCORE_LINE());
					} else if (identifier.equals("__METHOD__")) {
						tokenList.add(new UNDERSCORE_METHOD());
					} else if (identifier.equals("__NAMESPACE__")) {
						tokenList.add(new UNDERSCORE_NAMESPACE());
					} else if (identifier.equals("__TRAIT__")) {
						tokenList.add(new UNDERSCORE_TRAIT());
					} else {
						tokenList.add(new IDENTIFIER(identifier));
					}
				}

				else if (' ' == string.charAt(0) || '\t' == string.charAt(0)) {
					text.deleteCharAt(0);
				}

				else {
					// assert false : "unexpected situation: " + string;
					text.delete(0, 1);
				}
			}

			else if (STATE.SINGLEQUOTELITERAL == this.states.peek()) {

				int index = 0;
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
				final String value = text.substring(0, index);
				text.delete(0, index + 1);
				tokenList.add(new CHARLITERAL(value));

			} else if (STATE.DOUBLEQUOTELITERAL == this.states.peek()) {

				int index = 0;
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
				final String value = text.substring(0, index);
				text.delete(0, index + 1);
				tokenList.add(new STRINGLITERAL(value));
			}

			else {
				System.err.println("unexpected situation: " + string);
			}
		}

		return tokenList;
	}

	private static boolean isAlphabet(final char c) {
		return Character.isLowerCase(c) || Character.isUpperCase(c);
	}

	private static boolean isDigit(final char c) {
		return '0' == c || '1' == c || '2' == c || '3' == c || '4' == c
				|| '5' == c || '6' == c || '7' == c || '8' == c || '9' == c;
	}
}
