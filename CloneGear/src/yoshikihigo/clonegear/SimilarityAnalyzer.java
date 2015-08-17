package yoshikihigo.clonegear;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import yoshikihigo.clonegear.jung.MyEdge;
import yoshikihigo.clonegear.jung.MyNode;
import edu.uci.ics.jung.algorithms.layout.Layout;
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

		final List<Object[]> similarities = getSimilarities(similarityFile);

		final GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		final DisplayMode displayMode = env.getDefaultScreenDevice()
				.getDisplayMode();
		final int width = displayMode.getWidth();
		final int height = displayMode.getHeight();

		final Dimension viewArea = new Dimension(width - 100, height - 100);
		final Graph<MyNode, MyEdge> graph = createGraph(similarities);
		final Layout<MyNode, MyEdge> layout = new SpringLayout<>(graph,
				new Transformer<MyEdge, Integer>() {
					@Override
					public Integer transform(final MyEdge edge) {
						return (int) (1d / (double) edge.similarity);
					}
				});

		final BasicVisualizationServer<MyNode, MyEdge> panel = new BasicVisualizationServer<>(
				layout, viewArea);
		panel.getRenderContext().setVertexShapeTransformer(
				new Transformer<MyNode, Shape>() {
					@Override
					public Shape transform(final MyNode node) {
						return new Arc2D.Double(-5, -5, 10, 10, 0, 360,
								Arc2D.OPEN);
					}
				});

		final Transformer<MyEdge, Paint> edgePaintTransformer = new Transformer<MyEdge, Paint>() {
			@Override
			public Paint transform(final MyEdge edge) {
				if (0.98d <= edge.similarity) {
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

		JFrame frame = new JFrame("Graph View: Random Layout");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private static List<Object[]> getSimilarities(final String similarityFile) {

		final List<Object[]> similarities = new ArrayList<>();

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
				final Object[] similarity = new Object[3];
				similarity[0] = cloneset1;
				similarity[1] = cloneset2;
				similarity[2] = value;
				similarities.add(similarity);
			}

		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return similarities;
	}

	private static Graph<MyNode, MyEdge> createGraph(
			final List<Object[]> similarities) {

		final Graph<MyNode, MyEdge> g = new UndirectedSparseGraph<>();

		for (final Object[] similarity : similarities) {

			final MyNode node1 = MyNode.getMyNode((Integer) similarity[0]);
			final MyNode node2 = MyNode.getMyNode((Integer) similarity[1]);
			final Double value = (Double) similarity[2];

			if (!g.getVertices().contains(node1)) {
				g.addVertex(node1);
			}

			if (!g.getVertices().contains(node2)) {
				g.addVertex(node2);
			}

			if (0.96d < value.doubleValue()) {
				g.addEdge(new MyEdge(value.doubleValue()), node1, node2);
			}
		}

		return g;
	}
}
