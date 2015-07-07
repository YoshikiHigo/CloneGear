package yoshikihigo.clonegear.data;

import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.LANGUAGE;
import yoshikihigo.clonegear.lexer.token.Token;

public abstract class SourceFile {

	final public String path;
	final private List<Statement> statements;
	private int loc;
	
	public SourceFile(final String path) {
		this.path = path;
		this.statements = new ArrayList<>();
		this.loc = 0;
	}

	public void addStatements(final List<Statement> statements) {
		this.statements.addAll(statements);
	}

	public boolean hasStatement() {
		return !this.statements.isEmpty();
	}

	public List<Statement> getStatements() {
		final List<Statement> statements = new ArrayList<>();
		statements.addAll(this.statements);
		return statements;
	}
	
	public List<Token> getTokens(){
		final List<Token> tokens = new ArrayList<>();
		for(final Statement statement : this.statements){
			tokens.addAll(statement.tokens);
		}
		return tokens;
	}
	
	public void setLOC(final int loc){
		this.loc = loc;
	}
	
	public int getLOC(){
		return this.loc;
	}

	abstract public LANGUAGE getLanguage();
}
