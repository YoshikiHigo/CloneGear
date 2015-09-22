package yoshikihigo.clonegear;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import yoshikihigo.clonegear.data.CloneHash;
import yoshikihigo.clonegear.data.CloneSet;
import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.JavascriptFile;
import yoshikihigo.clonegear.data.Separator;
import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.data.Statement;
import yoshikihigo.clonegear.data.WebFile;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.clonegear.tfidf.TFIDF;

public class CGFinder {

	public static void main(final String[] args) {

		CGConfig.initialize(args);

		final long startTime = System.nanoTime();
		final List<SourceFile> files = getFiles();
		final long middleTime = System.nanoTime();
		final List<CloneSet> clonesets = detectClones(files);
		final long middleTime2 = System.nanoTime();
		final Map<CloneSet, Map<CloneSet, Double>> similarities = new HashMap<>();
		final List<CloneSet> filteredClonesets = filterClones(clonesets,
				similarities);
		print(filteredClonesets);

		if (CGConfig.getInstance().hasSIMILARITY()) {
			print(similarities);
		}

		// print(clonesets.values());
		// printInCCFinderFormat(files, filteredClonesets);
		final long endTime = System.nanoTime();

		{
			final StringBuilder text = new StringBuilder();
			text.append(Integer.toString(clonesets.size()));
			text.append(" clone sets have been detected.");
			text.append(System.lineSeparator());
			text.append(Integer.toString(filteredClonesets.size()));
			text.append(" clone sets have passed through filtering");
			text.append(System.lineSeparator());
			text.append("execution time: ");
			text.append(TimingUtility.getExecutionTime(startTime, endTime));
			text.append(System.lineSeparator());
			text.append(" file reading time: ");
			text.append(TimingUtility.getExecutionTime(startTime, middleTime));
			text.append(System.lineSeparator());
			text.append(" clone detection time: ");
			text.append(TimingUtility.getExecutionTime(middleTime, middleTime2));
			text.append(System.lineSeparator());
			text.append(" clone filtering time: ");
			text.append(TimingUtility.getExecutionTime(middleTime2, endTime));
			text.append(System.lineSeparator());
			text.append(" (for performance turning) matrix creation time for all the threads: ");
			text.append(TimingUtility.getExecutionTime(SmithWaterman
					.getMatrixCreationTime()));
			text.append(System.lineSeparator());
			text.append(" (for performance turning) similar alignment identification time for all the threads: ");
			text.append(TimingUtility.getExecutionTime(SmithWaterman
					.getCloneDetectionTime()));
			System.err.println(text.toString());
		}
	}

