package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import yoshikihigo.clonegear.gui.data.HavingClones;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager.ClonePairData.FilePair;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;

public final class GUICloneManager {

	private static GUICloneManager SINGLETON = null;

	public static void initialize() {
		SINGLETON = new GUICloneManager();
	}

	public static GUICloneManager instance() {
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}

	public final static <T extends HavingClones> List<GUIClone> getClones(
			final Collection<T> entities) {
		return GUICloneManager.getClones(entities, 0);
	}

	public final static <T extends HavingClones> List<GUIClone> getClones(
			final Collection<T> entities, final int threshold) {

		final SortedSet<GUIClone> clones = new TreeSet<>();
		entities.stream().forEach(
				entity -> {
					clones.addAll(entity.getClones().stream()
							.filter(clone -> threshold <= clone.getRNR())
							.collect(Collectors.toList()));
				});

		return new ArrayList<GUIClone>(clones);
	}

	public int getCloneSetCount() {
		return this.clonesets.size();
	}

	public int getCloneSetCount(final int threshold) {
		return (int) this.getCloneSets().stream()
				.filter(cloneset -> threshold <= cloneset.getRNR()).count();
	}

	public int getClonePairCount() {
		return this.getClonePairCount(0);
	}

	public int getClonePairCount(final int threshold) {
		return this.getCloneSets(threshold).stream()
				.mapToInt(cloneset -> cloneset.getClonepairs().size()).sum();
	}

	public GUICloneSet getCloneSet(final int id) {
		return this.clonesets.get(id);
	}

	public GUICloneSet getCloneSet(final GUIClone clone) {
		return this.clonesets.get(clone.clonesetID);
	}

	public List<GUICloneSet> getCloneSets() {
		return new ArrayList<GUICloneSet>(this.clonesets.values());
	}

	public List<GUICloneSet> getCloneSets(final int threshold) {
		return this.getCloneSets().stream()
				.filter(cloneset -> threshold <= cloneset.getRNR())
				.collect(Collectors.toList());
	}

	public List<GUIClonePair> getClonePairs(final int groupID1,
			final int fileID1, final int groupID2, final int fileID2) {
		final GUIFile file1 = GUIFileManager.instance().getFile(groupID1,
				fileID1);
		final GUIFile file2 = GUIFileManager.instance().getFile(groupID2,
				fileID2);
		return this.clonepairs.getClonePairs(file1, file2);
	}

	public List<GUIClonePair> getClonePairs(final int groupID1,
			final int fileID1, final int groupID2, final int fileID2,
			final int threshold) {
		final GUIFile file1 = GUIFileManager.instance().getFile(groupID1,
				fileID1);
		final GUIFile file2 = GUIFileManager.instance().getFile(groupID2,
				fileID2);
		return this.clonepairs.getClonePairs(file1, file2, threshold);
	}

	public List<GUIClonePair> getClonePairs(final GUIFile file1,
			final GUIFile file2) {
		return this.clonepairs.getClonePairs(file1, file2);
	}

	public List<GUIClonePair> getClonePairs(final GUIFile file1,
			final GUIFile file2, final int threshold) {
		return this.clonepairs.getClonePairs(file1, file2, threshold);
	}

	public boolean hasClonePairs(final int groupID1, final int fileID1,
			final int groupID2, final int fileID2) {
		final GUIFile file1 = GUIFileManager.instance().getFile(groupID1,
				fileID1);
		final GUIFile file2 = GUIFileManager.instance().getFile(groupID2,
				fileID2);
		return this.clonepairs.hasClonePairs(file1, file2);
	}

	public boolean hasClonePairs(final int groupID1, final int fileID1,
			final int groupID2, final int fileID2, final int threshold) {
		final GUIFile file1 = GUIFileManager.instance().getFile(groupID1,
				fileID1);
		final GUIFile file2 = GUIFileManager.instance().getFile(groupID2,
				fileID2);
		return this.clonepairs.hasClonePairs(file1, file2, threshold);
	}

	public boolean hasClonePairs(final GUIFile file1, final GUIFile file2) {
		return this.clonepairs.hasClonePairs(file1, file2);
	}

	public boolean hasClonePairs(final GUIFile file1, final GUIFile file2,
			final int threshold) {
		return this.clonepairs.hasClonePairs(file1, file2, threshold);
	}

	public List<GUIClone> getClones() {
		return new ArrayList<GUIClone>(this.clones);
	}

	public List<GUIClone> getClones(final int threshold) {
		return this.clones.stream()
				.filter(clone -> threshold <= clone.getRNR())
				.collect(Collectors.toList());
	}

	public List<GUIClone> getGroupClones(final int groupID) {
		return this.getGroupClones(groupID, 0);
	}

	public List<GUIClone> getGroupClones(final int groupID, final int threshold) {

		final GUIClone fromClone = new GUIClone(groupID, 0);
		final GUIClone toClone = new GUIClone(groupID + 1, 0);
		return this.clones.tailSet(fromClone).headSet(toClone).stream()
				.filter(clone -> threshold <= clone.getRNR())
				.collect(Collectors.toList());
	}

	public List<GUIClone> getFileClones(final GUIFile file) {
		return this.getFileClones(file.groupID, file.fileID);
	}

	public List<GUIClone> getFileClones(final int groupID, final int fileID) {
		return this.getFileClones(groupID, fileID, 0);
	}

	public List<GUIClone> getFileClones(final GUIFile file, final int threshold) {
		return this.getFileClones(file.groupID, file.fileID, threshold);
	}

