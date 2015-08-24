package yoshikihigo.clonegear.jung;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
	final SortedSet<MyEdge> edges;
	final private Set<MyMergedNode> owners;

	protected MyNode(final int id) {
		this.id = id;
		this.edges = new TreeSet<>();
		this.owners = new HashSet<>();
	}

	public void addEdge(final MyEdge edge) {
		this.edges.add(edge);
	}

	public SortedSet<MyEdge> getEdges() {
		return new TreeSet<MyEdge>(this.edges);
	}

	public SortedMap<MyNode, Double> getConnectedNodes() {
		final SortedMap<MyNode, Double> connectedNodes = new TreeMap<>();
		for (final MyEdge edge : this.edges) {
			if (!edge.startNode.equals(this)) {
				connectedNodes.put(edge.startNode, edge.similarity);
			}
			if (!edge.endNode.equals(this)) {
				connectedNodes.put(edge.endNode, edge.similarity);
			}
		}
		return connectedNodes;
	}

	public void addOwner(final MyMergedNode owner) {
		this.owners.add(owner);
	}

	public void clearOwners() {
		this.owners.clear();
	}

	public SortedSet<MyMergedNode> getOwners() {
		return new TreeSet<MyMergedNode>(this.owners);
	}

	public boolean isMerged() {
		return !this.owners.isEmpty();
	}

	@Override
	public int compareTo(final MyNode node) {
		return Integer.valueOf(this.id).compareTo(Integer.valueOf(node.id));
	}
}
