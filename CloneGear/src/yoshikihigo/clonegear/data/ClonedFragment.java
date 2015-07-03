package yoshikihigo.clonegear.data;

import java.util.Collections;
import java.util.List;

public class ClonedFragment {

	final public byte[][] cloneID;
	final public String path;
	final public List<Statement> statements;

	public ClonedFragment(final byte[][] cloneID, final String path,
			final List<Statement> statements) {
		this.cloneID = cloneID;
		this.path = path;
		this.statements = Collections.unmodifiableList(statements);
	}

}
