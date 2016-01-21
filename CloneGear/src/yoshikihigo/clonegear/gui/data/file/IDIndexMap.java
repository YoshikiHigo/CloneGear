package yoshikihigo.clonegear.gui.data.file;

import java.util.HashMap;
import java.util.Map;

public class IDIndexMap {

	private static IDIndexMap SINGLETON = null;

	public static IDIndexMap instance() {
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}

	public static void initialize(final GUIFileManager manager) {
		SINGLETON = new IDIndexMap();
		int index = 0;
		for (final GUIFile file : manager.getFiles()) {
			SINGLETON.indexes.put(file, index);
			SINGLETON.reverseIndexes.put(index++, file);
		}
	}

	public GUIFile getFile(final int index) {
		assert null != SINGLETON : "IDIndexMap has not been initialized yet.";
		return this.reverseIndexes.get(index);
	}

	public int getIndex(final int groupID, final int fileID) {
		assert null != SINGLETON : "IDIndexMap has not been initialized yet.";
		final GUIFile file = GUIFileManager.instance().getFile(groupID, fileID);
		return this.indexes.get(file);
	}

	public int searchFileIndex(final int position, int lowIndex, int highIndex) {
		assert null != SINGLETON : "IDIndexMap has not been initialized yet.";

		final int medial = (lowIndex + highIndex) / 2;
		final GUIFile file = this.getFile(medial);
		final int fileOffset = FileOffsetData.instance().get(file);

		if (0 < (fileOffset - position)) {
			return this.searchFileIndex(position, lowIndex, medial - 1);
		}

		else if ((fileOffset + file.loc - position) <= 0) {
			return this.searchFileIndex(position, medial + 1, highIndex);
		}

		else {
			return medial;
		}
	}

	private IDIndexMap() {
		this.indexes = new HashMap<>();
		this.reverseIndexes = new HashMap<>();
	}

	final private Map<GUIFile, Integer> indexes;
	final private Map<Integer, GUIFile> reverseIndexes;
}
