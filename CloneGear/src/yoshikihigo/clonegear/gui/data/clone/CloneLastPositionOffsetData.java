package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloneLastPositionOffsetData {

	private static CloneLastPositionOffsetData SINGLETON = null;

	public static CloneLastPositionOffsetData instance() {
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}

	public static void initialize(final GUICloneManager manager) {
		SINGLETON = new CloneLastPositionOffsetData();
		final List<GUICloneSet> lastPositionSorter = new ArrayList<>(
				manager.getCloneSets());
		Collections.sort(
				lastPositionSorter,
				(cloneset1, cloneset2) -> cloneset1.last().compareTo(
						cloneset2.last()));

		int index = 0;
		for (final GUICloneSet cloneSet : lastPositionSorter) {
			SINGLETON.offsets.put(cloneSet, index++);
		}
	}
	
	public int get(final GUICloneSet cloneset) {
		return this.offsets.get(cloneset);
	}

	private CloneLastPositionOffsetData() {
		this.offsets = new HashMap<>();
	}

	private final Map<GUICloneSet, Integer> offsets;
}
