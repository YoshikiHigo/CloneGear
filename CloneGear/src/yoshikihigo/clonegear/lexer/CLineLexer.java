package yoshikihigo.clonegear.lexer;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.lexer.token.AND;
import yoshikihigo.clonegear.lexer.token.ASM;
import yoshikihigo.clonegear.lexer.token.ASSIGN;
import yoshikihigo.clonegear.lexer.token.AUTO;
import yoshikihigo.clonegear.lexer.token.BREAK;
import yoshikihigo.clonegear.lexer.token.CASE;
import yoshikihigo.clonegear.lexer.token.CHAR;
import yoshikihigo.clonegear.lexer.token.CHARLITERAL;
import yoshikihigo.clonegear.lexer.token.COLON;
import yoshikihigo.clonegear.lexer.token.COMMA;
import yoshikihigo.clonegear.lexer.token.CONST;
import yoshikihigo.clonegear.lexer.token.CONTINUE;
import yoshikihigo.clonegear.lexer.token.DECREMENT;
import yoshikihigo.clonegear.lexer.token.DEFAULT;
import yoshikihigo.clonegear.lexer.token.DIVIDE;
import yoshikihigo.clonegear.lexer.token.DIVIDEEQUAL;
import yoshikihigo.clonegear.lexer.token.DO;
import yoshikihigo.clonegear.lexer.token.DOT;
import yoshikihigo.clonegear.lexer.token.DOUBLE;
import yoshikihigo.clonegear.lexer.token.ELSE;
import yoshikihigo.clonegear.lexer.token.ENDASM;
import yoshikihigo.clonegear.lexer.token.ENTRY;
import yoshikihigo.clonegear.lexer.token.ENUM;
import yoshikihigo.clonegear.lexer.token.EQUAL;
import yoshikihigo.clonegear.lexer.token.EXTERN;
import yoshikihigo.clonegear.lexer.token.FLOAT;
import yoshikihigo.clonegear.lexer.token.FOR;
import yoshikihigo.clonegear.lexer.token.GOTO;
import yoshikihigo.clonegear.lexer.token.GREAT;
import yoshikihigo.clonegear.lexer.token.GREATEQUAL;
import yoshikihigo.clonegear.lexer.token.IDENTIFIER;
import yoshikihigo.clonegear.lexer.token.IF;
import yoshikihigo.clonegear.lexer.token.INCREMENT;
import yoshikihigo.clonegear.lexer.token.INT;
import yoshikihigo.clonegear.lexer.token.LEFTBRACKET;
import yoshikihigo.clonegear.lexer.token.LEFTPAREN;
import yoshikihigo.clonegear.lexer.token.LEFTSQUAREBRACKET;
import yoshikihigo.clonegear.lexer.token.LESS;
import yoshikihigo.clonegear.lexer.token.LESSEQUAL;
import yoshikihigo.clonegear.lexer.token.LONG;
import yoshikihigo.clonegear.lexer.token.MINUS;
import yoshikihigo.clonegear.lexer.token.MINUSEQUAL;
import yoshikihigo.clonegear.lexer.token.MOD;
import yoshikihigo.clonegear.lexer.token.MODEQUAL;
import yoshikihigo.clonegear.lexer.token.NULL;
import yoshikihigo.clonegear.lexer.token.NUMBERLITERAL;
import yoshikihigo.clonegear.lexer.token.OR;
import yoshikihigo.clonegear.lexer.token.PLUS;
import yoshikihigo.clonegear.lexer.token.PLUSEQUAL;
import yoshikihigo.clonegear.lexer.token.QUESTION;
import yoshikihigo.clonegear.lexer.token.REGISTER;
import yoshikihigo.clonegear.lexer.token.RETURN;
import yoshikihigo.clonegear.lexer.token.RIGHTARROW;
import yoshikihigo.clonegear.lexer.token.RIGHTBRACKET;
import yoshikihigo.clonegear.lexer.token.RIGHTPAREN;
import yoshikihigo.clonegear.lexer.token.RIGHTSQUAREBRACKET;
import yoshikihigo.clonegear.lexer.token.SEMICOLON;
import yoshikihigo.clonegear.lexer.token.SHARP;
import yoshikihigo.clonegear.lexer.token.SHORT;
import yoshikihigo.clonegear.lexer.token.SIGNED;
import yoshikihigo.clonegear.lexer.token.SIZEOF;
import yoshikihigo.clonegear.lexer.token.STAR;
import yoshikihigo.clonegear.lexer.token.STAREQUAL;
import yoshikihigo.clonegear.lexer.token.STATIC;
import yoshikihigo.clonegear.lexer.token.STRINGLITERAL;
import yoshikihigo.clonegear.lexer.token.STRUCT;
import yoshikihigo.clonegear.lexer.token.SWITCH;
import yoshikihigo.clonegear.lexer.token.TYPEDEF;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.clonegear.lexer.token.UNION;
import yoshikihigo.clonegear.lexer.token.UNSIGNED;
import yoshikihigo.clonegear.lexer.token.VOID;
import yoshikihigo.clonegear.lexer.token.VOLATILE;
import yoshikihigo.clonegear.lexer.token.WHILE;

public class CLineLexer implements LineLexer {

