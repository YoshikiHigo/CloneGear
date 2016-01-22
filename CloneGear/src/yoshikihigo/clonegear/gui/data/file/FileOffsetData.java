package yoshikihigo.clonegear.gui.data.file;

import java.util.HashMap;
import java.util.Map;

public class FileOffsetData {

	private static FileOffsetData SINGLETON = null;

	public static FileOffsetData instance() {
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}

	public static void initialize(final GUIFileManager manager) {
		SINGLETON = new FileOffsetData();
		int offset = 0;
		for (final GUIFile file : manager.getFiles()) {
			SINGLETON.offsets.put(file, offset);
			offset += file.loc;
		}
	}

	public int get(final GUIFile file) {
		return this.offsets.get(file);
	}

	private FileOffsetData() {
		this.offsets = new HashMap<>();
	}

	final private Map<GUIFile, Integer> offsets;
}
