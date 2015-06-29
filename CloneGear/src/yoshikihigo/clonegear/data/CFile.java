package yoshikihigo.clonegear.data;

public class CFile extends SourceFile{

	public CFile(final String path){
		super(path);
	}
	
	@Override
	public String getLanguage(){
		return "c";
	}
}
