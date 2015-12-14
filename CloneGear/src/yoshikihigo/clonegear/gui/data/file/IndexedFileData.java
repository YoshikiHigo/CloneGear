package yoshikihigo.clonegear.gui.data.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class IndexedFileData {

	public static final IndexedFileData SINGLETON = new IndexedFileData();

	public IndexedFile get(final GUIFile file) {
		assert this.initialized : "IndexedFileData has not been initialized yet.";
		return this.indexedFiles.get(file);
	}

	public void initialize(final GUIFileManager manager) {
		assert !this.initialized : "IndexedFileData has already been initialized.";

		manager.getFiles()
				.stream()
				.forEach(
						file -> {
							final int index = IDIndexMap.SINGLETON.getIndex(
									file.groupID, file.fileID);
							final IndexedFile indexedFile = new IndexedFile(
									new File(file.path), index, index);
							this.indexedFiles.put(file, indexedFile);
						});
		this.initialized = true;
	}

	private IndexedFileData() {
		this.indexedFiles = new HashMap<>();
		this.initialized = false;
	}

	private final Map<GUIFile, IndexedFile> indexedFiles;
	private boolean initialized;
}
