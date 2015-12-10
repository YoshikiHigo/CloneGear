package yoshikihigo.clonegear;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.data.WebFile;

public class FileUtility {

	final static private Map<String, Integer> DIRECTORY_GROUP_MAP = new HashMap<>();

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
				final String directory = file.getParent();
				Integer groupID = DIRECTORY_GROUP_MAP.get(directory);
				if (null == groupID) {
					groupID = Integer.valueOf(DIRECTORY_GROUP_MAP.size());
					DIRECTORY_GROUP_MAP.put(directory, groupID);
				}
				final SourceFile sourcefile = language.getSourceFile(file,
						groupID.intValue());
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
				final String directory = file.getParent();
				Integer groupID = DIRECTORY_GROUP_MAP.get(directory);
				if (null == groupID) {
					groupID = Integer.valueOf(DIRECTORY_GROUP_MAP.size());
					DIRECTORY_GROUP_MAP.put(directory, groupID);
				}
				final WebFile webFile = (WebFile) language.getSourceFile(file,
						0);
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

	public static List<SourceFile> collectFilesWithList(final String list) {
		final List<SourceFile> files = new ArrayList<>();
		try {
			final List<String> paths = Files.readAllLines(Paths.get(list),
					StandardCharsets.UTF_8);
			int groupID = 0;
			for (final String path : paths) {
				if (isBlankLine(path)) {
					groupID++;
				} else {
					final File file = new File(path);
					if (file.isFile()) {
						final LANGUAGE language = getLANGUAGE(file);
						if (null != language
								&& CGConfig.getInstance().getLANGUAGE()
										.contains(language)) {
							final SourceFile sourcefile = language
									.getSourceFile(file, groupID);
							files.add(sourcefile);
						}
					}
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return files;
	}

	private static boolean isBlankLine(final String line) {
		return line.chars().allMatch(c -> (' ' == c) || ('\t' == c));
	}
}
