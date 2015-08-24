package yoshikihigo.clonegear;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.collections15.Transformer;

import yoshikihigo.clonegear.jung.MyEdge;
import yoshikihigo.clonegear.jung.MyMergedNode;
import yoshikihigo.clonegear.jung.MyNode;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

public class SimilarityAnalyzer {

	static Color NO_COLOR = new Color(0, 0, 0, 0);

	public static void main(final String[] args) {

		CGConfig.initialize(args);
		final String resultFile = CGConfig.getInstance().getRESULT();
		final String similarityFile = CGConfig.getInstance().getSIMILARITY();

		final SortedSet<MyEdge> edges = getSimilarities(similarityFile);
		final double[][] similarityMatrix = getSimilarityMatrix(edges);

		final SimilarityAnalyzer analyzer = new SimilarityAnalyzer(edges,
				similarityMatrix);
		analyzer.makeGraph();
		analyzer.makeController();

		final double threshold = getThreshold(edges);
		System.out.println(threshold);

	}

	private static SortedSet<MyEdge> getSimilarities(final String similarityFile) {

		final SortedSet<MyEdge> similarities = new TreeSet<>();

		try (final BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(similarityFile),
						"JISAutoDetect"))) {

			while (true) {
				final String line = reader.readLine();
				if (null == line) {
					break;
				}

				final StringTokenizer tokenizer = new StringTokenizer(line,
						" ,");
				final Integer cloneset1 = Integer
						.valueOf(tokenizer.nextToken());
				final Integer cloneset2 = Integer
						.valueOf(tokenizer.nextToken());
				final Double value = Double.valueOf(tokenizer.nextToken());

				final MyNode node1 = MyNode.getMyNode(cloneset1);
				final MyNode node2 = MyNode.getMyNode(cloneset2);
				final MyEdge edge = new MyEdge(value, node1, node2);
				node1.addEdge(edge);
				node2.addEdge(edge);
				similarities.add(edge);
			}

		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return similarities;
	}

	private static double getThreshold(final SortedSet<MyEdge> edges) {

		final MyEdge[] list = edges.toArray(new MyEdge[0]);

		int cloneset = 0;
		for (final MyEdge edge : list) {
			if (cloneset < edge.endNode.id) {
				cloneset = edge.endNode.id;
			}
		}

		final int total = (cloneset * (cloneset - 1)) / 2;
		final int position = (int) (0.001d * total);

		return list[(position < list.length) ? position : list.length - 1].similarity;
	}

	private static double[][] getSimilarityMatrix(final SortedSet<MyEdge> edges) {

		int numberOfNodes = 0;
		for (final MyEdge edge : edges) {
			if (numberOfNodes < edge.startNode.id) {
				numberOfNodes = edge.startNode.id;
			}
			if (numberOfNodes < edge.endNode.id) {
				numberOfNodes = edge.endNode.id;
			}
		}

		final double[][] matrix = new double[numberOfNodes + 1][numberOfNodes + 1];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				matrix[i][j] = (i == j) ? 1.0d : 0.0d;
			}
		}
		for (final MyEdge edge : edges) {
			matrix[edge.startNode.id][edge.endNode.id] = edge.similarity;
			matrix[edge.endNode.id][edge.startNode.id] = edge.similarity;
		}

		return matrix;
	}

	final SortedSet<MyEdge> edges;
	final Set<MyMergedNode> mergedNodes;
	final double[][] similarityMatrix;
	Graph<MyNode, MyEdge> graph;
	SpringLayout<MyNode, MyEdge> layout;
	int repulsionThreshold;
	double edgeThreshold;
	double mergingThreshold;

	private SimilarityAnalyzer(final SortedSet<MyEdge> edges,
			final double[][] similarityMatrix) {
		this.edges = edges;
		this.mergedNodes = new HashSet<>();
		this.similarityMatrix = similarityMatrix;
		this.graph = null;
		this.layout = null;
		this.repulsionThreshold = 10;
		this.edgeThreshold = getThreshold(edges);
		this.mergingThreshold = 1.0d;
	}

	private void makeGraph() {

		final double threshold = getThreshold(this.edges);

		final GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		final DisplayMode displayMode = env.getDefaultScreenDevice()
				.getDisplayMode();
		final int width = displayMode.getWidth();
		final int height = displayMode.getHeight();

		this.graph = new UndirectedSparseGraph<>();
		for (final MyEdge edge : this.edges) {

			final MyNode node1 = edge.startNode;
			final MyNode node2 = edge.endNode;
			final double value = edge.similarity;

			if (!this.graph.getVertices().contains(node1) && !node1.isMerged()) {
				this.graph.addVertex(node1);
			}

			if (!this.graph.getVertices().contains(node2) && !node2.isMerged()) {
				this.graph.addVertex(node2);
			}

			if ((threshold <= value) && !node1.isMerged() && !node2.isMerged()) {
				this.graph.addEdge(edge, node1, node2);
			}
		}

		this.addMergedNodes();

		this.layout = new SpringLayout<>(this.graph,
				new Transformer<MyEdge, Integer>() {
					@Override
					public Integer transform(final MyEdge edge) {
						return (int) ((1.0d - edge.similarity) * 500);
					}
				});
		this.layout.setRepulsionRange(10);

		final Dimension viewArea = new Dimension(width - 100, height - 100);
		final VisualizationViewer<MyNode, MyEdge> panel = new VisualizationViewer<>(
				this.layout, viewArea);
		panel.getRenderContext().setVertexShapeTransformer(
				new Transformer<MyNode, Shape>() {
					@Override
					public Shape transform(final MyNode node) {

						if (node instanceof MyMergedNode) {
							final MyMergedNode mergedNode = (MyMergedNode) node;
							final int size = mergedNode.getCore().size();
							return new Arc2D.Double(-5 + (-1 * size), -5
									+ (-1 * size), 10 + 2 * size,
									10 + 2 * size, 0, 360, Arc2D.OPEN);
						}

						else {
							return new Arc2D.Double(-5, -5, 10, 10, 0, 360,
									Arc2D.OPEN);
						}
					}
				});

		final Transformer<MyEdge, Paint> edgePaintTransformer = new Transformer<MyEdge, Paint>() {
			@Override
			public Paint transform(final MyEdge edge) {
				if (threshold <= edge.similarity) {
					return Color.BLACK;
				} else {
					return NO_COLOR;
				}
			}
		};
		panel.getRenderContext().setArrowFillPaintTransformer(
				edgePaintTransformer);
		panel.getRenderContext().setArrowDrawPaintTransformer(
				edgePaintTransformer);
		panel.getRenderContext().setEdgeDrawPaintTransformer(
				edgePaintTransformer);

		final JFrame frame = new JFrame("Graph View: Random Layout");
		final DefaultModalGraphMouse<MyNode, MyEdge> mouse = new DefaultModalGraphMouse<MyNode, MyEdge>();
		mouse.setMode(ModalGraphMouse.Mode.PICKING);
		panel.setGraphMouse(mouse);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private void makeController() {

		final JPanel panel = new JPanel(new GridLayout(3, 3));
		panel.add(new JLabel("Repulsion Threshold"));
		final JTextField repulsionThresholdField = new JTextField(
				Integer.toString(this.repulsionThreshold));
		panel.add(repulsionThresholdField);
		final JButton repulsionThresholdButton = new JButton("UPDATE");
		panel.add(repulsionThresholdButton);
		panel.add(new JLabel("Edge Threshold"));
		final JTextField edgeThresholdField = new JTextField(
				Double.toString(this.edgeThreshold));
		panel.add(edgeThresholdField);
		final JButton edgeThresholdButton = new JButton("UPDATE");
		panel.add(edgeThresholdButton);
		panel.add(new JLabel("Merging Threshold"));
		final JTextField mergingThresholdField = new JTextField(
				Double.toString(this.mergingThreshold));
		panel.add(mergingThresholdField);
		final JButton mergingThresholdButton = new JButton("UPDATE");
		panel.add(mergingThresholdButton);

		final JFrame frame = new JFrame(
				"CloneGear: SimilarityAnalzyer Controller");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);

		repulsionThresholdButton.addActionListener(e -> {
			final String text = repulsionThresholdField.getText();
			final int value = Integer.parseInt(text);
			this.layout.setRepulsionRange(value);
		});

		edgeThresholdButton.addActionListener(e -> {
			final String text = edgeThresholdField.getText();
			final double value = Double.parseDouble(text);
			if (value < this.edgeThreshold) {
				for (final MyEdge edge : this.edges) {
					if (this.edgeThreshold <= edge.similarity) {
						continue;
					}
					if (value <= edge.similarity) {
						this.graph.addEdge(edge, edge.startNode, edge.endNode);
						continue;
					}
					break;
				}
			} else if (value > this.edgeThreshold) {
				final Collection<MyEdge> edges = new ArrayList<>(this.graph
						.getEdges());
				for (final MyEdge edge : edges) {
					if (edge.similarity < value) {
						this.graph.removeEdge(edge);
					}
				}
			}
			this.edgeThreshold = value;
		});

		mergingThresholdButton.addActionListener(e -> {

			for (final MyMergedNode mergedNode : this.mergedNodes) {
				this.graph.removeVertex(mergedNode);
				for (final MyNode node : mergedNode.getCore()) {
					node.clearOwners();
				}
			}

			final String text = mergingThresholdField.getText();
			final double value = Double.parseDouble(text);

			final Set<Set<MyNode>> cliques = this.getCliques(value);
			final Set<MyMergedNode> mergedNodes = new HashSet<>();
			for (final Set<MyNode> clique : cliques) {
				final MyMergedNode mergedNode = new MyMergedNode(clique);
				for (final MyNode node : clique) {
					node.addOwner(mergedNode);
				}
				mergedNodes.add(mergedNode);
			}

			for (final MyMergedNode mergedNode : mergedNodes) {
				for (final MyNode node : mergedNode.getCore()) {
					this.graph.removeVertex(node);
				}
			}

			this.mergedNodes.clear();
			this.mergedNodes.addAll(mergedNodes);
			this.addMergedNodes();
		});
	}

	private Set<Set<MyNode>> getCliques(final double mergingThreshold) {

		Set<Set<MyNode>> pres = new HashSet<>();
		for (final MyEdge edge : this.edges) {
			if (mergingThreshold <= edge.similarity) {
				final Set<MyNode> clique = new HashSet<>();
				clique.add(edge.startNode);
				clique.add(edge.endNode);
				pres.add(clique);
				continue;
			}
			break;
		}

		final Set<Set<MyNode>> localMaximumCliques = new HashSet<>();
		while (true) {
			final Set<Set<MyNode>> posts = new HashSet<>();
			final Set<MyNode> elements = this.getElements(pres);
			CLIQUE: for (final Set<MyNode> clique : pres) {
				boolean isLocalMaximum = true;
				ELEMENT: for (final MyNode element : elements) {
					NODE: for (final MyNode node : clique) {
						if (clique.contains(element)) {
							continue ELEMENT;
						}
						final double similarity = this.similarityMatrix[node.id][element.id];
						if (similarity < mergingThreshold) {
							continue ELEMENT;
						}
					}
					final Set<MyNode> newClique = new HashSet<>(clique);
					newClique.add(element);
					posts.add(newClique);
					isLocalMaximum = false;
				}

				if (isLocalMaximum) {
					localMaximumCliques.add(clique);
				}
			}

			if (posts.isEmpty()) {
				break;
			}

			pres = posts;
		}

		return localMaximumCliques;
	}

	private void addMergedNodes() {

		for (final MyMergedNode mergedNode : this.mergedNodes) {
			System.out.println("A");
			this.graph.addVertex(mergedNode);
			for (final MyNode node : mergedNode.getCore()) {
				for (final Entry<MyNode, Double> entry : node
						.getConnectedNodes().entrySet()) {

					final MyNode connectedNode = entry.getKey();
					final Double similarity = entry.getValue();

					if (similarity < this.edgeThreshold) {
						continue;
					}

					final SortedSet<MyMergedNode> owners = connectedNode
							.getOwners();

					// in case connected node is not merged
					if (owners.isEmpty()) {
						this.graph.addEdge(new MyEdge(similarity, mergedNode,
								connectedNode), mergedNode, connectedNode);
					}

					// in case connected node is merged
					else {
						for (final MyMergedNode owner : owners) {
							if (mergedNode == owner) {
								continue;
							}
							this.graph.addVertex(owner);
							this.graph.addEdge(new MyEdge(similarity,
									mergedNode, owner), mergedNode, owner);
						}
					}

					if (mergedNode.contain(connectedNode)) {
						continue;
					}

				}
			}
		}

		System.out.println("C");
	}

	private Set<MyNode> getElements(final Set<Set<MyNode>> cliques) {
		final Set<MyNode> elements = new HashSet<>();
		for (final Set<MyNode> clique : cliques) {
			elements.addAll(clique);
		}
		return elements;
	}
}
