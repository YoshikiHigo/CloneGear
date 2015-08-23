package yoshikihigo.clonegear.jung;

import java.util.HashMap;
import java.util.Map;

public class MyNode implements Comparable<MyNode> {

	final static protected Map<Integer, MyNode> NODES = new HashMap<>();

	final static public MyNode getMyNode(final int id) {

		MyNode node = NODES.get(id);
		if (null == node) {
			node = new MyNode(id);
			NODES.put(id, node);
		}
		return node;
	}

	final public int id;

	protected MyNode(final int id) {
		this.id = id;
	}

	@Override
	public int compareTo(final MyNode node) {
		return Integer.valueOf(this.id).compareTo(Integer.valueOf(node.id));
	}
}
