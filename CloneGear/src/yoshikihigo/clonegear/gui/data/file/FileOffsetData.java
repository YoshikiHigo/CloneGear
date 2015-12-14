package yoshikihigo.clonegear.gui.data.file;

import java.util.HashMap;
import java.util.Map;

public class FileOffsetData {

	public static final FileOffsetData SINGLETON = new FileOffsetData();

	public int get(final GUIFile file) {
		assert this.initialized : "FileOffsetData has not been initialized yet.";
		return this.offsets.get(file);
	}

	public void initialize(final GUIFileManager manager) {
		assert !this.initialized : "FileOffsetData has already been initialized.";
		int offset = 0;
		for (final GUIFile file : manager.getFiles()) {
			this.offsets.put(file, offset);
			offset += file.loc;
		}
		this.initialized = true;
	}

	private FileOffsetData() {
		this.offsets = new HashMap<>();
		this.initialized = false;
	}

	final private Map<GUIFile, Integer> offsets;
	private boolean initialized;
}
