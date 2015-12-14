package yoshikihigo.clonegear.gui.data.file;

import java.util.HashMap;
import java.util.Map;

public final class GUIFileLabelManager {

	public final static GUIFileLabelManager SINGLETON = new GUIFileLabelManager();

	public String getIDLabel(final GUIFile file) {
		String label = this.idLabels.get(file);
		if (null == label) {
			label = this.createLabel(file);
			this.idLabels.put(file, label);
		}
		return label;
	}

	private GUIFileLabelManager() {
		this.idLabels = new HashMap<>();
	}

	private String createLabel(final GUIFile file) {
		final StringBuffer labelBuffer = new StringBuffer();
		labelBuffer.append(" ");
		labelBuffer.append(file.groupID);
		labelBuffer.append(".");
		labelBuffer.append(file.fileID);
		labelBuffer.append(" ");
		return labelBuffer.toString();
	}

	private final Map<GUIFile, String> idLabels;
}
