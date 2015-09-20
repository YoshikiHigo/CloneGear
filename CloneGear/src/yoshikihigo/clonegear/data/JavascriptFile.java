package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class JavascriptFile extends SourceFile {

	public JavascriptFile(final String path) {
		super(path);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.JAVASCRIPT;
	}
}
