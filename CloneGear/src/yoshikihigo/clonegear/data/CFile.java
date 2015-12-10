package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class CFile extends SourceFile {

	public CFile(final String path, final int groupID) {
		super(path, groupID);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.C;
	}
}
