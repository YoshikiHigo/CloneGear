package yoshikihigo.clonegear.gui.data.clone;

import java.util.Comparator;

import yoshikihigo.clonegear.gui.data.Entity;
import yoshikihigo.clonegear.gui.data.file.GUIFile;

public final class GUIClone implements Comparable<GUIClone>, Entity {

	public static final Comparator<GUIClone> ID_COMPARATOR = (clone1, clone2) -> {
		final int groupIDComparisonResults = Integer.compare(clone1.groupID,
				clone2.groupID);
		if (0 != groupIDComparisonResults) {
			return groupIDComparisonResults;
		}
		return Integer.compare(clone1.fileID, clone2.fileID);
	};

	public static final Comparator<GUIClone> LOCATION_COMPARATOR = (clone1,
			clone2) -> {
		final int fromLineComparisonResults = Integer.compare(clone1.fromLine,
				clone2.fromLine);
		if (0 != fromLineComparisonResults) {
			return fromLineComparisonResults;
		}
		return Integer.compare(clone1.toLine, clone2.toLine);
	};

	public GUIClone(final int clonesetID, final GUIFile file,
			final int fromLine, final int toLine, final float rnr) {
		this.clonesetID = clonesetID;
		this.file = file;
		this.groupID = file.groupID;
		this.fileID = file.fileID;
		this.fromLine = fromLine;
		this.toLine = toLine;
		this.rnr = rnr;
	}

	GUIClone(final int groupID, final int fileID) {
		this.clonesetID = -1;
		this.file = null;
		this.groupID = groupID;
		this.fileID = fileID;
		this.fromLine = -1;
		this.toLine = -1;
		this.rnr = -1f;
	}

	public final int getLOC() {
		return this.toLine - this.fromLine + 1;
	}

	public final int getRNR() {
		final GUICloneSet cloneSet = GUICloneManager.instance().getCloneSet(
				this);
		return cloneSet.getRNR();
	}

	public final boolean isOverlap(final GUIClone target) {
		if (!this.file.equals(target.file)) {
			return false;
		} else if (this.toLine < target.fromLine) {
			return false;
		} else if (this.fromLine > target.toLine) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public final boolean equals(final Object o) {

		if (null == o) {
			return false;
		}

		if (!(o instanceof GUIClone)) {
			return false;
		}

		return 0 == this.compareTo((GUIClone) o);
	}

	@Override
	public final int compareTo(final GUIClone clone) {

		final int groupComparisonResults = Integer.compare(this.groupID,
				clone.groupID);
		if (0 != groupComparisonResults) {
			return groupComparisonResults;
		}

		final int fileComparisonResults = Integer.compare(this.fileID,
				clone.fileID);
		if (0 != fileComparisonResults) {
			return fileComparisonResults;
		}

		final int fromLineComparisonResults = Integer.compare(this.fromLine,
				clone.fromLine);
		if (0 != fromLineComparisonResults) {
			return fromLineComparisonResults;
		}

		return Integer.compare(this.toLine, clone.toLine);
	}

	@Override
	public final int hashCode() {
		return this.groupID + this.fileID + this.fromLine + this.toLine;
	}

	final public int clonesetID;
	final public GUIFile file;
	final public int groupID;
	final public int fileID;
	final public int fromLine;
	final public int toLine;
	final public float rnr;
}
