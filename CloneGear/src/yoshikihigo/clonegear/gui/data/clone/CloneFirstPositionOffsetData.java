package yoshikihigo.clonegear.gui.data.clone;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CloneFirstPositionOffsetData {

	private static CloneFirstPositionOffsetData SINGLETON = null;

	public static CloneFirstPositionOffsetData instance(){
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}
	
	public static void initialize(final GUICloneManager manager) {
		
		SINGLETON = new CloneFirstPositionOffsetData();
		final List<GUICloneSet> clonesets = new LinkedList<>(
				manager.getCloneSets());
		Collections.sort(clonesets, (cloneset1, cloneset2) -> cloneset1.first()
				.compareTo(cloneset2.first()));

		int index = 0;
		for (final GUICloneSet cloneSet : clonesets) {
			SINGLETON.offsets.put(cloneSet, index++);
		}
	}
	
	public int get(final GUICloneSet cloneset) {
		return this.offsets.get(cloneset);
	}

	

	private CloneFirstPositionOffsetData() {
		this.offsets = new HashMap<>();
	}

	private final Map<GUICloneSet, Integer> offsets;
}
