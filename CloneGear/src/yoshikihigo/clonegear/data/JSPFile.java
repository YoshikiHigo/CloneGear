package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class JSPFile extends WebFile {

	public JSPFile(final String path, final int groupID) {
		super(path, groupID);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.JSP;
	}
}
