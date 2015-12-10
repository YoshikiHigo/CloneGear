package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class PythonFile extends SourceFile {

	public PythonFile(final String path, final int groupID) {
		super(path, groupID);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.PYTHON;
	}
}
