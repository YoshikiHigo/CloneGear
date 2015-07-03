package yoshikihigo.clonegear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

		final List<SourceFile> files = getFiles(new File(Config.getInstance()
				.getSource()));

		for (final SourceFile file : files) {

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
		for (final SortedSet<ClonedFragment> cloneset : clonesets.values()) {
			System.out.println("----- begin clone set -----");
			for (final ClonedFragment clonedFragment : cloneset) {
				System.out.print(clonedFragment.path);
				System.out.print("\t");
				System.out
						.print(Integer.toString(clonedFragment.getFromLine()));
				System.out.print("--");
				System.out
						.println(Integer.toString(clonedFragment.getToLine()));
			}
			System.out.println("-----  end clone set  -----");
		}
	}
}
