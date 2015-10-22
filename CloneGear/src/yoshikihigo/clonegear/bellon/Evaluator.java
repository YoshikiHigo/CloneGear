package yoshikihigo.clonegear.bellon;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Evaluator {

	public static void main(final String[] args) {

		EvalConfig.initialize(args);

		final Evaluator evaluator = new Evaluator();
		final String oracleFile = EvalConfig.getInstance().getORACLE();
		final String resultsFile = EvalConfig.getInstance().getRESULTS();
		final float threshold = EvalConfig.getInstance().getTHRESHOLD();
		final List<ClonePair> oracles = evaluator.readFile(oracleFile, true);
		final List<ClonePair> results = evaluator.readFile(resultsFile, false);

		final List<ClonePair> goods = new ArrayList<>();
		final List<ClonePair> oks = new ArrayList<>();
		ORACLE: for (final ClonePair oracle : oracles) {
			CLONEPAIR1: for (final ClonePair clonepair : results) {
				final float good = ClonePair.good(oracle, clonepair);
				if (threshold <= good) {
					goods.add(oracle);
					break CLONEPAIR1;
				}
			}

			CLONEPAIR2: for (final ClonePair clonepair : results) {
				final float ok = ClonePair.ok(oracle, clonepair);
				if (threshold <= ok) {
					oks.add(oracle);
					break CLONEPAIR2;
				}
			}
		}

		evaluator.print(oracles, results, goods, oks);
	}

	private List<ClonePair> readFile(final String path, final boolean isOracle) {

		final List<ClonePair> clonepairs = new ArrayList<>();

		try (final BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(path), "UTF-8"))) {

			while (true) {

				final String line = reader.readLine();
				if (null == line) {
					break;
				}

				final StringTokenizer tokenizer = new StringTokenizer(line,
						"\t");
				if (isOracle) {
					final String id = tokenizer.nextToken();
					final String project = tokenizer.nextToken();
				}
				final String leftPath = tokenizer.nextToken();
				final int leftStartLine = Integer.parseInt(tokenizer
						.nextToken());
				final int leftEndLine = Integer.parseInt(tokenizer.nextToken());
				final String rightPath = tokenizer.nextToken();
				final int rightStartLine = Integer.parseInt(tokenizer
						.nextToken());
				final int rightEndLine = Integer
						.parseInt(tokenizer.nextToken());
				final int type = Integer.parseInt(tokenizer.nextToken());
				if (isOracle) {
					final String leftGaps = tokenizer.nextToken();
					final String rightGaps = tokenizer.nextToken();
				}

				final Clone left = new Clone(leftPath, leftStartLine,
						leftEndLine);
				final Clone right = new Clone(rightPath, rightStartLine,
						rightEndLine);
				final ClonePair clonepair = new ClonePair(left, right, type);
				clonepairs.add(clonepair);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

		return clonepairs;
	}

	private void print(final List<ClonePair> oracles,
			final List<ClonePair> clonepairs, final List<ClonePair> goods,
			final List<ClonePair> oks) {
		System.out
				.println("oracles, clonepairs, good, ok, good-type1, good-type2, good-type3, ok-type1, ok-type2, ok-type3");
		System.out.println(oracles.size() + ", " + clonepairs.size() + ", "
				+ goods.size() + ", " + oks.size() + ", "
				+ this.getSpecifiedTypeClonePairs(goods, 1).size() + ", "
				+ this.getSpecifiedTypeClonePairs(goods, 2).size() + ", "
				+ this.getSpecifiedTypeClonePairs(goods, 3).size() + ", "
				+ this.getSpecifiedTypeClonePairs(oks, 1).size() + ", "
				+ this.getSpecifiedTypeClonePairs(oks, 2).size() + ", "
				+ this.getSpecifiedTypeClonePairs(oks, 3).size());

	}

	private List<ClonePair> getSpecifiedTypeClonePairs(
			final List<ClonePair> clonepairs, final int type) {
		final List<ClonePair> specified = new ArrayList<>();
		for (final ClonePair clonepair : clonepairs) {
			if (clonepair.type == type) {
				specified.add(clonepair);
			}
		}
		return specified;
	}
}
