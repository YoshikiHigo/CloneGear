package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloneNIFOffsetData {

	private static CloneNIFOffsetData SINGLETON = null;

	public static CloneNIFOffsetData instance() {
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}

	public static void initialize(final GUICloneManager manager) {

		SINGLETON = new CloneNIFOffsetData();
		final List<GUICloneSet> clonesets = new ArrayList<>(
				manager.getCloneSets());
		Collections.sort(clonesets, (cloneset1, cloneset2) -> Integer.compare(
				cloneset1.getNIF(), cloneset2.getNIF()));

		int index = 0;
		for (final GUICloneSet cloneSet : clonesets) {
			SINGLETON.offsets.put(cloneSet, index++);
		}
	}

	public int get(final GUICloneSet cloneset) {
		return this.offsets.get(cloneset);
	}

	private CloneNIFOffsetData() {
		this.offsets = new HashMap<>();
	}

	private final Map<GUICloneSet, Integer> offsets;
}
