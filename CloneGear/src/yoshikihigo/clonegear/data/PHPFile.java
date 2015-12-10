package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class PHPFile extends WebFile {

	public PHPFile(final String path, final int groupID) {
		super(path, groupID);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.PHP;
	}
}
