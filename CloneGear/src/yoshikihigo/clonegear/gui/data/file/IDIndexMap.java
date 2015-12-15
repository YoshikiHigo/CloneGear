package yoshikihigo.clonegear.gui.data.file;

import java.util.HashMap;
import java.util.Map;

public class IDIndexMap {

	public static final IDIndexMap SINGLETON = new IDIndexMap();

	public GUIFile getFile(final int index) {
		assert this.initialized : "IDIndexMap has not been initialized yet.";
		return this.reverseIndexes.get(index);
	}

	public int getIndex(final int groupID, final int fileID) {
		assert this.initialized : "IDIndexMap has not been initialized yet.";
		final GUIFile file = GUIFileManager.SINGLETON.getFile(groupID, fileID);
		return this.indexes.get(file);
	}

	public int searchFileIndex(final int position, int lowIndex, int highIndex) {
		assert this.initialized : "IDIndexMap has not been initialized yet.";

		final int medial = (lowIndex + highIndex) / 2;
		final GUIFile file = this.getFile(medial);
		final int fileOffset = FileOffsetData.SINGLETON.get(file);

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

	public void initialize(final GUIFileManager manager) {
		assert !this.initialized : "IDIndexMap has already been initialized.";
		int index = 0;
		for (final GUIFile file : manager.getFiles()) {
			this.indexes.put(file, index);
			this.reverseIndexes.put(index++, file);
		}
		this.initialized = true;
	}

	private IDIndexMap() {
		this.indexes = new HashMap<>();
		this.reverseIndexes = new HashMap<>();
		this.initialized = false;
	}

	final private Map<GUIFile, Integer> indexes;
	final private Map<Integer, GUIFile> reverseIndexes;
	private boolean initialized;
}
