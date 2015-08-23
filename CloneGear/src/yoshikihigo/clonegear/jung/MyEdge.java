package yoshikihigo.clonegear.jung;

public class MyEdge implements Comparable<MyEdge> {

	final public double similarity;
	final public MyNode startNode;
	final public MyNode endNode;

	public MyEdge(final double similarity, final MyNode startNode,
			final MyNode endNode) {
		this.similarity = similarity;
		this.startNode = startNode;
		this.endNode = endNode;
	}

	@Override
	public int compareTo(final MyEdge edge) {

		if (this.similarity > edge.similarity) {
			return -1;
		} else if (this.similarity < edge.similarity) {
			return 1;
		} else if (this.startNode.id < edge.startNode.id) {
			return -1;
		} else if (this.startNode.id > edge.startNode.id) {
			return 1;
		} else if (this.endNode.id < edge.endNode.id) {
			return -1;
		} else if (this.endNode.id > edge.endNode.id) {
			return 1;
		} else {
			return 0;
		}
	}
}
