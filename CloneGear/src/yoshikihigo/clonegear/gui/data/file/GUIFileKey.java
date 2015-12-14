package yoshikihigo.clonegear.gui.data.file;

import java.util.HashMap;
import java.util.Map;

public class GUIFileKey implements Comparable<GUIFileKey> {

	final public int groupID;
	final public int fileID;

	private GUIFileKey(final int groupID, final int fileID) {
		this.groupID = groupID;
		this.fileID = fileID;
	}

	@Override
	public int compareTo(final GUIFileKey target) {
		final int groupIDComparisonResults = Integer.compare(this.groupID,
				target.groupID);
		if (0 != groupIDComparisonResults) {
			return groupIDComparisonResults;
		}

		final int fileIDComparisonResults = Integer.compare(this.fileID,
				target.fileID);
		return fileIDComparisonResults;
	}

	static private Map<Integer, Map<Integer, GUIFileKey>> FILEKEY_MAP = new HashMap<>();

	static public GUIFileKey getFileKey(final int groupID, final int fileID) {
		Map<Integer, GUIFileKey> map = FILEKEY_MAP.get(groupID);
		if (null == map) {
			map = new HashMap<>();
			FILEKEY_MAP.put(groupID, map);
		}
		GUIFileKey fileKey = map.get(fileID);
		if (null == fileKey) {
			fileKey = new GUIFileKey(groupID, fileID);
			map.put(fileID, fileKey);
		}
		return fileKey;
	}
}
