package yoshikihigo.clonegear;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import yoshikihigo.clonegear.data.CFile;
import yoshikihigo.clonegear.data.CloneHash;
import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.JavaFile;
import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.data.Statement;
import yoshikihigo.clonegear.lexer.token.Token;

public class CGFinder {

	public static void main(final String[] args) {

		CGConfig.initialize(args);

		final long startTime = System.nanoTime();
		final List<SourceFile> files = getFiles();
		final long middleTime = System.nanoTime();
		final Map<CloneHash, SortedSet<ClonedFragment>> clonesets = detectClones(files);
		// print(clonesets);
		printInCCFinderFormat(files, clonesets);
		final long endTime = System.nanoTime();

		if (CGConfig.getInstance().isVERBOSE()) {
			final StringBuilder text = new StringBuilder();
			text.append("execution time: ");
			text.append(TimingUtility.getExecutionTime(startTime, endTime));
			text.append(System.lineSeparator());
			text.append(" file reading time: ");
			text.append(TimingUtility.getExecutionTime(startTime, middleTime));
			text.append(System.lineSeparator());
			text.append(" clone detection time: ");
			text.append(TimingUtility.getExecutionTime(middleTime, endTime));
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

		final List<SourceFile> files = collectFiles(new File(CGConfig
				.getInstance().getSource()));

		{
			if (!CGConfig.getInstance().isVERBOSE()) {
				System.err.print("parsing source files ... ");
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
						new FileReader(file.path))) {
					while (reader.ready()) {
						final String line = reader.readLine();
						textBuilder.append(line);
						textBuilder.append(System.lineSeparator());
						loc++;
					}
				} catch (IOException e) {
					System.err.print("file \"");
					System.err.print(file.path);
					System.err.println("\" is unreeadable.");
					continue;
				}

				final List<Statement> statements = StringUtility
						.splitToStatements(textBuilder.toString(),
								file.getLanguage());
				final List<Statement> foldedStatements = Statement
						.getFoldedStatements(statements);
				file.addStatements(foldedStatements);
				file.setLOC(loc);
			}

			if (!CGConfig.getInstance().isVERBOSE()) {
				System.err.println("done.");
			}

			return files;
		}
	}

	private static List<SourceFile> collectFiles(final File file) {

		final List<SourceFile> files = new ArrayList<>();

		if (file.isFile()) {

			if (CGConfig.getInstance().getLANGUAGE().equals("")
					|| CGConfig.getInstance().getLANGUAGE()
							.equalsIgnoreCase("java")) {

				if (file.getName().endsWith(".java")) {
					files.add(new JavaFile(file.getAbsolutePath()));
				}
			}

			else if (CGConfig.getInstance().getLANGUAGE().equals("")
					|| CGConfig.getInstance().getLANGUAGE().equalsIgnoreCase("c")) {

				if (file.getName().endsWith(".c")) {
					files.add(new CFile(file.getAbsolutePath()));
				}
			}

			else if (CGConfig.getInstance().getLANGUAGE().equals("")
					|| CGConfig.getInstance().getLANGUAGE()
							.equalsIgnoreCase("cpp")) {

				if (file.getName().endsWith(".cpp")) {
					files.add(new CFile(file.getAbsolutePath()));
				}
			}

			else if (CGConfig.getInstance().getLANGUAGE().equals("")
					|| CGConfig.getInstance().getLANGUAGE()
							.equalsIgnoreCase("python")) {

				if (file.getName().endsWith(".py")) {
					files.add(new CFile(file.getAbsolutePath()));
				}
			}
		}

		else if (file.isDirectory()) {
			final File[] children = file.listFiles();
			for (final File child : children) {
				final List<SourceFile> childFiles = collectFiles(child);
				files.addAll(childFiles);
			}
		}

		else {
			assert false : "\"file\" is invalid.";
		}

		return files;
	}

	private static Map<CloneHash, SortedSet<ClonedFragment>> detectClones(
			final List<SourceFile> files) {

		if (!CGConfig.getInstance().isVERBOSE()) {
			System.err.print("detecting clones ... ");
		}

		final ExecutorService executorService = Executors
				.newFixedThreadPool(CGConfig.getInstance().getTHREAD());
		final Map<CloneHash, SortedSet<ClonedFragment>> clonesets = new HashMap<CloneHash, SortedSet<ClonedFragment>>();
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
		}

		return clonesets;
	}

	private static void print(
			final Map<CloneHash, SortedSet<ClonedFragment>> clonesets) {

		try (final BufferedWriter writer = CGConfig.getInstance().hasOUTPUT() ? new BufferedWriter(
				new FileWriter(CGConfig.getInstance().getOUTPUT()))
				: new BufferedWriter(new OutputStreamWriter(System.out))) {

			int clonesetID = 0;
			for (final SortedSet<ClonedFragment> cloneset : clonesets.values()) {
				for (final ClonedFragment clonedFragment : cloneset) {
					writer.write(Integer.toString(clonesetID));
					writer.write("\t");
					writer.write(clonedFragment.path);
					writer.write("\t");
					writer.write(Integer.toString(clonedFragment.getFromLine()));
					writer.write("\t");
					writer.write(Integer.toString(clonedFragment.getToLine()));
					writer.newLine();
				}
				clonesetID++;
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void printInCCFinderFormat(final List<SourceFile> files,
			final Map<CloneHash, SortedSet<ClonedFragment>> clonesets) {

		try (final BufferedWriter writer = CGConfig.getInstance().hasOUTPUT() ? new BufferedWriter(
				new FileWriter(CGConfig.getInstance().getOUTPUT()))
				: new BufferedWriter(new OutputStreamWriter(System.out))) {

			writer.write("#begin{file description}");
			writer.newLine();
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
				writer.write("0.");
				writer.write(Integer.toString(number));
				writer.write("\t");
				writer.write(Integer.toString(file.getLOC()));
				writer.write("\t");
				writer.write(Integer.toString(file.getTokens().size()));
				writer.write("\t");
				writer.write(file.path);
				writer.newLine();
				map.put(file.path, number);
			}
			writer.write("#end{file description}");
			writer.newLine();

			writer.write("#begin{syntax error}");
			writer.newLine();
			writer.write("#end{syntax error}");
			writer.newLine();

			writer.write("#begin{clone}");
			writer.newLine();
			for (final SortedSet<ClonedFragment> cloneset : clonesets.values()) {
				writer.write("#begin{set}");
				writer.newLine();
				for (final ClonedFragment fragment : cloneset) {
					final Integer id = map.get(fragment.path);
					final List<Token> tokens = fragment.getTokens();
					writer.write("0.");
					writer.write(id.toString());
					writer.write("\t");
					writer.write(Integer.toString(fragment.getFromLine()));
					writer.write(",0,");
					writer.write(Integer.toString(tokens.get(0).index));
					writer.write("\t");
					writer.write(Integer.toString(fragment.getToLine() + 1));
					writer.write(",0,");
					writer.write(Integer.toString(tokens.get(tokens.size() - 1).index));
					writer.write("\t");
					writer.write(Integer.toString(tokens.size()));
					writer.newLine();
				}
				writer.write("#end{set}");
				writer.newLine();
			}
			writer.write("#end{clone}");
			writer.newLine();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
