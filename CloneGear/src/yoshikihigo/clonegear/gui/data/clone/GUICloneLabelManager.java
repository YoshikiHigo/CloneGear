package yoshikihigo.clonegear.gui.data.clone;

import java.util.HashMap;
import java.util.Map;

public final class GUICloneLabelManager {

	public final static GUICloneLabelManager SINGLETON = new GUICloneLabelManager();

	public String getIDLabel(final GUIClone clone) {
		String label = this.idLabels.get(clone);
		if (null == label) {
			label = this.createIDLabel(clone);
			this.idLabels.put(clone, label);
		}
		return label;
	}

	public String getLocationLabel(final GUIClone clone) {
		String label = this.locationLabels.get(clone);
		if (null == label) {
			label = this.createLocationLabel(clone);
			this.locationLabels.put(clone, label);
		}
		return label;
	}

	private GUICloneLabelManager() {
		this.idLabels = new HashMap<>();
		this.locationLabels = new HashMap<>();
	}

	private String createIDLabel(final GUIClone codeFragment) {
		final StringBuffer labelBuffer = new StringBuffer();
		labelBuffer.append(codeFragment.groupID);
		labelBuffer.append(".");
		labelBuffer.append(codeFragment.fileID);
		return labelBuffer.toString();
	}

	private String createLocationLabel(final GUIClone codeFragment) {
		final StringBuffer labelBuffer = new StringBuffer();
		labelBuffer.append(" ");
		labelBuffer.append(codeFragment.fromLine);
		labelBuffer.append(" - ");
		labelBuffer.append(codeFragment.toLine);
		labelBuffer.append(" ");
		return labelBuffer.toString();
	}

	private final Map<GUIClone, String> idLabels;
	private final Map<GUIClone, String> locationLabels;
}
