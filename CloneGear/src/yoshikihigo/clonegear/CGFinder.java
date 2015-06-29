package yoshikihigo.clonegear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.data.CFile;
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

		else {
			assert false : "\"file\" is invalid.";
		}

		return files;
	}
}
