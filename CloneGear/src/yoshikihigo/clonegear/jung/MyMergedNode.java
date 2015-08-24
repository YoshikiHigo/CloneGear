package yoshikihigo.clonegear.jung;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class MyMergedNode extends MyNode {

	final private SortedSet<MyNode> core;

	public MyMergedNode(final Collection<MyNode> nodes) {
		super(-1);
		this.core = new TreeSet<>(nodes);
	}

	public SortedSet<MyNode> getCore() {
		return new TreeSet<MyNode>(this.core);
	}

	public boolean contain(final MyNode node) {
		return this.core.contains(node);
	}
}
