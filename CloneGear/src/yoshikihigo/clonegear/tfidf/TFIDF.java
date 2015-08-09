package yoshikihigo.clonegear.tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import yoshikihigo.clonegear.lexer.token.Token;

public class TFIDF {

	static public List<NGram> getNGrams(final List<Token> tokens, final int n) {

		final List<NGram> grams = new ArrayList<>();

		for (int index = 0; (index + n - 1) < tokens.size(); index++) {
			final Token[] gram = new Token[n];
			for (int offset = 0; offset < n; offset++) {
				gram[offset] = tokens.get(index + offset);
			}
			grams.add(new NGram(gram));
		}

		return grams;
	}

	static public double getTF(final NGram ngram, final List<Token> clone) {
		final int n = ngram.tokens.length;
		final List<NGram> ngrams = getNGrams(clone, n);
		int count = 0;
		for (final NGram g : ngrams) {
			if (ngram.equals(g)) {
				count++;
			}
		}

		return (double) count / (double) ngrams.size();
	}

	static private final Map<List<List<Token>>, Map<NGram, Double>> CACHE = new HashMap<>();

	static public double getIDF(final NGram ngram,
			final List<List<Token>> clonesets) {

		Map<NGram, Double> values = CACHE.get(clonesets);
		if (null == values) {
			values = new HashMap<NGram, Double>();
			CACHE.put(clonesets, values);
		}
		Double value = values.get(ngram);
		if (null != value) {
			return value.doubleValue();
		}

		int count = 0;
		final int n = ngram.tokens.length;
		for (final List<Token> clone : clonesets) {
			CLONE: for (int index = 0; (index + n - 1) < clone.size(); index++) {
				for (int offset = 0; offset < n; offset++) {
					if (ngram.tokens[offset].getClass() != clone.get(
							index + offset).getClass()) {
						continue CLONE;
					}
				}
				count++;
			}
		}

		value = Double.valueOf(Math.log1p((double) count
				/ (double) clonesets.size()));
		values.put(ngram, value);

		return value;
	}

	static public double getTFIDF(final NGram ngram, final List<Token> clone,
			final List<List<Token>> clonesets) {

		final double tf = getTF(ngram, clone);
		final double idf = getIDF(ngram, clonesets);
		return tf * idf;
	}

	static public Map<NGram, Double> getTFIDFVector(final List<Token> clone,
			final List<List<Token>> clonesets) {

		final Map<NGram, Double> vector = new HashMap<>();

		final int N = 5;
		final List<NGram> ngrams = getNGrams(clone, N);
		for (final NGram ngram : ngrams) {
			if (!vector.containsKey(ngram)) {
				final double value = getTFIDF(ngram, clone, clonesets);
				vector.put(ngram, value);
			}
		}

		return vector;
	}

	static public double getSIM(final List<Token> clone1,
			final List<Token> clone2, List<List<Token>> clonesets) {

		final Map<NGram, Double> vector1 = getTFIDFVector(clone1, clonesets);
		final Map<NGram, Double> vector2 = getTFIDFVector(clone2, clonesets);

		double sim = 0d;
		for (final Entry<NGram, Double> entry : vector1.entrySet()) {
			final NGram ngram = entry.getKey();
			final Double value1 = entry.getValue();
			if (vector2.containsKey(ngram)) {
				final Double value2 = vector2.get(ngram);
				sim += value1 * value2;
			}
		}

		return sim;
	}

	static public double getNSIM(final List<Token> clone1,
			final List<Token> clone2, final List<List<Token>> clonesets) {

		final double sim = getSIM(clone1, clone2, clonesets);
		if (0d == sim) {
			return 0d;
		}

		final Map<NGram, Double> vector1 = getTFIDFVector(clone1, clonesets);
		final Map<NGram, Double> vector2 = getTFIDFVector(clone2, clonesets);
		final double absoluteValue1 = getAbsoluteValue(vector1);
		final double absoluteValue2 = getAbsoluteValue(vector2);

		return sim / (absoluteValue1 * absoluteValue2);
	}

	static public double getAbsoluteValue(final Map<NGram, Double> vector) {
		double total = 0d;
		for (final Double value : vector.values()) {
			total += Math.pow(value, 2);
		}
		return Math.sqrt(total);
	}
}
