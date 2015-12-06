package yoshikihigo.clonegear.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import yoshikihigo.clonegear.lexer.token.Token;

public class ClonePair extends CloneData implements Comparable<ClonePair> {

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

	@Override
	public int compareTo(final ClonePair clonepair) {
		final int leftComparisonResults = this.left.compareTo(clonepair.left);
		if (0 != leftComparisonResults) {
			return leftComparisonResults;
		}

		final int rightComparisonResults = this.right
				.compareTo(clonepair.right);
		return rightComparisonResults;
	}

	@Override
	public boolean equals(final Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof ClonePair)) {
			return false;
		}
		final ClonePair target = (ClonePair) o;
		return this.left.equals(target.left) && this.right.equals(target.right);
	}

	@Override
	public int hashCode() {
		return this.left.hashCode() + this.right.hashCode();
	}
}
