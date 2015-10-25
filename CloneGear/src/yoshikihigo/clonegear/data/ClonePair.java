package yoshikihigo.clonegear.data;

import java.util.concurrent.atomic.AtomicInteger;

public class ClonePair {

	final static private AtomicInteger ID_GENERATOR = new AtomicInteger(0);

	final public int id;
	final public ClonedFragment left;
	final public ClonedFragment right;

	public ClonePair(final ClonedFragment left, final ClonedFragment right) {
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
}
