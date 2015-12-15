package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.gui.data.file.IDIndexMap;

public class CloneMiddlePositionOffsetData {

	public static final CloneMiddlePositionOffsetData SINGLETON = new CloneMiddlePositionOffsetData();

	public int get(final GUICloneSet cloneset) {
		assert this.initialized : "CloneMiddlePositionOffsetData was not initialized.";
		return this.offsets.get(cloneset);
	}

	public void initialize(final GUICloneManager manager) {

		final List<GUICloneSet> clonesets = new ArrayList<>(
				manager.getCloneSets());
		Collections.sort(
				clonesets,
				(cloneset1, cloneset2) -> {
					final double average1 = cloneset1
							.getClones()
							.stream()
							.mapToInt(
									clone -> IDIndexMap.SINGLETON.getIndex(
											clone.file.groupID,
											clone.file.fileID)).average()
							.getAsDouble();

					final double average2 = cloneset2
							.getClones()
							.stream()
							.mapToInt(
									clone -> IDIndexMap.SINGLETON.getIndex(
											clone.file.groupID,
											clone.file.fileID)).average()
							.getAsDouble();

					return Double.compare(average1, average2);
				});

		int index = 0;
		for (final GUICloneSet cloneset : clonesets) {
			this.offsets.put(cloneset, index++);
		}

		this.initialized = true;
	}

	private CloneMiddlePositionOffsetData() {
		this.offsets = new HashMap<>();
		this.initialized = false;
	}

	private final Map<GUICloneSet, Integer> offsets;
	private boolean initialized;
}
