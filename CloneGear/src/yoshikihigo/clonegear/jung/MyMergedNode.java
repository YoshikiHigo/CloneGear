package yoshikihigo.clonegear.jung;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class MyMergedNode extends MyNode {

	final private SortedSet<MyNode> core;

	public MyMergedNode(final Collection<MyNode> nodes) {
		super(NODES.size());
		this.core = new TreeSet<>(nodes);
	}
}
