package yoshikihigo.clonegear;

import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.data.CloneHash;
import yoshikihigo.clonegear.data.CloneSet;
import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.lexer.token.Token;

public class CloneDetectionThread implements Runnable {

	final private static Object LOCK = new Object();

	final private SourceFile iFile;
	final private SourceFile jFile;
	final private Map<CloneHash, CloneSet> clonesets;

	public CloneDetectionThread(final SourceFile iFile, final SourceFile jFile,
			Map<CloneHash, CloneSet> clonesets) {
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
				CloneSet cloneset = this.clonesets.get(hash);
				if (null == cloneset) {
					final List<Token> tokens = CloneHash.getTokens(hash);
					cloneset = new CloneSet(hash, tokens);
					this.clonesets.put(hash, cloneset);
				}
				cloneset.addClone(clonedFragment);
			}
		}

	}
}
