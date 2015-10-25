package yoshikihigo.clonegear.data;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import yoshikihigo.clonegear.lexer.token.Token;

public class CloneSet extends CloneData {

	final static private AtomicInteger ID_GENERATOR = new AtomicInteger(0);

	final public int id;
	final private SortedSet<ClonedFragment> clones;

	public CloneSet(final CloneHash hash, final List<Token> tokens) {
		super(hash, tokens);
		this.id = ID_GENERATOR.getAndIncrement();
		this.clones = new TreeSet<>();
	}

	public void addClone(final ClonedFragment clone) {
		this.clones.add(clone);
	}

	public SortedSet<ClonedFragment> getClones() {
		final SortedSet<ClonedFragment> clones = new TreeSet<>();
		clones.addAll(this.clones);
		return clones;
	}

	@Override
	public int getID() {
		return this.id;
	}
}
