package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.IDIndexMap;

public class CloneRangeOffsetData {

	public static final CloneRangeOffsetData SINGLETON = new CloneRangeOffsetData();

	public int get(final GUICloneSet cloneSet) {
		assert this.initialized : "CloneRangeOffsetData was not initialized.";
		return this.offsets.get(cloneSet);
	}

	public void initialize(final GUICloneManager manager) {

		final List<GUICloneSet> clonesets = new ArrayList<>(
				manager.getCloneSets());
		Collections.sort(
				clonesets,
				(cloneset1, cloneset2) -> {

					final GUIFile first1file = cloneset1.first().file;
					final int first1groupID = first1file.groupID;
					final int first1fileID = first1file.fileID;
					final int first1index = IDIndexMap.SINGLETON.getIndex(
							first1groupID, first1fileID);

					final GUIFile last1file = cloneset1.last().file;
					final int last1groupID = last1file.groupID;
					final int last1fileID = last1file.fileID;
					final int last1index = IDIndexMap.SINGLETON.getIndex(
							last1groupID, last1fileID);

					final GUIFile first2file = cloneset2.first().file;
					final int first2groupID = first2file.groupID;
					final int first2fileID = first2file.fileID;
					final int first2index = IDIndexMap.SINGLETON.getIndex(
							first2groupID, first2fileID);

					final GUIFile last2file = cloneset2.last().file;
					final int last2groupID = last2file.groupID;
					final int last2fileID = last2file.fileID;
					final int last2index = IDIndexMap.SINGLETON.getIndex(
							last2groupID, last2fileID);

					return Integer.compare((last1index - first1index),
							(last2index - first2index));
				});

		int index = 0;
		for (final GUICloneSet cloneset : clonesets) {
			this.offsets.put(cloneset, index++);
		}

		this.initialized = true;
	}

	private CloneRangeOffsetData() {
		this.offsets = new HashMap<>();
		this.initialized = false;
	}

	private final Map<GUICloneSet, Integer> offsets;
	private boolean initialized;
}
