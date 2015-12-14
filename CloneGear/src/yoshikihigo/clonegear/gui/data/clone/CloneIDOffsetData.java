package yoshikihigo.clonegear.gui.data.clone;

import java.util.HashMap;
import java.util.Map;

public class CloneIDOffsetData {

	public static final CloneIDOffsetData SINGLETON = new CloneIDOffsetData();

	public int get(final GUICloneSet cloneSet) {
		assert this.initialized : "CloneIDOffsetData was not initialized.";
		return this.offsets.get(cloneSet);
	}

	public void initialize(final GUICloneManager manager) {
		manager.getCloneSets().stream().forEach(cloneset -> {
			this.offsets.put(cloneset, cloneset.getID());
		});
		this.initialized = true;
	}

	private CloneIDOffsetData() {
		this.offsets = new HashMap<>();
		this.initialized = false;
	}

	private final Map<GUICloneSet, Integer> offsets;
	private boolean initialized;
}
