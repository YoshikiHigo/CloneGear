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
import yoshikihigo.clonegear.jung.MyNode;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class SimilarityAnalyzer {

	static Color NO_COLOR = new Color(0, 0, 0, 0);

	public static void main(final String[] args) {

		CGConfig.initialize(args);
		final String resultFile = CGConfig.getInstance().getRESULT();
		final String similarityFile = CGConfig.getInstance().getSIMILARITY();

		final SortedSet<MyEdge> edges = getSimilarities(similarityFile);

		final SimilarityAnalyzer analyzer = new SimilarityAnalyzer(edges);
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

	final SortedSet<MyEdge> edges;
	Graph<MyNode, MyEdge> graph;
	SpringLayout<MyNode, MyEdge> layout;
	int repulsionThreshold;
	double edgeThreshold;
	double mergingThreshold;

	private SimilarityAnalyzer(final SortedSet<MyEdge> edges) {
		this.edges = edges;
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

			if (!this.graph.getVertices().contains(node1)) {
				this.graph.addVertex(node1);
			}

			if (!this.graph.getVertices().contains(node2)) {
				this.graph.addVertex(node2);
			}

			if (threshold <= value) {
				this.graph.addEdge(edge, node1, node2);
			}
		}

		this.layout = new SpringLayout<>(this.graph,
				new Transformer<MyEdge, Integer>() {
					@Override
					public Integer transform(final MyEdge edge) {
						return (int) ((1.0d - edge.similarity) * 500);
					}
				});
		this.layout.setRepulsionRange(10);

		final Dimension viewArea = new Dimension(width - 100, height - 100);
		final BasicVisualizationServer<MyNode, MyEdge> panel = new BasicVisualizationServer<>(
				this.layout, viewArea);
		panel.getRenderContext().setVertexShapeTransformer(
				new Transformer<MyNode, Shape>() {
					@Override
					public Shape transform(final MyNode node) {
						return new Arc2D.Double(-2.5d, -2.5d, 5, 5, 0, 360,
								Arc2D.OPEN);
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
			final String text = mergingThresholdField.getText();
			final double value = Double.parseDouble(text);
		});
	}
}
