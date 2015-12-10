package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class CPPFile extends SourceFile {

	public CPPFile(final String path, final int groupID) {
		super(path, groupID);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.CPP;
	}
}
