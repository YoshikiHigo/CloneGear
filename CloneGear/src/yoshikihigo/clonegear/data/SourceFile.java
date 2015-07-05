package yoshikihigo.clonegear.data;

import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.LANGUAGE;

public abstract class SourceFile {

	final public String path;
	final private List<Statement> statements;

	public SourceFile(final String path) {
		this.path = path;
		this.statements = new ArrayList<>();
	}

	public void addStatements(final List<Statement> statements) {
		this.statements.addAll(statements);
	}

	public boolean hasStatement() {
		return !this.statements.isEmpty();
	}

	public List<Statement> getStatements() {
		final List<Statement> statements = new ArrayList<Statement>();
		statements.addAll(this.statements);
		return statements;
	}

	abstract public LANGUAGE getLanguage();
}
