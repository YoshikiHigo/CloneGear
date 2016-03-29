package yoshikihigo.clonegear.data;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import yoshikihigo.clonegear.lexer.token.Token;

public class CloneSet extends CloneData implements Comparable<CloneSet> {

	final static private AtomicInteger ID_GENERATOR = new AtomicInteger(0);

	final public int id;
	final private SortedSet<ClonePair> clonepairs;

	public CloneSet(final CloneHash hash, final List<Token> tokens) {
		super(hash, tokens);
		this.id = ID_GENERATOR.getAndIncrement();
		this.clonepairs = new TreeSet<>();
	}

	public void addClonepair(final ClonePair clonepair) {
		this.clonepairs.add(clonepair);
	}

	public SortedSet<ClonePair> getClonepairs() {
		final SortedSet<ClonePair> clonepairs = new TreeSet<>(this.clonepairs);
		return clonepairs;
	}

	public SortedSet<ClonedFragment> getClonedFragments() {
		final SortedSet<ClonedFragment> fragments = new TreeSet<>();
		this.clonepairs.stream().forEach(clonepair -> {
			fragments.add(clonepair.left);
			fragments.add(clonepair.right);
		});
		return fragments;
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public int compareTo(final CloneSet target) {
		return Integer.compare(this.getID(), target.getID());
	}
}
