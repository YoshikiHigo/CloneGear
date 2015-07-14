package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class PythonFile extends SourceFile {

	public PythonFile(final String path){
		super(path);
	}
	
	@Override
	public LANGUAGE getLanguage(){
		return LANGUAGE.PYTHON;
	}
}
