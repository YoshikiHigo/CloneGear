package yoshikihigo.clonegear.data;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.lexer.token.IDENTIFIER;
import yoshikihigo.clonegear.lexer.token.LEFTPAREN;
import yoshikihigo.clonegear.lexer.token.Token;

public class MD5 {

	static MD5 makeJCHash(final List<Token> tokens) {

		final List<Token> nonTrivialTokens = Statement
				.removeJCTrivialTokens(tokens);
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
		final byte[] value = MD5.getMD5(text);
		final MD5 md5 = new MD5(value);
		tokenToMD5.put(nonTrivialTokens, md5);
		md5ToToken.put(md5, nonTrivialTokens);
		return md5;
	}

	static MD5 makePYHash(final List<Token> tokens) {

		final List<Token> nonTrivialTokens = Statement
				.removePYTrivialTokens(tokens);
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
		final byte[] value = MD5.getMD5(text);
		final MD5 md5 = new MD5(value);
		tokenToMD5.put(nonTrivialTokens, md5);
		md5ToToken.put(md5, nonTrivialTokens);
		return md5;
	}

	static private byte[] getMD5(final String text) {
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
