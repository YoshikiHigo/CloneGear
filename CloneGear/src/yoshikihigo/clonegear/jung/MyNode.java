package yoshikihigo.clonegear.jung;

import java.util.HashMap;
import java.util.Map;

public class MyNode {

	final static private Map<Integer, MyNode> NODES = new HashMap<>();

	final static public MyNode getMyNode(final int id) {

		MyNode node = NODES.get(id);
		if (null == node) {
			node = new MyNode(id);
			NODES.put(id, node);
		}
		return node;
	}

	final public int id;

	private MyNode(final int id) {
		this.id = id;
	}
}
