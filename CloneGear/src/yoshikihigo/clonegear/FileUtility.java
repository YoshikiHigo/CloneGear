package yoshikihigo.clonegear;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.data.WebFile;

public class FileUtility {

	public static LANGUAGE getLANGUAGE(final File file) {
		if (!file.isFile()) {
			return null;
		}
		for (final LANGUAGE language : LANGUAGE.values()) {
			if (language.isTarget(file)) {
				return language;
			}
		}
		return null;
	}

	public static List<SourceFile> collectSourceFiles(final File file) {

		final List<SourceFile> files = new ArrayList<>();

		if (file.isFile()) {

			final LANGUAGE language = getLANGUAGE(file);
			if (null != language
					&& CGConfig.getInstance().getLANGUAGE().contains(language)) {
				final SourceFile sourcefile = language.getSourceFile(file);
				files.add(sourcefile);
			}
		}

		else if (file.isDirectory()) {

			final File[] children = file.listFiles();
			if (null != children) {
				for (final File child : children) {
					final List<SourceFile> childFiles = collectSourceFiles(child);
					files.addAll(childFiles);
				}
			}
		}

		else {
			assert false : "\"file\" is invalid.";
		}

		return files;
	}

	public static List<WebFile> collectWebFiles(final File file) {

		final List<WebFile> files = new ArrayList<>();

		if (file.isFile()) {

			final LANGUAGE language = getLANGUAGE(file);
			if ((null != language)
					&& (language.equals(LANGUAGE.HTML)
							|| language.equals(LANGUAGE.JSP) || language
								.equals(LANGUAGE.PHP))) {
				final WebFile webFile = (WebFile) language.getSourceFile(file);
				files.add(webFile);
			}
		}

		else if (file.isDirectory()) {

			final File[] children = file.listFiles();
			if (null != children) {
				for (final File child : children) {
					final List<WebFile> childFiles = collectWebFiles(child);
					files.addAll(childFiles);
				}
			}
		}

		else {
			assert false : "\"file\" is invalid.";
		}

		return files;
	}

	public static String readFile(final File file, final String encoding) {

		final StringBuilder text = new StringBuilder();

		try (final InputStreamReader reader = new InputStreamReader(
				new FileInputStream(file), null != encoding ? encoding
						: "JISAutoDetect")) {

			while (reader.ready()) {
				final int c = reader.read();
				text.append((char) c);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return text.toString();
	}
}
