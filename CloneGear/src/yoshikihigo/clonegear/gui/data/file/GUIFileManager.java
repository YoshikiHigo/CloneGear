package yoshikihigo.clonegear.gui.data.file;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;

public final class GUIFileManager {

	private static GUIFileManager SINGLETON = null;

	public static void initialize() {
		SINGLETON = new GUIFileManager();
	}

	public static GUIFileManager instance() {
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}

	public void add(final GUIFile file) {
		this.idFiles
				.put(GUIFileKey.getFileKey(file.groupID, file.fileID), file);
		this.pathFiles.put(file.path, file);
	}

	public int getFileCount() {
		return this.idFiles.size();
	}

	public int getFileCount(final int groupID) {
		return this.getFiles(groupID).size();
	}

	public int getGroupCount() {
		GUIFileKey fileKey = this.idFiles.lastKey();
		return fileKey.groupID + 1;
	}

	public GUIFile getFile(final String path) {
		return this.pathFiles.get(path);
	}

	public GUIFile getFile(final int groupID, final int fileID) {
		return this.idFiles.get(GUIFileKey.getFileKey(groupID, fileID));
	}

	public List<GUIFile> getFiles(final int groupID) {
		final GUIFileKey fromKey = GUIFileKey.getFileKey(groupID, 0);
		final GUIFileKey toKey = GUIFileKey.getFileKey(groupID + 1, 0);
		return new ArrayList<GUIFile>(this.idFiles.subMap(fromKey, toKey)
				.values());
	}

	public List<GUIFile> getFiles() {
		return new ArrayList<GUIFile>(this.idFiles.values());
	}

	public List<GUIFile> getGroup(final int groupID) {
		return this.getFiles(groupID);
	}

	public List<List<GUIFile>> getGroups() {
		final int groupCount = this.getGroupCount();
		final List<List<GUIFile>> groups = new ArrayList<>();
		for (int i = 0; i < groupCount; i++) {
			final List<GUIFile> group = this.getGroup(i);
			groups.add(group);
		}
		return groups;
	}

	public GUIFile getFirstFile(final int groupID) {
		return this.getFile(groupID, 0);
	}

	public GUIFile getLastFile(final int groupID) {
		final SortedMap<GUIFileKey, GUIFile> headMap = this.idFiles
				.headMap(GUIFileKey.getFileKey(groupID + 1, 0));
		final GUIFileKey key = headMap.lastKey();
		return this.getFile(key.groupID, key.fileID);
	}

	public List<GUIFile> getRelatedFiles(final GUIFile file) {
		return this.getFiles().stream().filter(f -> !f.equals(file))
				.filter(f -> GUICloneManager.instance().hasClonePairs(f, file))
				.sorted().collect(Collectors.toList());
	}

	public List<GUIFile> getRelatedFiles(final GUIFile file, final int threshold) {
		return this
				.getFiles()
				.stream()
				.filter(f -> !f.equals(file))
				.filter(f -> GUICloneManager.instance().hasClonePairs(f, file,
						threshold)).sorted().collect(Collectors.toList());
	}

	public int getTotalLOC() {
		return this.getFiles().stream().mapToInt(file -> file.loc).sum();
	}

	public double getTotalROC() {
		return this.getFiles().stream()
				.mapToDouble(file -> file.getROC() * file.loc).average()
				.orElse(0d);
	}

	public double getTotalROC(final int threshold) {
		return this.getFiles().stream()
				.mapToDouble(file -> file.getROC(threshold) * file.loc)
				.average().orElse(0d);
	}

	public int getGroupLOC(final int groupID) {
		return this.getFiles(groupID).stream().mapToInt(file -> file.loc).sum();
	}

	public double getGroupROC(final int groupID) {
		return this.getGroupROC(groupID, 0);
	}

	public double getGroupROC(final int groupID, final int threshold) {
		final List<GUIFile> files = this.getFiles(groupID);
		final int cloc = files.stream()
				.mapToInt(file -> file.getCLOC(threshold)).sum();
		final int loc = files.stream().mapToInt(file -> file.loc).sum();
		return 100d * cloc / loc;
	}

	public int getGroupNOC(final int groupID) {
		return this.getFiles(groupID).stream().mapToInt(file -> file.getNOC())
				.sum();
	}

	public int getGroupNOC(final int groupID, final int threshold) {
		return this.getFiles(groupID).stream()
				.mapToInt(file -> file.getNOC(threshold)).sum();
	}

	public List<GUIClone> getGroupClones(final int groupID) {
		final SortedSet<GUIClone> clones = new TreeSet<>();
		this.getFiles(groupID).forEach(file -> {
			clones.addAll(file.getClones());
		});
		return new ArrayList<GUIClone>(clones);
	}

	public List<GUIClone> getGroupClones(final int groupID, final int threshold) {
		final SortedSet<GUIClone> clones = new TreeSet<>();
		this.getFiles(groupID).forEach(file -> {
			clones.addAll(file.getClones(threshold));
		});
		return new ArrayList<GUIClone>(clones);
	}

	private GUIFileManager() {
		this.idFiles = new TreeMap<>();
		this.pathFiles = new TreeMap<>();
	}

	final private SortedMap<GUIFileKey, GUIFile> idFiles;
	final private SortedMap<String, GUIFile> pathFiles;
}
