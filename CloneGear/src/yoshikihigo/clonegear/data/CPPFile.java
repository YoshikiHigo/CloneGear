package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class CPPFile extends SourceFile {

	public CPPFile(final String path) {
		super(path);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.CPP;
	}
}
