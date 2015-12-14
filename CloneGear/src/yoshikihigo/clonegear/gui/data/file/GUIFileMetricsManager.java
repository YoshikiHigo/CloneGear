package yoshikihigo.clonegear.gui.data.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;

final class GUIFileMetricsManager {

	static GUIFileMetricsManager SINGLETON = new GUIFileMetricsManager();

	int getNOC(final GUIFile file) {
		return this.getClones(file).size();
	}

	int getNOC(final GUIFile file, final int threshold) {
		return this.getClones(file, threshold).size();
	}

	int getNOC(final GUIFile file, final GUIFile target) {
		return this.getClones(file, target).size();
	}

	int getNOC(final GUIFile file, final GUIFile target, final int threshold) {
		return this.getClones(file, target, threshold).size();
	}

	List<GUIClone> getClones(final GUIFile file) {
		return this.getClones(file, 0);
	}

	List<GUIClone> getClones(final GUIFile file, final int threshold) {

		List<GUIClone> clones = this.clones.get(file);
		if (null == clones) {
			clones = this.createClones(file);
			this.clones.put(file, clones);
		}

		return clones.stream().filter(clone -> threshold <= clone.getRNR())
				.collect(Collectors.toList());
	}

	List<GUIClone> getClones(final GUIFile file, final GUIFile target) {
		return this.getClones(file, target, 0);
	}

	List<GUIClone> getClones(final GUIFile file, final GUIFile target,
			final int threshold) {
		final SortedSet<GUIClone> clones = new TreeSet<>();
		GUICloneManager.SINGLETON.getClonePairs(file, target, threshold)
				.forEach(clonepair -> {
					if (clonepair.left.file.equals(this)) {
						clones.add(clonepair.left);
					}
					if (clonepair.right.file.equals(this)) {
						clones.add(clonepair.right);
					}
				});
		return new ArrayList<GUIClone>(clones);
	}

	double getROC(final GUIFile file) {
		return this.getROC(file, 0);
	}

	double getROC(final GUIFile file, final int threshold) {

		int[] lines = this.lines.get(file);
		if (null == lines) {
			lines = this.createLines(file);
			this.lines.put(file, lines);
		}

		int clonedTokenNumber = 0;
		for (int i = 0; i < lines.length; i++) {
			if (threshold <= lines[i]) {
				clonedTokenNumber++;
			}
		}

		return 100.0d * clonedTokenNumber / lines.length;
	}

	double getROC(final GUIFile file, final GUIFile target) {
		return getROC(file, target, 0);
	}

	double getROC(final GUIFile file, final GUIFile target, final int threshold) {

		int[] lines = this.createLines(file, this.getClones(file, target));

		int clonedTokenNumber = 0;
		for (int i = 0; i < lines.length; i++) {
			if (threshold <= lines[i]) {
				clonedTokenNumber++;
			}
		}

		return 100.0d * clonedTokenNumber / lines.length;
	}

	private List<GUIClone> createClones(final GUIFile file) {
		return GUICloneManager.SINGLETON.getFileClones(file.groupID,
				file.fileID);
	}

	private int[] createLines(final GUIFile file) {
		return this.createLines(file, file.getClones());
	}

	private int[] createLines(final GUIFile file,
			final Collection<GUIClone> clones) {

		final int[] lines = new int[file.loc];
		Arrays.fill(lines, -1);

		clones.stream().forEach(clone -> {
			final int rnr = clone.getRNR();
			for (int i = clone.fromLine; i <= clone.toLine; i++) {
				if (lines[i] < 0) {
					lines[i] = rnr;
				} else if (rnr < lines[i]) {
					lines[i] = rnr;
				}
			}
		});

		return lines;
	}

	private GUIFileMetricsManager() {
		this.clones = new HashMap<>();
		this.lines = new HashMap<>();
	}

	private final Map<GUIFile, List<GUIClone>> clones;
	private final Map<GUIFile, int[]> lines;
}
