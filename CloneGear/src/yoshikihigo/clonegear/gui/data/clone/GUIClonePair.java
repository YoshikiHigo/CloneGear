package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.List;

import yoshikihigo.clonegear.gui.data.Entity;
import yoshikihigo.clonegear.gui.data.HavingClones;

public class GUIClonePair implements Comparable<GUIClonePair>, Entity,
		HavingClones {

	@Override
	public final List<GUIClone> getClones() {
		final List<GUIClone> clones = new ArrayList<>();
		clones.add(this.left);
		clones.add(this.right);
		return clones;
	}

	public final int getRNR() {
		final GUICloneSet cloneSet = GUICloneManager.SINGLETON
				.getCloneSet(this.left);
		return cloneSet.getRNR();
	}

	@Override
	public int compareTo(final GUIClonePair clonePair) {

		final int leftCloneOrder = this.left.compareTo(clonePair.left);
		if (0 != leftCloneOrder) {
			return leftCloneOrder;
		}

		final int rightCloneOrder = this.right.compareTo(clonePair.right);
		return rightCloneOrder;
	}

	@Override
	public boolean equals(Object o) {

		if (null == o) {
			return false;
		}

		if (!(o instanceof GUIClonePair)) {
			return false;
		}

		final GUIClonePair clonepair = (GUIClonePair) o;
		return this.left.equals(clonepair.left)
				&& this.right.equals(clonepair.right);
	}

	@Override
	public int hashCode() {
		return this.left.hashCode() + this.right.hashCode();
	}

	public GUIClonePair(final GUIClone left, final GUIClone right) {
		this(0, left, right);
	}

	public GUIClonePair(final int clonesetID, final GUIClone left,
			final GUIClone right) {

		this.clonesetID = clonesetID;
		final int order = left.compareTo(right);
		if (order < 0) {
			this.left = left;
			this.right = right;
		} else if (0 < order) {
			this.left = right;
			this.right = left;
		} else {
			throw new IllegalArgumentException(
					"Two parameters are the same code fragment!");
		}
	}

	final public int clonesetID;
	final public GUIClone left;
	final public GUIClone right;
}
