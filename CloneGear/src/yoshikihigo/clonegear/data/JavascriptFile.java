package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class JavascriptFile extends SourceFile {

	public JavascriptFile(final String path, final int groupID) {
		super(path, groupID);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.JAVASCRIPT;
	}
}
