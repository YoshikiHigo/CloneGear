package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class JSPFile extends WebFile {

	public JSPFile(final String path) {
		super(path);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.JSP;
	}
}
