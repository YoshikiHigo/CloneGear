package yoshikihigo.clonegear.gui.data.clone;

import java.util.Comparator;

import yoshikihigo.clonegear.gui.data.Entity;
import yoshikihigo.clonegear.gui.data.file.GUIFile;

public final class GUIClone implements Comparable<GUIClone>, Entity {

	public static final Comparator<GUIClone> ID_COMPARATOR = (code1, code2) -> {
		final int groupIDComparisonResults = Integer.compare(code1.groupID,
				code2.groupID);
		if (0 != groupIDComparisonResults) {
			return groupIDComparisonResults;
		}
		final int fileIDComparisonResults = Integer.valueOf(code1.fileID)
				.compareTo(Integer.valueOf(code2.fileID));
		return fileIDComparisonResults;
	};

	public static final Comparator<GUIClone> LOCATION_COMPARATOR = (code1,
			code2) -> {
		final int fromLineComparisonResults = Integer.compare(code1.fromLine,
				code2.fromLine);
		if (0 != fromLineComparisonResults) {
			return fromLineComparisonResults;
		}
		final int toLineComparisonResults = Integer.compare(code1.toLine,
				code2.toLine);
		return toLineComparisonResults;
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
		final GUICloneSet cloneSet = GUICloneManager.SINGLETON
				.getCloneSet(this);
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
	public final boolean equals(Object o) {

		if (null == o) {
			return false;
		}

		if (!(o instanceof GUIClone)) {
			return false;
		}

		return 0 == this.compareTo((GUIClone) o);
	}

	@Override
	public final int compareTo(final GUIClone codeFragment) {

		final int groupComparisonResults = Integer.compare(this.groupID,
				codeFragment.groupID);
		if (0 != groupComparisonResults) {
			return groupComparisonResults;
		}

		final int fileComparisonResults = Integer.compare(this.fileID,
				codeFragment.fileID);
		if (0 != fileComparisonResults) {
			return fileComparisonResults;
		}

		final int fromLineComparisonResults = Integer.compare(this.fromLine,
				codeFragment.fromLine);
		if (0 != fromLineComparisonResults) {
			return fromLineComparisonResults;
		}

		final int toLineComparisonResults = Integer.compare(this.toLine,
				codeFragment.toLine);
		return toLineComparisonResults;
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