	public List<GUIClone> getFileClones(final int groupID, final int fileID,
			final int threshold) {

		GUIClone fromDummyClone = new GUIClone(groupID, fileID);
		GUIClone toDummyClone = new GUIClone(groupID, fileID + 1);
		return this.clones.tailSet(fromDummyClone).headSet(toDummyClone)
				.stream().filter(clone -> threshold <= clone.getRNR())
				.collect(Collectors.toList());
	}

	public Set<FilePair> getClonePairFileKeys() {
		return this.clonepairs.getFileKeys();
	}

	public List<GUIClonePair> getClonePairs(final FilePair fileKey) {
		return this.clonepairs.getClonePairs(fileKey.left, fileKey.right);
	}

	public double getGroupROC(final int groupID) {
		return GUIFileManager.instance().getGroupROC(groupID);
	}

	public double getGroupROC(final int groupID, final int threshold) {
		return GUIFileManager.instance().getGroupROC(groupID, threshold);
	}

	public int getGroupNOC(final int groupID) {
		return GUIFileManager.instance().getGroupNOC(groupID);
	}

	public int getGroupNOC(final int groupID, final int threshold) {
		return GUIFileManager.instance().getGroupNOC(groupID, threshold);
	}

	public int getMaxDFL() {
		return this.getCloneSets().stream()
				.max((c1, c2) -> Integer.compare(c1.getDFL(), c2.getDFL()))
				.get().getDFL();
	}

	public int getMaxLEN() {
		return this.getCloneSets().stream()
				.max((c1, c2) -> Integer.compare(c1.getLEN(), c2.getLEN()))
				.get().getLEN();
	}

	public int getMaxNIF() {
		return this.getCloneSets().stream()
				.max((c1, c2) -> Integer.compare(c1.getNIF(), c2.getNIF()))
				.get().getNIF();
	}

	public int getMaxPOP() {
		return this.getCloneSets().stream()
				.max((c1, c2) -> Integer.compare(c1.getPOP(), c2.getPOP()))
				.get().getPOP();
	}

	public int getMaxRAD() {
		return this.getCloneSets().stream()
				.max((c1, c2) -> Integer.compare(c1.getRAD(), c2.getRAD()))
				.get().getRAD();
	}

	public int getMaxRNR() {
		return 100;
	}

	private GUICloneManager() {
		this.clonesets = new TreeMap<>();
		this.clonepairs = new ClonePairData();
		this.clones = new TreeSet<>();
	}

	static public class ClonePairData {

		private final Map<FilePair, SortedSet<GUIClonePair>> clonePairData;

		ClonePairData() {
			this.clonePairData = new HashMap<>();
		}

		Set<FilePair> getFileKeys() {
			return this.clonePairData.keySet();
		}

		void add(final GUIClonePair clonepair) {
			final FilePair fileKey = new FilePair(clonepair.left.file,
					clonepair.right.file);
			SortedSet<GUIClonePair> clonepairs = this.clonePairData
					.get(fileKey);
			if (null == clonepairs) {
				clonepairs = new TreeSet<>();
				this.clonePairData.put(fileKey, clonepairs);
			}
			clonepairs.add(clonepair);
		}

		List<GUIClonePair> getClonePairs(final GUIFile file1,
				final GUIFile file2) {
			final FilePair key = new FilePair(file1, file2);
			final SortedSet<GUIClonePair> clonePairs = this.clonePairData
					.get(key);
			return null != clonePairs ? new ArrayList<GUIClonePair>(clonePairs)
					: new ArrayList<GUIClonePair>();
		}

		List<GUIClonePair> getClonePairs(final GUIFile file1,
				final GUIFile file2, final int threshold) {
			return this.getClonePairs(file1, file2).stream()
					.filter(clonepair -> threshold <= clonepair.getRNR())
					.collect(Collectors.toList());
		}

		boolean hasClonePairs(final GUIFile file1, final GUIFile file2) {
			final FilePair key = new FilePair(file1, file2);
			return this.clonePairData.containsKey(key);
		}

		boolean hasClonePairs(final GUIFile file1, final GUIFile file2,
				final int threshold) {
			return this.getClonePairs(file1, file2).stream()
					.anyMatch(clonepair -> threshold <= clonepair.getRNR());
		}

		static public final class FilePair {

			FilePair(final GUIFile left, final GUIFile right) {
				if (left.compareTo(right) <= 0) {
					this.left = left;
					this.right = right;
				} else {
					this.left = right;
					this.right = left;
				}
			}

			@Override
			public boolean equals(Object o) {

				if (null == o) {
					return false;
				}

				if (!(o instanceof FilePair)) {
					return false;
				}

				final FilePair pair = (FilePair) o;
				return this.left.equals(pair.left)
						&& this.right.equals(pair.right);
			}

			@Override
			public int hashCode() {
				return this.left.hashCode() + this.right.hashCode();
			}

			final public GUIFile left;
			final public GUIFile right;
		}
	}

	public void addClonepair(final GUIClonePair clonepair) {
		GUICloneSet cloneset = clonesets.get(clonepair.clonesetID);
		if (null == cloneset) {
			cloneset = new GUICloneSet(clonepair.clonesetID);
			clonesets.put(clonepair.clonesetID, cloneset);
		}
		cloneset.addClonepair(clonepair);
		clonepairs.add(clonepair);
		this.clones.add(clonepair.left);
		this.clones.add(clonepair.right);
	}

	private final SortedMap<Integer, GUICloneSet> clonesets;
	private final ClonePairData clonepairs;
	private final SortedSet<GUIClone> clones;
}
