package yoshikihigo.clonegear.gui.data.clone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import yoshikihigo.clonegear.gui.util.PathCompare;

final class GUICloneMetricsManager {

	static final GUICloneMetricsManager SINGLETON = new GUICloneMetricsManager();

	private static int calculateDFL(final GUICloneSet cloneset) {
		final List<GUIClone> clones = cloneset.getClones();
		final int totalLEN = clones.stream()
				.mapToInt(clone -> (clone.toLine - clone.fromLine)).sum();
		return totalLEN - (totalLEN / clones.size());
	}

	private static int calculateLEN(final GUICloneSet cloneset) {
		return (int) cloneset.getClones().stream()
				.mapToInt(clone -> clone.getLOC()).average().getAsDouble();
	}

	private static int calculateNIF(final GUICloneSet cloneset) {
		return cloneset.getClones().stream().map(clone -> clone.file.path)
				.collect(Collectors.toSet()).size();
	}

	private static int calculatePOP(final GUICloneSet cloneset) {
		return cloneset.size();
	}

	private static int calculateRAD(final GUICloneSet cloneset) {

		final Set<String> paths = cloneset.getClones().stream()
				.map(clone -> clone.file.path).collect(Collectors.toSet());

		int differentIndex = PathCompare.getDifferentIndex(paths
				.toArray(new String[0]));
		int maxSeparatorCount = 0;

		for (String path : paths) {
			final int count = PathCompare.countHierarchy(differentIndex, path);
			if (maxSeparatorCount < count) {
				maxSeparatorCount = count;
			}
		}

		return maxSeparatorCount;
	}

	private static int calculateRNR(final GUICloneSet cloneset) {
		final List<GUIClone> clones = cloneset.getClones();
		return (int) clones.stream().mapToInt(clone -> (int) (100 * clone.rnr))
				.average().getAsDouble();
	}

	int getDFL(final GUICloneSet cloneSet) {
		Integer dfl = this.dfls.get(cloneSet);
		if (null == dfl) {
			dfl = calculateDFL(cloneSet);
			this.dfls.put(cloneSet, dfl);
		}
		return dfl;
	}

	int getLEN(final GUICloneSet cloneSet) {
		Integer len = this.lens.get(cloneSet);
		if (null == len) {
			len = calculateLEN(cloneSet);
			this.lens.put(cloneSet, len);
		}
		return len;
	}

	int getNIF(final GUICloneSet cloneSet) {
		Integer nif = this.nifs.get(cloneSet);
		if (null == nif) {
			nif = calculateNIF(cloneSet);
			this.nifs.put(cloneSet, nif);
		}
		return nif;
	}

	int getPOP(final GUICloneSet cloneSet) {
		Integer pop = this.pops.get(cloneSet);
		if (null == pop) {
			pop = calculatePOP(cloneSet);
			this.pops.put(cloneSet, pop);
		}
		return pop;
	}

	int getRAD(final GUICloneSet cloneSet) {
		Integer rad = this.rads.get(cloneSet);
		if (null == rad) {
			rad = calculateRAD(cloneSet);
			this.rads.put(cloneSet, rad);
		}
		return rad;
	}

	int getRNR(final GUICloneSet cloneSet) {
		Integer rnr = this.rnrs.get(cloneSet);
		if (null == rnr) {
			rnr = calculateRNR(cloneSet);
			this.rnrs.put(cloneSet, rnr);
		}
		return rnr.intValue();
	}

	private GUICloneMetricsManager() {
		this.dfls = new HashMap<>();
		this.lens = new HashMap<>();
		this.nifs = new HashMap<>();
		this.pops = new HashMap<>();
		this.rads = new HashMap<>();
		this.rnrs = new HashMap<>();
	}

	private final Map<GUICloneSet, Integer> dfls;
	private final Map<GUICloneSet, Integer> lens;
	private final Map<GUICloneSet, Integer> nifs;
	private final Map<GUICloneSet, Integer> pops;
	private final Map<GUICloneSet, Integer> rads;
	private final Map<GUICloneSet, Integer> rnrs;
}
