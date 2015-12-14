package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloneNIFOffsetData {

	public static final CloneNIFOffsetData SINGLETON = new CloneNIFOffsetData();

	public int get(final GUICloneSet cloneset) {
		assert this.initialized : "CloneNIFOffsetData was not initialized.";
		return this.offsets.get(cloneset);
	}

	public void initialize(final GUICloneManager manager) {

		final List<GUICloneSet> clonesets = new ArrayList<>(
				manager.getCloneSets());
		Collections.sort(clonesets, (cloneset1, cloneset2) -> Integer.compare(
				cloneset1.getNIF(), cloneset2.getNIF()));

		int index = 0;
		for (final GUICloneSet cloneSet : clonesets) {
			this.offsets.put(cloneSet, index++);
		}

		this.initialized = true;
	}

	private CloneNIFOffsetData() {
		this.offsets = new HashMap<>();
		this.initialized = false;
	}

	private final Map<GUICloneSet, Integer> offsets;
	private boolean initialized;
}
