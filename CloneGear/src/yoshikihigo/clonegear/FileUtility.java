package yoshikihigo.clonegear;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.data.SourceFile;

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
}
