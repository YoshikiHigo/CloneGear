package yoshikihigo.clonegear.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yoshikihigo.clonegear.lexer.token.Token;

public class ClonedFragment implements Comparable<ClonedFragment> {

	final public MD5[] cloneID;
	final public SourceFile file;
	final public List<Statement> statements;

	public ClonedFragment(final MD5[] cloneID, final SourceFile file,
			final List<Statement> statements) {
		this.cloneID = cloneID;
		this.file = file;
		this.statements = Collections.unmodifiableList(statements);
	}

	public int getNumberOfStatements() {
		return this.statements.size();
	}

	public int getNumberOfTokens() {
		int not = 0;
		for (final Statement statement : this.statements) {
			not += statement.getNumberOfTokens();
		}
		return not;
	}

	public List<Token> getTokens() {
		final List<Token> tokens = new ArrayList<Token>();
		for (final Statement statement : this.statements) {
			tokens.addAll(statement.tokens);
		}
		return tokens;
	}

	public int getFromLine() {
		return this.statements.get(0).fromLine;
	}

	public int getToLine() {
		return this.statements.get(this.statements.size() - 1).toLine;
	}

	public boolean isOverraped(final ClonedFragment clonedFragment) {

		if (!this.file.equals(clonedFragment.file)) {
			return false;
		}

		else if (this.getToLine() < clonedFragment.getFromLine()) {
			return false;
		}

		else if (clonedFragment.getToLine() < this.getFromLine()) {
			return false;
		}

		else {
			return true;
		}
	}

	@Override
	public int compareTo(final ClonedFragment o) {

		final int fileComparisonResult = this.file.compareTo(o.file);
		if (0 != fileComparisonResult) {
			return fileComparisonResult;
		}

		final int fromLine1 = this.getFromLine();
		final int toLine1 = this.getToLine();
		final int fromLine2 = o.getFromLine();
		final int toLine2 = o.getToLine();
		if (fromLine1 < fromLine2) {
			return -1;
		} else if (fromLine1 > fromLine2) {
			return 1;
		} else if (toLine1 < toLine2) {
			return -1;
		} else if (toLine1 > toLine2) {
			return 1;
		} else {
			return 0;
		}
	}

}
