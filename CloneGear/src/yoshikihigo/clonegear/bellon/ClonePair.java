package yoshikihigo.clonegear.bellon;

import java.util.HashSet;
import java.util.Set;

public class ClonePair {

	final public Clone left;
	final public Clone right;
	final public int type;

	public ClonePair(final Clone left, final Clone right, final int type) {
		this.left = left;
		this.right = right;
		this.type = type;
	}

	static public float ok(final ClonePair pair1, final ClonePair pair2) {
		return Math.min(overlap(pair1.left, pair2.left),
				overlap(pair1.right, pair2.right));
	}

	static public float good(final ClonePair pair1, final ClonePair pair2) {
		return Math.min(
				Math.max(contain(pair1.left, pair2.left),
						contain(pair2.left, pair1.left)),
				Math.max(contain(pair1.right, pair2.right),
						contain(pair2.right, pair1.right)));
	}

	static public float overlap(final Clone clone1, final Clone clone2) {

		if (!clone1.path.equals(clone2.path)) {
			return 0f;
		} else {
			final Set<Integer> union = new HashSet<>();
			union.addAll(clone1.getLines());
			union.addAll(clone2.getLines());
			final Set<Integer> intersection = new HashSet<>();
			intersection.addAll(clone1.getLines());
			intersection.retainAll(clone2.getLines());
			return (float) intersection.size() / (float) union.size();
		}
	}

	static public float contain(final Clone clone1, final Clone clone2) {

		if (!clone1.path.equals(clone2.path)) {
			return 0f;
		} else {
			final Set<Integer> intersection = new HashSet<>();
			intersection.addAll(clone1.getLines());
			intersection.retainAll(clone2.getLines());
			return (float) intersection.size()
					/ (float) clone1.getLines().size();
		}
	}
}
