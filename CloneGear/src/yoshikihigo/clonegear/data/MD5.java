package yoshikihigo.clonegear.data;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import yoshikihigo.clonegear.CGConfig;
import yoshikihigo.clonegear.lexer.token.IDENTIFIER;
import yoshikihigo.clonegear.lexer.token.LEFTPAREN;
import yoshikihigo.clonegear.lexer.token.LITERAL;
import yoshikihigo.clonegear.lexer.token.NORMALIZEDIDENTIFIER;
import yoshikihigo.clonegear.lexer.token.NORMALIZEDLITERAL;
import yoshikihigo.clonegear.lexer.token.Token;

public class MD5 {

	static MD5 makeJCHash(final List<Token> tokens) {

		final List<Token> nonTrivialTokens = Statement.removeJCTrivialTokens(tokens);
		final List<Token> normalizedTokens = new ArrayList<>();
		// final StringBuilder builder = new StringBuilder();
		final Map<String, String> literals = new HashMap<>();
		final Map<String, String> variables = new HashMap<>();
		final Map<String, String> methods = new HashMap<>();
		final String parameterize = CGConfig.getInstance().getPARAMETERIZE();

		for (int index = 0; index < nonTrivialTokens.size(); index++) {

			final Token token = nonTrivialTokens.get(index);

			// in cases of literals
			if (token instanceof LITERAL) {
				final String value = token.value;
				if (parameterize.equals("no")) {
					normalizedTokens.add(token);
				} else if (parameterize.equals("matching")) {
					String normalizedValue = literals.get(value);
					if (null == normalizedValue) {
						normalizedValue = "$L" + variables.size();
						literals.put(value, normalizedValue);
					}
					final Token parameterizedLiteral = new NORMALIZEDLITERAL(normalizedValue);
					parameterizedLiteral.line = token.line;
					parameterizedLiteral.index = token.index;
					normalizedTokens.add(parameterizedLiteral);
				} else if (parameterize.equals("simple")) {
					final Token parameterizedLiteral = new NORMALIZEDLITERAL("$L0");
					parameterizedLiteral.line = token.line;
					parameterizedLiteral.index = token.index;
					normalizedTokens.add(parameterizedLiteral);
				}
			}

			// in case of identifiers
			else if (token instanceof IDENTIFIER) {

				if (nonTrivialTokens.size() == (index + 1) || !(nonTrivialTokens.get(index + 1) instanceof LEFTPAREN)) {
					final String name = token.value;
					if (parameterize.equals("no")) {
						normalizedTokens.add(token);
					} else if (parameterize.equals("matching")) {
						String normalizedName = variables.get(name);
						if (null == normalizedName) {
							normalizedName = "$V" + variables.size();
							variables.put(name, normalizedName);
						}
						final Token normalizedIdentifier = new NORMALIZEDIDENTIFIER(normalizedName);
						normalizedIdentifier.line = token.line;
						normalizedIdentifier.index = token.index;
						normalizedTokens.add(normalizedIdentifier);
					} else if (parameterize.equals("simple")) {
						final Token normalizedIdentifier = new NORMALIZEDIDENTIFIER("$L0");
						normalizedIdentifier.line = token.line;
						normalizedIdentifier.index = token.index;
						normalizedTokens.add(normalizedIdentifier);
					}
				}

				else {

					normalizedTokens.add(token); // do not parameterize function/method names
					// final String name = token.value;
					// if (parameterize.equals("no")) {
					// builder.append(name);
					// } else if (parameterize.equals("matching")) {
					// String normalizedName = methods.get(name);
					// if (null == normalizedName) {
					// normalizedName = "$F" + methods.size();
					// methods.put(name, normalizedName);
					// }
					// builder.append(normalizedName);
					// } else if (parameterize.equals("simple")) {
					// builder.append("$F");
					// }
				}
			}

			// for other tokens
			else {
				normalizedTokens.add(token);
			}
		}

		final String text = String.join(" ", normalizedTokens.stream().map(t -> t.value).collect(Collectors.toList()));
		System.out.println(text);
		final MD5 md5 = MD5.getMD5(text);
		tokenToMD5.put(normalizedTokens, md5);
		md5ToToken.put(md5, normalizedTokens);
		return md5;
	}

	static MD5 makePYHash(final List<Token> tokens) {

		final List<Token> nonTrivialTokens = Statement.removePYTrivialTokens(tokens);
		final StringBuilder builder = new StringBuilder();
		final Map<String, String> identifiers = new HashMap<>();
		final String parameterize = CGConfig.getInstance().getPARAMETERIZE();

		for (int index = 0; index < nonTrivialTokens.size(); index++) {

			final Token token = nonTrivialTokens.get(index);

			if (token instanceof IDENTIFIER) {

				if (nonTrivialTokens.size() == (index + 1) || !(nonTrivialTokens.get(index + 1) instanceof LEFTPAREN)) {
					final String name = token.value;
					if (parameterize.equals("no")) {
						builder.append(name);
					} else if (parameterize.equals("matching")) {
						String normalizedName = identifiers.get(name);
						if (null == normalizedName) {
							normalizedName = "$" + identifiers.size();
							identifiers.put(name, normalizedName);
						}
						builder.append(normalizedName);
					} else if (parameterize.equals("simple")) {
						builder.append("$");
					}
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
		final MD5 md5 = MD5.getMD5(text);
		tokenToMD5.put(nonTrivialTokens, md5);
		md5ToToken.put(md5, nonTrivialTokens);
		return md5;
	}

	static MD5 makePHPHash(final List<Token> tokens) {

		final StringBuilder builder = new StringBuilder();
		final Map<String, String> identifiers = new HashMap<>();
		final String parameterize = CGConfig.getInstance().getPARAMETERIZE();

		for (int index = 0; index < tokens.size(); index++) {

			final Token token = tokens.get(index);

			if (token instanceof IDENTIFIER) {

				if (tokens.size() == (index + 1) || !(tokens.get(index + 1) instanceof LEFTPAREN)) {
					final String name = token.value;
					if (parameterize.equals("no")) {
						builder.append(name);
					} else if (parameterize.equals("matching")) {
						String normalizedName = identifiers.get(name);
						if (null == normalizedName) {
							normalizedName = "$" + identifiers.size();
							identifiers.put(name, normalizedName);
						}
						builder.append(normalizedName);
					} else if (parameterize.equalsIgnoreCase("simple")) {
						builder.append("$");
					}
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
		final MD5 md5 = MD5.getMD5(text);
		tokenToMD5.put(tokens, md5);
		md5ToToken.put(md5, tokens);
		return md5;
	}

	static public MD5 getMD5(final String text) {
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			final byte[] data = text.getBytes("UTF-8");
			md.update(data);
			final byte[] digest = md.digest();
			return new MD5(digest);
		} catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return new MD5(new byte[0]);
		}
	}

	static private Map<List<Token>, MD5> tokenToMD5 = new HashMap<>();
	static private Map<MD5, List<Token>> md5ToToken = new HashMap<>();

	static public List<Token> getTokens(final MD5 md5) {
		return md5ToToken.get(md5);
	}

	final public byte[] value;

	private MD5(final byte[] value) {
		this.value = value;
	}
}
