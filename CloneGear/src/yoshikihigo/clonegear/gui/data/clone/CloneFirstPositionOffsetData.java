package yoshikihigo.clonegear.gui.data.clone;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CloneFirstPositionOffsetData {

	public static final CloneFirstPositionOffsetData SINGLETON = new CloneFirstPositionOffsetData();

	public int get(final GUICloneSet cloneset) {
		assert this.initialized : "CloneFirstPositionOffsetData was not initialized.";
		return this.offsets.get(cloneset);
	}

	public void initialize(final GUICloneManager manager) {

		final List<GUICloneSet> clonesets = new LinkedList<>(
				manager.getCloneSets());
		Collections.sort(clonesets, (cloneset1, cloneset2) -> cloneset1.first()
				.compareTo(cloneset2.first()));

		int index = 0;
		for (final GUICloneSet cloneSet : clonesets) {
			this.offsets.put(cloneSet, index++);
		}

		this.initialized = true;
	}

	private CloneFirstPositionOffsetData() {
		this.offsets = new HashMap<>();
		this.initialized = false;
	}

	private final Map<GUICloneSet, Integer> offsets;
	private boolean initialized;
}
