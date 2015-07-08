package yoshikihigo.clonegear;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import yoshikihigo.clonegear.data.CloneHash;
import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.SourceFile;

public class CloneDetectionThread implements Runnable {

	final private static Object LOCK = new Object();

	final private SourceFile iFile;
	final private SourceFile jFile;
	final private Map<CloneHash, SortedSet<ClonedFragment>> clonesets;

	public CloneDetectionThread(final SourceFile iFile, final SourceFile jFile,
			Map<CloneHash, SortedSet<ClonedFragment>> clonesets) {
		this.iFile = iFile;
		this.jFile = jFile;
		this.clonesets = clonesets;
	}

	@Override
	public void run() {

		final SmithWaterman sw = new SmithWaterman(this.iFile, this.jFile);
		final List<ClonedFragment> clonedFragments = sw.getClonedFragments();
		for (final ClonedFragment clonedFragment : clonedFragments) {
			synchronized (LOCK) {
				final CloneHash hash = new CloneHash(clonedFragment.cloneID);
				SortedSet<ClonedFragment> cloneset = clonesets.get(hash);
				if (null == cloneset) {
					cloneset = new TreeSet<ClonedFragment>();
					clonesets.put(hash, cloneset);
				}
				cloneset.add(clonedFragment);
			}
		}

	}
}