	private static List<SourceFile> getFiles() {

		final List<SourceFile> files = FileUtility.collectSourceFiles(new File(
				CGConfig.getInstance().getSource()));

		if (!CGConfig.getInstance().isVERBOSE()) {
			System.err.println("parsing source files ... ");
		}

		int number = 1;
		for (final SourceFile file : files) {

			if (CGConfig.getInstance().isVERBOSE()) {
				System.err.print(Integer.toString(number++));
				System.err.print("/");
				System.err.print(Integer.toString(files.size()));
				System.err.print(": parsing ");
				System.err.println(file.path);
			}

			int loc = 0;
			final StringBuilder textBuilder = new StringBuilder();
			try (final BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file.path),
							"JISAutoDetect"))) {
				while (reader.ready()) {
					final String line = reader.readLine();
					textBuilder.append(line);
					textBuilder.append(System.lineSeparator());
					loc++;
				}
			} catch (IOException e) {
				System.err.print("file \"");
				System.err.print(file.path);
				System.err.println("\" is unreadable.");
				continue;
			}

			final List<Statement> statements = StringUtility.splitToStatements(
					textBuilder.toString(), file.getLanguage());
			final List<Statement> foldedStatements = Statement
					.getFoldedStatements(statements);
			file.addStatements(foldedStatements);
			file.setLOC(loc);
		}

		final List<WebFile> webFiles = FileUtility.collectWebFiles(new File(
				CGConfig.getInstance().getSource()));
		if (CGConfig.getInstance().getLANGUAGE().contains(LANGUAGE.JAVASCRIPT)) {
			for (final WebFile f : webFiles) {
				final List<String> codes = f.extractJavascript();
				final JavascriptFile javascriptFile = new JavascriptFile(f.path);
				for (final String code : codes) {
					final List<Statement> statements = StringUtility
							.splitToStatements(code, LANGUAGE.JAVASCRIPT);
					final List<Statement> foldedStatements = Statement
							.getFoldedStatements(statements);
					javascriptFile.addStatements(foldedStatements);
					javascriptFile.addStatement(new Separator());
				}
				files.add(javascriptFile);
			}
		}

		return files;
	}

	private static List<CloneSet> detectClones(final List<SourceFile> files) {

		if (!CGConfig.getInstance().isVERBOSE()) {
			System.err.println("detecting clones ... ");
		}

		final ExecutorService executorService = Executors
				.newFixedThreadPool(CGConfig.getInstance().getTHREAD());
		final Map<CloneHash, CloneSet> clonesets = new HashMap<>();
		final List<Future<?>> futures = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			final SourceFile iFile = files.get(i);
			for (int j = i; j < files.size(); j++) {
				final SourceFile jFile = files.get(j);
				Future<?> future = executorService
						.submit(new CloneDetectionThread(iFile, jFile,
								clonesets));
				futures.add(future);
			}
		}

		try {
			for (final Future<?> future : futures) {
				future.get();
			}
		} catch (final ExecutionException | InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			executorService.shutdown();
		}

		final List<CloneSet> sets = new ArrayList<>();
		sets.addAll(clonesets.values());
		return sets;
	}

	private static List<CloneSet> filterClones(final List<CloneSet> clonesets,
			final Map<CloneSet, Map<CloneSet, Double>> similarities) {

		if (!CGConfig.getInstance().isVERBOSE()) {
			System.err.println("filtering trivial clones ... ");
		}

		// final List<List<Token>> clones = new ArrayList<>();
		// final Map<List<Token>, CloneHash> tokenToMD5 = new HashMap<>();
		// for (final Entry<CloneHash, CloneSet> entry : clonesets.entrySet()) {
		//
		// final CloneHash cloneHash = entry.getKey();
		// final List<Token> clone = new ArrayList<>();
		// for (final MD5 md5 : cloneHash.value) {
		// final List<Token> statement = MD5.getTokens(md5);
		// assert null != statement : "statement must not be null.";
		// clone.addAll(statement);
		// }
		// clones.add(clone);
		// tokenToMD5.put(clone, cloneHash);
		// }

		for (final CloneSet cloneset : clonesets) {
			similarities.put(cloneset, new HashMap<CloneSet, Double>());
		}
		final TFIDF tfidf = TFIDF.getInstance(clonesets);
		for (int i = 0; i < clonesets.size(); i++) {
			final CloneSet cloneI = clonesets.get(i);
			for (int j = i + 1; j < clonesets.size(); j++) {
				final CloneSet cloneJ = clonesets.get(j);
				final double similarity = tfidf.getNSIM(cloneI, cloneJ);
				if (0d < similarity) {
					similarities.get(cloneI).put(cloneJ, similarity);
					similarities.get(cloneJ).put(cloneI, similarity);
				}
			}
		}
		final List<CloneSet> filtered = new ArrayList<>();
		for (final Entry<CloneSet, Map<CloneSet, Double>> entry : similarities
				.entrySet()) {

			if (!isTrivial(entry)) {
				continue;
			}

			final CloneSet cloneset = entry.getKey();
			filtered.add(cloneset);
		}

		return filtered;
	}

	private static boolean isTrivial(
			final Entry<CloneSet, Map<CloneSet, Double>> entry) {

		final int N = 5;
		final double T = 0.95d;

		int count = 0;
		for (final Double similarity : entry.getValue().values()) {
			if (T <= similarity) {
				count++;
			}
			if (N <= count) {
				return true;
			}
		}

		return false;
	}

	private static void print(final List<CloneSet> clonesets) {

		Collections.sort(clonesets, new Comparator<CloneSet>() {
			@Override
			public int compare(final CloneSet cloneset1,
					final CloneSet cloneset2) {
				return Integer.valueOf(cloneset1.id).compareTo(
						Integer.valueOf(cloneset2.id));
			}
		});

		try (final PrintWriter writer = CGConfig.getInstance().hasRESULT() ? new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
						CGConfig.getInstance().getRESULT()), "UTF-8")))
				: new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"))) {

			for (final CloneSet cloneset : clonesets) {
				for (final ClonedFragment clonedFragment : cloneset.getClones()) {
					writer.print(Integer.toString(cloneset.id));
					writer.print("\t");
					writer.print(clonedFragment.path);
					writer.print("\t");
					writer.print(Integer.toString(clonedFragment.getFromLine()));
					writer.print("\t");
					writer.print(Integer.toString(clonedFragment.getToLine()));
					writer.println();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void printInCCFinderFormat(final List<SourceFile> files,
			final Collection<SortedSet<ClonedFragment>> clonesets) {

		try (final PrintWriter writer = CGConfig.getInstance().hasRESULT() ? new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
						CGConfig.getInstance().getRESULT()), "UTF-8")))
				: new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"))) {

			writer.println("#begin{file description}");
			Collections.sort(files, new Comparator<SourceFile>() {
				@Override
				public int compare(final SourceFile file1,
						final SourceFile file2) {
					return file1.path.compareTo(file2.path);
				}
			});
			final Map<String, Integer> map = new HashMap<String, Integer>();
			for (final SourceFile file : files) {
				final int number = map.size();
				writer.print("0.");
				writer.print(Integer.toString(number));
				writer.print("\t");
				writer.print(Integer.toString(file.getLOC()));
				writer.print("\t");
				writer.print(Integer.toString(file.getTokens().size()));
				writer.print("\t");
				writer.print(file.path);
				writer.println();
				map.put(file.path, number);
			}
			writer.println("#end{file description}");

			writer.println("#begin{syntax error}");
			writer.println("#end{syntax error}");

			writer.println("#begin{clone}");
			for (final SortedSet<ClonedFragment> cloneset : clonesets) {
				writer.println("#begin{set}");
				for (final ClonedFragment fragment : cloneset) {
					final Integer id = map.get(fragment.path);
					final List<Token> tokens = fragment.getTokens();
					writer.print("0.");
					writer.print(id.toString());
					writer.print("\t");
					writer.print(Integer.toString(fragment.getFromLine()));
					writer.print(",0,");
					writer.print(Integer.toString(tokens.get(0).index));
					writer.print("\t");
					writer.print(Integer.toString(fragment.getToLine() + 1));
					writer.print(",0,");
					writer.print(Integer.toString(tokens.get(tokens.size() - 1).index));
					writer.print("\t");
					writer.print(Integer.toString(tokens.size()));
					writer.println();
				}
				writer.println("#end{set}");
			}
			writer.println("#end{clone}");

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void print(
			final Map<CloneSet, Map<CloneSet, Double>> similarities) {

		try (final PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(CGConfig
						.getInstance().getSIMILARITY()), "UTF-8")))) {

			for (final Entry<CloneSet, Map<CloneSet, Double>> entry : similarities
					.entrySet()) {

				final CloneSet cloneset1 = entry.getKey();

				for (final Entry<CloneSet, Double> entry2 : entry.getValue()
						.entrySet()) {

					final CloneSet cloneset2 = entry2.getKey();
					final Double similarity = entry2.getValue();

					if (cloneset1.id < cloneset2.id) {
						final StringBuilder text = new StringBuilder();
						text.append(Integer.toString(cloneset1.id));
						text.append(", ");
						text.append(Integer.toString(cloneset2.id));
						text.append(", ");
						text.append(Double.toString(similarity));
						writer.println(text.toString());
					}
				}
			}
		}

		catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
