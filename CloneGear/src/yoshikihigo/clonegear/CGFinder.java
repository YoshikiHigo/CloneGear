package yoshikihigo.clonegear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import yoshikihigo.clonegear.data.CloneData;
import yoshikihigo.clonegear.data.CloneHash;
import yoshikihigo.clonegear.data.ClonePair;
import yoshikihigo.clonegear.data.CloneSet;
import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.HTMLFile;
import yoshikihigo.clonegear.data.JSPFile;
import yoshikihigo.clonegear.data.JavascriptFile;
import yoshikihigo.clonegear.data.PHPFile;
import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.data.Statement;
import yoshikihigo.clonegear.data.WebFile;
import yoshikihigo.clonegear.lexer.token.Token;
import yoshikihigo.clonegear.tfidf.TFIDF;

public class CGFinder {

	public static void main(final String[] args) {

		CGConfig.initialize(args);

		final long timeToStart = System.nanoTime();
		final List<SourceFile> files = getFiles();
		final long timeToDetect = System.nanoTime();
		final List<ClonePair> clonepairs = detectClones(files);
		final List<CloneSet> clonesets;
		final List<ClonePair> remainingClonepairs;
		final List<CloneSet> remainingClonesets;
		final long timeToFilter;
		if (CGConfig.getInstance().isBELLON()) {
			clonesets = null;
			timeToFilter = System.nanoTime();
			final Map<ClonePair, Map<ClonePair, Double>> similarities = new HashMap<>();
			remainingClonepairs = /* filterClones(clonepairs, similarities) */clonepairs;
			remainingClonesets = null;
			printInBellonFomat(remainingClonepairs);
		} else {
			clonesets = convert(clonepairs);
			timeToFilter = System.nanoTime();
			final Map<CloneSet, Map<CloneSet, Double>> similarities = new HashMap<>();
			remainingClonepairs = null;
			remainingClonesets = /* filterClones(clonesets, similarities) */clonesets;
			DetectionResultsFormat.writer(remainingClonesets, CGConfig
					.getInstance().getRESULT());
		}
		final long timeToEnd = System.nanoTime();

		{
			final StringBuilder text = new StringBuilder();
			if (CGConfig.getInstance().isBELLON()) {
				text.append(Integer.toString(clonepairs.size()));
				text.append(" clone pairs have been detected.");
				text.append(System.lineSeparator());
				// text.append(Integer.toString(remainingClonepairs.size()));
				// text.append(" clone pairs have passed through filtering.");
				// text.append(System.lineSeparator());
			} else {
				text.append(Integer.toString(clonesets.size()));
				text.append(" clone sets (");
				text.append(Integer.toString(clonepairs.size()));
				text.append(" clone pairs) have been detected.");
				text.append(System.lineSeparator());
				// text.append(Integer.toString(remainingClonesets.size()));
				// text.append(" clone sets have passed through filtering.");
				// text.append(System.lineSeparator());
			}

			text.append("execution time: ");
			text.append(TimingUtility.getExecutionTime(timeToStart, timeToEnd));
			text.append(System.lineSeparator());
			text.append(" file reading time: ");
			text.append(TimingUtility.getExecutionTime(timeToStart,
					timeToDetect));
			text.append(System.lineSeparator());
			text.append(" clone detection time: ");
			text.append(TimingUtility.getExecutionTime(timeToDetect,
					timeToFilter));
			text.append(System.lineSeparator());
			text.append(" clone filtering time: ");
			text.append(TimingUtility.getExecutionTime(timeToFilter, timeToEnd));
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

		for (final Iterator<SourceFile> iterator = files.iterator(); iterator
				.hasNext();) {
			final SourceFile file = iterator.next();
			if (file instanceof HTMLFile || file instanceof JSPFile
					|| file instanceof PHPFile) {
				iterator.remove();
			}
		}

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

			try {
				final List<String> lines = Files.readAllLines(
						Paths.get(file.path), StandardCharsets.UTF_8);
				final String text = String.join(System.lineSeparator(), lines);
				final int loc = lines.size();
				file.setLOC(loc);

				final List<Statement> statements = StringUtility
						.splitToStatements(text, file.getLanguage());
				if (!CGConfig.getInstance().isFOLDING()) {
					final List<Statement> foldedStatements = Statement
							.getFoldedStatements(statements);
					file.addStatements(foldedStatements);
				} else {
					file.addStatements(statements);
				}

			} catch (final IOException e) {
				System.err.print("file \"" + file.path + "\" is unreadable.");
				continue;
			}
		}

		final List<WebFile> webFiles = FileUtility.collectWebFiles(new File(
				CGConfig.getInstance().getSource()));
		if (CGConfig.getInstance().getLANGUAGE().contains(LANGUAGE.JAVASCRIPT)) {
			for (final WebFile f : webFiles) {
				final List<Statement> statements = f.getJavascriptStatements();
				final JavascriptFile javascriptFile = new JavascriptFile(
						f.path, 0);
				if (!CGConfig.getInstance().isFOLDING()) {
					final List<Statement> foldedStatements = Statement
							.getFoldedStatements(statements);
					javascriptFile.addStatements(foldedStatements);
				} else {
					javascriptFile.addStatements(statements);
				}
			}
		}
		if (CGConfig.getInstance().getLANGUAGE().contains(LANGUAGE.JSP)) {
			for (final WebFile f : webFiles) {
				final List<Statement> statements = f.getJSPStatements();
				final JSPFile jspFile = new JSPFile(f.path, 0);
				if (!CGConfig.getInstance().isFOLDING()) {
					final List<Statement> foldedStatements = Statement
							.getFoldedStatements(statements);
					jspFile.addStatements(foldedStatements);
				} else {
					jspFile.addStatements(statements);
				}
			}
		}
		if (CGConfig.getInstance().getLANGUAGE().contains(LANGUAGE.PHP)) {
			for (final WebFile f : webFiles) {
				final List<Statement> statements = f.getPHPStatements();
				final PHPFile phpFile = new PHPFile(f.path, 0);
				if (!CGConfig.getInstance().isFOLDING()) {
					final List<Statement> foldedStatements = Statement
							.getFoldedStatements(statements);
					phpFile.addStatements(foldedStatements);
				} else {
					phpFile.addStatements(statements);
				}
			}
		}

		return files;
	}

