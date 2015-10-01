package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class PHPFile extends WebFile {

	public PHPFile(final String path) {
		super(path);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.PHP;
	}
}
