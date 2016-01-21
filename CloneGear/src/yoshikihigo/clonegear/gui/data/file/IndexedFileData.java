package yoshikihigo.clonegear.gui.data.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class IndexedFileData {

	private static IndexedFileData SINGLETON = null;

	public static IndexedFileData instance() {
		return SINGLETON;
	}

	public static void initialize(final GUIFileManager manager) {
		SINGLETON = new IndexedFileData();
		for (final GUIFile file : manager.getFiles()) {
			final int index = IDIndexMap.instance().getIndex(file.groupID,
					file.fileID);
			final IndexedFile indexedFile = new IndexedFile(
					new File(file.path), index, index);
			SINGLETON.indexedFiles.put(file, indexedFile);
		}
	}

	public IndexedFile get(final GUIFile file) {
		assert null != SINGLETON : "IndexedFileData has not been initialized yet.";
		return this.indexedFiles.get(file);
	}

	private IndexedFileData() {
		this.indexedFiles = new HashMap<>();
	}

	private final Map<GUIFile, IndexedFile> indexedFiles;
}