	private static List<ClonePair> detectClones(final List<SourceFile> files) {

		if (!CGConfig.getInstance().isVERBOSE()) {
			System.err.println("detecting clones ... ");
		}

		final boolean isCrossGroupDetection = CGConfig.getInstance()
				.isCrossGroupDetection();
		final boolean isCrossFileDetection = CGConfig.getInstance()
				.isCrossFileDetection();
		final boolean isWithinFileDetection = CGConfig.getInstance()
				.isWithinFileDetection();

		final ExecutorService executorService = Executors
				.newFixedThreadPool(CGConfig.getInstance().getTHREAD());
		final List<ClonePair> clonepairs = new ArrayList<>();
		final List<Future<?>> futures = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			final SourceFile iFile = files.get(i);
			for (int j = i; j < files.size(); j++) {
				final SourceFile jFile = files.get(j);

				if (!isCrossGroupDetection && (iFile.groupID != jFile.groupID)) {
					continue;
				}

				if (!isCrossFileDetection && (iFile.groupID == jFile.groupID)
						&& !iFile.path.equals(jFile.path)) {
					continue;
				}

				if (!isWithinFileDetection && iFile.path.equals(jFile.path)) {
					continue;
				}

				Future<?> future = executorService
						.submit(new CloneDetectionThread(iFile, jFile,
								clonepairs));
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

		return clonepairs;
	}

	private static List<CloneSet> convert(final List<ClonePair> clonepairs) {

		final Map<CloneHash, CloneSet> clonesets = new HashMap<>();
		clonepairs.stream().forEach(clonepair -> {
			final CloneHash hash = clonepair.hash;
			CloneSet cloneset = clonesets.get(hash);
			if (null == cloneset) {
				cloneset = new CloneSet(hash, clonepair.tokens);
				clonesets.put(hash, cloneset);
			}
			cloneset.addClonepair(clonepair);
		});

		final List<CloneSet> list = new ArrayList<>(clonesets.values());
		return list;
	}

	private static <T extends CloneData> List<T> filterClones(
			final List<T> clones, final Map<T, Map<T, Double>> similarities) {

		if (!CGConfig.getInstance().isVERBOSE()) {
			System.err.println("filtering trivial clones ... ");
		}

		for (final T clone : clones) {
			similarities.put(clone, new HashMap<T, Double>());
		}
		final TFIDF<T> tfidf = new TFIDF<>(clones);
		for (int i = 0; i < clones.size(); i++) {
			final T cloneI = clones.get(i);
			for (int j = i + 1; j < clones.size(); j++) {
				final T cloneJ = clones.get(j);
				final double similarity = tfidf.getNSIM(cloneI, cloneJ);
				if (0d < similarity) {
					similarities.get(cloneI).put(cloneJ, similarity);
					similarities.get(cloneJ).put(cloneI, similarity);
				}
			}
		}
		final List<T> remaining = new ArrayList<>();
		for (final Entry<T, Map<T, Double>> entry : similarities.entrySet()) {

			if (isTrivial(entry)) {
				continue;
			}

			final T cloneset = entry.getKey();
			remaining.add(cloneset);
		}

		return remaining;
	}

	private static <T extends CloneData> boolean isTrivial(
			final Entry<T, Map<T, Double>> entry) {

		final int N = 10;
		final double threshold = 0.96d;

		int count = 0;
		for (final Double similarity : entry.getValue().values()) {
			if (threshold <= similarity) {
				count++;
			}
			if (N <= count) {
				return true;
			}
		}

		return false;
	}

	private static void printInBellonFomat(final List<ClonePair> clonepairs) {

		try (final PrintWriter writer = CGConfig.getInstance().hasRESULT() ? new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
						CGConfig.getInstance().getRESULT()), "UTF-8")))
				: new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"))) {

			for (final ClonePair clonepair : clonepairs) {
				writer.print(clonepair.left.file.path);
				writer.print("\t");
				writer.print(clonepair.left.getFromLine());
				writer.print("\t");
				writer.print(clonepair.left.getToLine());
				writer.print("\t");
				writer.print(clonepair.right.file.path);
				writer.print("\t");
				writer.print(clonepair.right.getFromLine());
				writer.print("\t");
				writer.print(clonepair.right.getToLine());
				writer.print("\t3");
				writer.println();
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
			Collections.<SourceFile> sort(files,
					(file1, file2) -> file1.path.compareTo(file2.path));

			final Map<SourceFile, Integer> map = new HashMap<SourceFile, Integer>();
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
				map.put(file, number);
			}
			writer.println("#end{file description}");

			writer.println("#begin{syntax error}");
			writer.println("#end{syntax error}");

			writer.println("#begin{clone}");
			for (final SortedSet<ClonedFragment> cloneset : clonesets) {
				writer.println("#begin{set}");
				for (final ClonedFragment fragment : cloneset) {
					final Integer id = map.get(fragment.file);
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

	private static <T extends CloneData> void print(
			final Map<T, Map<T, Double>> similarities) {

		try (final PrintWriter writer = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(CGConfig
						.getInstance().getSIMILARITY()), "UTF-8")))) {

			for (final Entry<T, Map<T, Double>> entry : similarities.entrySet()) {

				final T clone1 = entry.getKey();

				for (final Entry<T, Double> entry2 : entry.getValue()
						.entrySet()) {

					final T clone2 = entry2.getKey();
					final Double similarity = entry2.getValue();

					if (clone1.getID() < clone2.getID()) {
						final StringBuilder text = new StringBuilder();
						text.append(Integer.toString(clone1.getID()));
						text.append(", ");
						text.append(Integer.toString(clone2.getID()));
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