	@Override
	public List<Token> lexFile(final String text) {

		final List<Token> tokens = new ArrayList<Token>();
		try (final LineNumberReader reader = new LineNumberReader(
				new StringReader(text));) {

			String line;
			final CLineLexer lexer = new CLineLexer();
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

	@Override
	public List<Token> lexLine(final String line) {
		final List<Token> tokens = new ArrayList<Token>();
		this.lex(new StringBuilder(line), tokens);
		return tokens;
	}

	private void lex(final StringBuilder text, final List<Token> tokenList) {

		if (0 == text.length()) {
			return;
		}

		final String string = text.toString();
		if (string.startsWith("-=")) {
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
		} else if (string.startsWith("->")) {
			text.delete(0, 2);
			tokenList.add(new RIGHTARROW());
		}
		// } else if (string.startsWith("!")) {
		// text.delete(0, 1);
		// tokenList.add(new NOT());
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
		} else if (string.startsWith("#")) {
			text.delete(0, 1);
			tokenList.add(new SHARP());
		}

		else if ('\"' == string.charAt(0)) {
			int index = 1;
			while (index < string.length()) {
				if ('\"' == string.charAt(index)) {
					break;
				}
				index++;
			}
			final String value = text.substring(1, index);
			text.delete(0, index + 1);
			tokenList.add(new STRINGLITERAL(value));
		}

		else if ('\'' == string.charAt(0)) {
			int index = 1;
			while (index < string.length()) {
				if ('\'' == string.charAt(index)) {
					break;
				}
				index++;
			}
			final String value = text.substring(1, index);
			text.delete(0, index + 1);
			tokenList.add(new CHARLITERAL(value));
		}

		else if ('/' == string.charAt(0)) {

			if ((2 <= string.length()) && ('/' == string.charAt(1))) {
				return;
			}
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

		else if (isAlphabet(string.charAt(0))) {
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

			if (identifier.equals("asm")) {
				tokenList.add(new ASM());
			} else if (identifier.equals("auto")) {
				tokenList.add(new AUTO());
			} else if (identifier.equals("break")) {
				tokenList.add(new BREAK());
			} else if (identifier.equals("case")) {
				tokenList.add(new CASE());
			} else if (identifier.equals("char")) {
				tokenList.add(new CHAR());
			} else if (identifier.equals("const")) {
				tokenList.add(new CONST());
			} else if (identifier.equals("continue")) {
				tokenList.add(new CONTINUE());
			} else if (identifier.equals("default")) {
				tokenList.add(new DEFAULT());
			} else if (identifier.equals("do")) {
				tokenList.add(new DO());
			} else if (identifier.equals("double")) {
				tokenList.add(new DOUBLE());
			} else if (identifier.equals("else")) {
				tokenList.add(new ELSE());
			} else if (identifier.equals("endasm")) {
				tokenList.add(new ENDASM());
			} else if (identifier.equals("entry")) {
				tokenList.add(new ENTRY());
			} else if (identifier.equals("enum")) {
				tokenList.add(new ENUM());
			} else if (identifier.equals("extern")) {
				tokenList.add(new EXTERN());
			} else if (identifier.equals("float")) {
				tokenList.add(new FLOAT());
			} else if (identifier.equals("for")) {
				tokenList.add(new FOR());
			} else if (identifier.equals("goto")) {
				tokenList.add(new GOTO());
			} else if (identifier.equals("if")) {
				tokenList.add(new IF());
			} else if (identifier.equals("int")) {
				tokenList.add(new INT());
			} else if (identifier.equals("long")) {
				tokenList.add(new LONG());
			} else if (identifier.equals("NULL")) {
				tokenList.add(new NULL());
			} else if (identifier.equals("register")) {
				tokenList.add(new REGISTER());
			} else if (identifier.equals("return")) {
				tokenList.add(new RETURN());
			} else if (identifier.equals("short")) {
				tokenList.add(new SHORT());
			} else if (identifier.equals("signed")) {
				tokenList.add(new SIGNED());
			} else if (identifier.equals("sizeof")) {
				tokenList.add(new SIZEOF());
			} else if (identifier.equals("static")) {
				tokenList.add(new STATIC());
			} else if (identifier.equals("struct")) {
				tokenList.add(new STRUCT());
			} else if (identifier.equals("switch")) {
				tokenList.add(new SWITCH());
			} else if (identifier.equals("typedef")) {
				tokenList.add(new TYPEDEF());
			} else if (identifier.equals("union")) {
				tokenList.add(new UNION());
			} else if (identifier.equals("unsigned")) {
				tokenList.add(new UNSIGNED());
			} else if (identifier.equals("void")) {
				tokenList.add(new VOID());
			} else if (identifier.equals("volatile")) {
				tokenList.add(new VOLATILE());
			} else if (identifier.equals("while")) {
				tokenList.add(new WHILE());
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

		lex(text, tokenList);
	}

	private static boolean isAlphabet(final char c) {
		return Character.isLowerCase(c) || Character.isUpperCase(c);
	}

	private static boolean isDigit(final char c) {
		return '0' == c || '1' == c || '2' == c || '3' == c || '4' == c
				|| '5' == c || '6' == c || '7' == c || '8' == c || '9' == c;
	}
}
