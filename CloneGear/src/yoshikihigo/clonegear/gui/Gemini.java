package yoshikihigo.clonegear.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import yoshikihigo.clonegear.DetectionResultsFormat;
import yoshikihigo.clonegear.gui.data.clone.CloneFirstPositionOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneIDOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneLastPositionOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneMetricsMaxValues;
import yoshikihigo.clonegear.gui.data.clone.CloneMiddlePositionOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneNIFOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneRangeOffsetData;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.data.file.FileOffsetData;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.data.file.IDIndexMap;
import yoshikihigo.clonegear.gui.data.file.IndexedFileData;
import yoshikihigo.clonegear.gui.view.metric.MetricAnalysisView;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeAnalysisView;
import yoshikihigo.clonegear.gui.view.visual.VisualAnalysisView;

public class Gemini extends JFrame implements Runnable {

	public static void main(String args[]) {
		final String inputFileName = args[0];
		final Gemini gemini = new Gemini(inputFileName);
		gemini.setVisible(true);
	}

	private boolean alive;

	public Gemini(final String inputFileName) {

		super("Gemini");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		this.alive = true;
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Gemini.this.alive = false;
			}
		});

		GUIFileManager.initialize();
		GUICloneManager.initialize();

		final List<GUIFile> files = new ArrayList<>();
		final List<GUIClonePair> clonepairs = new ArrayList<>();
		DetectionResultsFormat.read(inputFileName, files, clonepairs);
		files.stream().forEach(file -> GUIFileManager.instance().add(file));
		clonepairs.stream().forEach(
				pair -> GUICloneManager.instance().addClonepair(pair));

		FileOffsetData.initialize(GUIFileManager.instance());
		IDIndexMap.initialize(GUIFileManager.instance());
		IndexedFileData.initialize(GUIFileManager.instance());

		CloneMetricsMaxValues.initialize(GUICloneManager.instance()
				.getCloneSets());
		CloneIDOffsetData.initialize(GUICloneManager.instance());
		CloneFirstPositionOffsetData.initialize(GUICloneManager.instance());
		CloneLastPositionOffsetData.initialize(GUICloneManager.instance());
		CloneMiddlePositionOffsetData.initialize(GUICloneManager.instance());
		CloneRangeOffsetData.initialize(GUICloneManager.instance());
		CloneNIFOffsetData.initialize(GUICloneManager.instance());

		final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		final int viewerWidth = (int) size.getWidth() - 10;
		final int viewerHeight = (int) size.getHeight() - 60;

		final QuantitativeAnalysisView quantitativeAnalysisView = new QuantitativeAnalysisView(
				viewerWidth, viewerHeight);
		quantitativeAnalysisView.init();

		final MetricAnalysisView metricAnalysisView = new MetricAnalysisView(
				viewerWidth, viewerHeight);
		metricAnalysisView.init();

		final VisualAnalysisView visualAnalysisView = new VisualAnalysisView(
				viewerWidth, viewerHeight);
		visualAnalysisView.init();

		final JTabbedPane mainPanel = new JTabbedPane();
		mainPanel.addTab("Scatter Plot Analysis", visualAnalysisView);
		mainPanel.addTab("Metric View Analysis", metricAnalysisView);
		mainPanel.addTab("Quantitative Analysis", quantitativeAnalysisView);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);

		quantitativeAnalysisView.reset();
	}

	@Override
	public void run() {
		this.setVisible(true);
		while (this.alive) {
			try {
				Thread.sleep(200);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
