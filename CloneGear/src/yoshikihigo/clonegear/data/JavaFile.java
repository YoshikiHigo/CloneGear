package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class JavaFile extends SourceFile {

	public JavaFile(final String path){
		super(path);
	}
	
	@Override
	public LANGUAGE getLanguage(){
		return LANGUAGE.JAVA;
	}
}
