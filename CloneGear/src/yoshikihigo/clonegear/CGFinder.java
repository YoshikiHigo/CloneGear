package yoshikihigo.clonegear;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import yoshikihigo.clonegear.data.CFile;
import yoshikihigo.clonegear.data.CloneHash;
import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.JavaFile;
import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.data.Statement;

public class CGFinder {

	public static void main(final String[] args) {

		Config.initialize(args);
		final long startTime = System.nanoTime();

		final List<SourceFile> files = getFiles(new File(Config.getInstance()
				.getSource()));
		int number = 1;

		for (final SourceFile file : files) {

			if (Config.getInstance().isVERBOSE()) {
				System.err.print(Integer.toString(number++));
				System.err.print("/");
				System.err.print(Integer.toString(files.size()));
				System.err.print(": parsing ");
				System.err.println(file.path);
			}

			final StringBuilder textBuilder = new StringBuilder();
			try (final BufferedReader reader = new BufferedReader(
					new FileReader(file.path))) {
				while (reader.ready()) {
					final String line = reader.readLine();
					textBuilder.append(line);
					textBuilder.append(System.lineSeparator());
				}
			} catch (IOException e) {
				System.err.print("file \"");
				System.err.print(file.path);
				System.err.println("\" is unreeadable.");
				continue;
			}

			final List<Statement> statements = StringUtility.splitToStatements(
					textBuilder.toString(), file.getLanguage());
			file.addStatements(statements);
		}

		final Map<CloneHash, SortedSet<ClonedFragment>> clonesets = new HashMap<CloneHash, SortedSet<ClonedFragment>>();
		for (int i = 0; i < files.size(); i++) {
			final SourceFile iFile = files.get(i);
			for (int j = i + 1; j < files.size(); j++) {
				final SourceFile jFile = files.get(j);
				final SmithWaterman sw = new SmithWaterman(iFile, jFile);
				final List<ClonedFragment> clonedFragments = sw
						.getClonedFragments();
				for (final ClonedFragment clonedFragment : clonedFragments) {
					final CloneHash hash = new CloneHash(clonedFragment.cloneID);
					SortedSet<ClonedFragment> cloneset = clonesets.get(hash);
					if (null == cloneset) {
						cloneset = new TreeSet<ClonedFragment>();
						clonesets.put(hash, cloneset);
					}
					cloneset.add(clonedFragment);
				}
			}
		}

		print(clonesets);

		final long endTime = System.nanoTime();
		if (Config.getInstance().isVERBOSE()) {
			final StringBuilder text = new StringBuilder();
			text.append("execution time: ");
			text.append(TimingUtility.getExecutionTime(startTime, endTime));
			text.append(System.lineSeparator());
			text.append("matrix creation time: ");
			text.append(TimingUtility.getExecutionTime(SmithWaterman
					.getMatrixCreationTime()));
			text.append(System.lineSeparator());
			text.append("clone detection time: ");
			text.append(TimingUtility.getExecutionTime(SmithWaterman
					.getCloneDetectionTime()));
			System.err.println(text.toString());
		}
	}

	private static List<SourceFile> getFiles(final File file) {

		final List<SourceFile> files = new ArrayList<>();

		if (file.isFile()) {

			if (Config.getInstance().getLANGUAGE().equals("")
					|| Config.getInstance().getLANGUAGE()
							.equalsIgnoreCase("java")) {

				if (file.getName().endsWith(".java")) {
					files.add(new JavaFile(file.getAbsolutePath()));
				}
			}

			else if (Config.getInstance().getLANGUAGE().equals("")
					|| Config.getInstance().getLANGUAGE().equalsIgnoreCase("c")) {

				if (file.getName().endsWith(".c")) {
					files.add(new CFile(file.getAbsolutePath()));
				}
			}

			else if (Config.getInstance().getLANGUAGE().equals("")
					|| Config.getInstance().getLANGUAGE()
							.equalsIgnoreCase("cpp")) {

				if (file.getName().endsWith(".cpp")) {
					files.add(new CFile(file.getAbsolutePath()));
				}
			}
		}

		else if (file.isDirectory()) {
			final File[] children = file.listFiles();
			for (final File child : children) {
				final List<SourceFile> childFiles = getFiles(child);
				files.addAll(childFiles);
			}
		}

		else {
			assert false : "\"file\" is invalid.";
		}

		return files;
	}

	private static void print(
			final Map<CloneHash, SortedSet<ClonedFragment>> clonesets) {

		try (final BufferedWriter writer = Config.getInstance().hasOUTPUT() ? new BufferedWriter(
				new FileWriter(Config.getInstance().getOUTPUT()))
				: new BufferedWriter(new OutputStreamWriter(System.out))) {

			for (final SortedSet<ClonedFragment> cloneset : clonesets.values()) {
				writer.write("----- begin clone set -----");
				writer.newLine();
				for (final ClonedFragment clonedFragment : cloneset) {
					writer.write(clonedFragment.path);
					writer.write("\t");
					writer.write(Integer.toString(clonedFragment.getFromLine()));
					writer.write("--");
					writer.write(Integer.toString(clonedFragment.getToLine()));
					writer.newLine();
				}
				writer.write("-----  end clone set  -----");
				writer.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
