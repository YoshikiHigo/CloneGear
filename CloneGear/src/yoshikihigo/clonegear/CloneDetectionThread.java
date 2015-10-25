package yoshikihigo.clonegear;

import java.util.List;

import yoshikihigo.clonegear.data.ClonePair;
import yoshikihigo.clonegear.data.SourceFile;

public class CloneDetectionThread implements Runnable {

	final private static Object LOCK = new Object();

	final private SourceFile iFile;
	final private SourceFile jFile;
	final private List<ClonePair> clonepairs;

	public CloneDetectionThread(final SourceFile iFile, final SourceFile jFile,
			final List<ClonePair> clonepairs) {
		this.iFile = iFile;
		this.jFile = jFile;
		this.clonepairs = clonepairs;
	}

	@Override
	public void run() {

		final SmithWaterman sw = new SmithWaterman(this.iFile, this.jFile);
		final List<ClonePair> clonepairs = sw.getClonedFragments();
		synchronized (LOCK) {
			this.clonepairs.addAll(clonepairs);
		}
	}
}
