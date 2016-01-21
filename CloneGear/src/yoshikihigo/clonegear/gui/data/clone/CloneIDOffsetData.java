package yoshikihigo.clonegear.gui.data.clone;

import java.util.HashMap;
import java.util.Map;

public class CloneIDOffsetData {

	private static CloneIDOffsetData SINGLETON = null;

	public static CloneIDOffsetData instance() {
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}

	public static void initialize(final GUICloneManager manager) {
		SINGLETON = new CloneIDOffsetData();
		manager.getCloneSets().stream().forEach(cloneset -> {
			SINGLETON.offsets.put(cloneset, cloneset.id);
		});
	}

	public int get(final GUICloneSet cloneSet) {
		return this.offsets.get(cloneSet);
	}

	private CloneIDOffsetData() {
		this.offsets = new HashMap<>();
	}

	private final Map<GUICloneSet, Integer> offsets;
}
