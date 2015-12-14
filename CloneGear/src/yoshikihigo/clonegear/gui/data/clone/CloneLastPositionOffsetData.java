package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloneLastPositionOffsetData {

	public static final CloneLastPositionOffsetData SINGLETON = new CloneLastPositionOffsetData();

	public int get(final GUICloneSet cloneset) {
		assert this.initialized : "CloneLastPositionOfsetData was not initialized.";
		return this.offsets.get(cloneset);
	}

	public void initialize(final GUICloneManager manager) {
		final List<GUICloneSet> lastPositionSorter = new ArrayList<GUICloneSet>(
				manager.getCloneSets());
		Collections.sort(
				lastPositionSorter,
				(cloneset1, cloneset2) -> cloneset1.last().compareTo(
						cloneset2.last()));

		int index = 0;
		for (final GUICloneSet cloneSet : lastPositionSorter) {
			this.offsets.put(cloneSet, index++);
		}

		this.initialized = true;
	}

	private CloneLastPositionOffsetData() {
		this.offsets = new HashMap<>();
		this.initialized = false;
	}

	private final Map<GUICloneSet, Integer> offsets;
	private boolean initialized;
}
