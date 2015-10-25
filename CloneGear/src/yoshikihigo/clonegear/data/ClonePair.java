package yoshikihigo.clonegear.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import yoshikihigo.clonegear.lexer.token.Token;

public class ClonePair extends CloneData {

	final static private AtomicInteger ID_GENERATOR = new AtomicInteger(0);

	final public int id;
	final public ClonedFragment left;
	final public ClonedFragment right;

	public ClonePair(final CloneHash hash, final List<Token> tokens,
			final ClonedFragment left, final ClonedFragment right) {
		super(hash, tokens);
		this.id = ID_GENERATOR.getAndIncrement();
		final int order = left.compareTo(right);
		if (order < 0) {
			this.left = left;
			this.right = right;
		} else if (order > 0) {
			this.left = right;
			this.right = left;
		} else {
			this.left = null;
			this.right = null;
			assert false : "invalid state.";
		}
	}

	@Override
	public int getID() {
		return this.id;
	}
}
