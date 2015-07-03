package yoshikihigo.clonegear.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yoshikihigo.clonegear.lexer.token.Token;

public class ClonedFragment implements Comparable<ClonedFragment> {

	final public byte[][] cloneID;
	final public String path;
	final public List<Statement> statements;

	public ClonedFragment(final byte[][] cloneID, final String path,
			final List<Statement> statements) {
		this.cloneID = cloneID;
		this.path = path;
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

	@Override
	public int compareTo(final ClonedFragment o) {

		final int pathComparisonResult = this.path.compareTo(o.path);
		if (0 != pathComparisonResult) {
			return pathComparisonResult;
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
