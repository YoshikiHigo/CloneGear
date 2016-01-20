package yoshikihigo.clonegear.gui.data.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import yoshikihigo.clonegear.gui.data.Entity;
import yoshikihigo.clonegear.gui.data.HavingClones;
import yoshikihigo.clonegear.gui.data.clone.GUIClone;

public class GUIFile implements Comparable<GUIFile>, Entity, HavingClones {

	static final private Map<String, GUIFile> GUIFILES = new HashMap<>();
	static final private AtomicInteger ID_GENERATOR = new AtomicInteger(1);

	static public GUIFile getGUIFile(final String path) {
		return getGUIFile(0, path, 0);
	}

	static public GUIFile getGUIFile(final int groupID, final String path,
			final int loc) {
		GUIFile file = GUIFILES.get(path);
		if (null == file) {
			file = new GUIFile(groupID, path, loc);
			GUIFILES.put(path, file);
		}
		return file;
	}

	final public int groupID;
	final public int fileID;
	final public int loc;
	final public String path;

	private GUIFile(final int groupID, final String path, final int loc) {
		this.groupID = groupID;
		this.fileID = ID_GENERATOR.getAndIncrement();
		this.path = path;
		this.loc = loc;
	}

	public final String getFileName() {
		final String[] names = this.path.split(System.lineSeparator());
		return names[names.length - 1];
	}

	@Override
	public int compareTo(final GUIFile fileInfo) {

		final int groupIDComprisonResults = Integer.compare(this.groupID,
				fileInfo.groupID);
		if (0 != groupIDComprisonResults) {
			return groupIDComprisonResults;
		}

		return Integer.compare(this.fileID, fileInfo.fileID);
	}

	@Override
	public boolean equals(final Object o) {

		if (null == o) {
			return false;
		}

		if (!(o instanceof GUIFile)) {
			return false;
		}

		final GUIFile file = (GUIFile) o;
		return this.path.equals(file.path);
	}

	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	@Override
	public List<GUIClone> getClones() {
		return GUIFileMetricsManager.SINGLETON.getClones(this);
	}

	public List<GUIClone> getClones(final int threshold) {
		return GUIFileMetricsManager.SINGLETON.getClones(this, threshold);
	}

	public List<GUIClone> getClonesWith(final GUIFile file) {
		return GUIFileMetricsManager.SINGLETON.getClones(this, file);
	}

	public List<GUIClone> getClonesWith(final GUIFile file, final int threshold) {
		return GUIFileMetricsManager.SINGLETON.getClones(this, file, threshold);
	}

	public int getNOC() {
		return GUIFileMetricsManager.SINGLETON.getNOC(this);
	}

	public int getNOC(final int threshold) {
		return GUIFileMetricsManager.SINGLETON.getNOC(this, threshold);
	}

	public int getNOCwith(final GUIFile target) {
		return GUIFileMetricsManager.SINGLETON.getNOC(this, target);
	}

	public int getNOCwith(final GUIFile target, final int threshold) {
		return GUIFileMetricsManager.SINGLETON.getNOC(this, target, threshold);
	}

	public int getCLOC() {
		return GUIFileMetricsManager.SINGLETON.getCLOC(this);
	}

	public int getCLOC(final int threshold) {
		return GUIFileMetricsManager.SINGLETON.getCLOC(this, threshold);
	}
	
	public double getROC() {
		return GUIFileMetricsManager.SINGLETON.getROC(this);
	}

	public double getROC(final int threshold) {
		return GUIFileMetricsManager.SINGLETON.getROC(this, threshold);
	}

	public double getROCwith(final GUIFile target) {
		return GUIFileMetricsManager.SINGLETON.getROC(this, target);
	}

	public double getROCwith(final GUIFile target, final int threshold) {
		return GUIFileMetricsManager.SINGLETON.getROC(this, target, threshold);
	}

	public int getNOF() {
		return GUIFileManager.SINGLETON.getRelatedFiles(this).size();
	}

	public int getNOF(final int threshold) {
		return GUIFileManager.SINGLETON.getRelatedFiles(this, threshold).size();
	}
}
