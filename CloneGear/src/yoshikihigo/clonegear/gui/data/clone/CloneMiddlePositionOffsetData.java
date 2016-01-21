package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.gui.data.file.IDIndexMap;

public class CloneMiddlePositionOffsetData {

	private static CloneMiddlePositionOffsetData SINGLETON = null;

	public static CloneMiddlePositionOffsetData instance() {
		return SINGLETON;
	}

	public static void initialize(final GUICloneManager manager) {

		SINGLETON = new CloneMiddlePositionOffsetData();
		final List<GUICloneSet> clonesets = new ArrayList<>(
				manager.getCloneSets());
		Collections.sort(
				clonesets,
				(cloneset1, cloneset2) -> {
					final double average1 = cloneset1
							.getClones()
							.stream()
							.mapToInt(
									clone -> IDIndexMap.instance().getIndex(
											clone.file.groupID,
											clone.file.fileID)).average()
							.getAsDouble();
					final double average2 = cloneset2
							.getClones()
							.stream()
							.mapToInt(
									clone -> IDIndexMap.instance().getIndex(
											clone.file.groupID,
											clone.file.fileID)).average()
							.getAsDouble();
					return Double.compare(average1, average2);
				});

		int index = 0;
		for (final GUICloneSet cloneset : clonesets) {
			SINGLETON.offsets.put(cloneset, index++);
		}
	}

	public int get(final GUICloneSet cloneset) {
		return this.offsets.get(cloneset);
	}

	private CloneMiddlePositionOffsetData() {
		this.offsets = new HashMap<>();
	}

	private final Map<GUICloneSet, Integer> offsets;
}
