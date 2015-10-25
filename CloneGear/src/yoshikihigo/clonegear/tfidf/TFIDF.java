package yoshikihigo.clonegear.tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import yoshikihigo.clonegear.data.CloneData;
import yoshikihigo.clonegear.lexer.token.Token;

public class TFIDF<T extends CloneData> {

	private final List<T> clones;
	private final Map<NGram, Double> IDFCACHE;
	private final Map<T, Map<NGram, Double>> TFIDFVectorCACHE;
	private final int N;

	public TFIDF(final List<T> clones) {
		this.clones = clones;
		this.IDFCACHE = new HashMap<>();
		this.TFIDFVectorCACHE = new HashMap<>();
		this.N = 5;
	}

	public List<NGram> getNGrams(final T clone) {

		final List<NGram> grams = new ArrayList<>();

		for (int index = 0; (index + N - 1) < clone.tokens.size(); index++) {
			final Token[] gram = new Token[N];
			for (int offset = 0; offset < N; offset++) {
				gram[offset] = clone.tokens.get(index + offset);
			}
			grams.add(new NGram(gram));
		}

		return grams;
	}

	public double getTF(final NGram ngram, final T clone) {
		assert ngram.tokens.length == N : "illegal state.";
		final List<NGram> ngrams = this.getNGrams(clone);
		int count = 0;
		for (final NGram g : ngrams) {
			if (ngram.equals(g)) {
				count++;
			}
		}

		return (double) count / (double) ngrams.size();
	}

	public double getIDF(final NGram ngram) {

		Double value = this.IDFCACHE.get(ngram);
		if (null != value) {
			return value.doubleValue();
		}

		int count = 0;
		final int n = ngram.tokens.length;
		for (final T clone : this.clones) {
			CLONE: for (int index = 0; (index + n - 1) < clone.tokens.size(); index++) {
				for (int offset = 0; offset < n; offset++) {
					if (ngram.tokens[offset].getClass() != clone.tokens.get(
							index + offset).getClass()) {
						continue CLONE;
					}
				}
				count++;
			}
		}

		value = Double.valueOf(Math.log1p((double) count
				/ (double) clones.size()));
		this.IDFCACHE.put(ngram, value);

		return value;
	}

	public double getTFIDF(final NGram ngram, final T clone) {

		final double tf = this.getTF(ngram, clone);
		final double idf = this.getIDF(ngram);
		return tf * idf;
	}

	public Map<NGram, Double> getTFIDFVector(final T clone) {

		Map<NGram, Double> vector = TFIDFVectorCACHE.get(clone);
		if (null != vector) {
			return vector;
		}

		vector = new HashMap<>();
		final List<NGram> ngrams = getNGrams(clone);
		for (final NGram ngram : ngrams) {
			if (!vector.containsKey(ngram)) {
				final double value = getTFIDF(ngram, clone);
				vector.put(ngram, value);
			}
		}
		TFIDFVectorCACHE.put(clone, vector);

		return vector;
	}

	public double getSIM(final T clone1, final T clone2) {

		final Map<NGram, Double> vector1 = this.getTFIDFVector(clone1);
		final Map<NGram, Double> vector2 = this.getTFIDFVector(clone2);

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

	public double getNSIM(final T clone1, final T clone2) {

		final double sim = getSIM(clone1, clone2);
		if (0d == sim) {
			return 0d;
		}

		final Map<NGram, Double> vector1 = getTFIDFVector(clone1);
		final Map<NGram, Double> vector2 = getTFIDFVector(clone2);
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
