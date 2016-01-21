package yoshikihigo.clonegear.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
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

		alive = true;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		GUIFileManager.initialize();
		GUICloneManager.initialize();

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		final JDesktopPane desktopPane = new JDesktopPane();
		this.getContentPane().add(desktopPane, java.awt.BorderLayout.CENTER);

		MessagePrinter.OUT
				.println("> starting to read a given clone detection file ...");
		final List<GUIFile> files = new ArrayList<>();
		final List<GUIClonePair> clonepairs = new ArrayList<>();
		DetectionResultsFormat.read(inputFileName, files, clonepairs);
		files.stream().forEach(file -> GUIFileManager.instance().add(file));
		clonepairs.stream()
				.forEach(
						clonepair -> GUICloneManager.instance().addClonepair(
								clonepair));

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

		MessagePrinter.OUT.println("> creating Quantitative Analysis View ...");
		final QuantitativeAnalysisView quantitativeAnalysisView = new QuantitativeAnalysisView(
				viewerWidth, viewerHeight);
		quantitativeAnalysisView.init();
		desktopPane.add(quantitativeAnalysisView);

		MessagePrinter.OUT.println("> creating Metric Analysis View ...");
		final MetricAnalysisView metricAnalysisView = new MetricAnalysisView(
				viewerWidth, viewerHeight);
		metricAnalysisView.init();
		desktopPane.add(metricAnalysisView);

		MessagePrinter.OUT.println("> creating Visual Analysis View ...");
		final VisualAnalysisView visualAnalysisView = new VisualAnalysisView(
				viewerWidth, viewerHeight);
		visualAnalysisView.init();
		desktopPane.add(visualAnalysisView);

		try {
			quantitativeAnalysisView.setVisible(true);
			quantitativeAnalysisView.setIcon(true);
			metricAnalysisView.setVisible(true);
			metricAnalysisView.setIcon(true);
			visualAnalysisView.setVisible(true);
		} catch (PropertyVetoException e) {
		}

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Gemini.this.alive = false;
			}
		});

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
