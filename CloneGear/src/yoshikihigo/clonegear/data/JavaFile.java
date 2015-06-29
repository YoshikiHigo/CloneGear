package yoshikihigo.clonegear.data;

public class JavaFile extends SourceFile {

	public JavaFile(final String path){
		super(path);
	}
	
	@Override
	public String getLanguage(){
		return "java";
	}
}
