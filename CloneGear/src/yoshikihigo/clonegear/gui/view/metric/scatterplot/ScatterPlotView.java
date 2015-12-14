package yoshikihigo.clonegear.gui.view.metric.scatterplot;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.ScaleValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;

public class ScatterPlotView extends JPanel implements MetricViewInterface {

	public ScatterPlotView() {

		this.setLayout(new BorderLayout());

		final FileNameDisplayPanel fileNameDisplayPanel = new FileNameDisplayPanel(
				this);
		this.add(fileNameDisplayPanel, BorderLayout.NORTH);

		final ScaleSliderPanel scaleSliderPanel = new ScaleSliderPanel();
		this.add(scaleSliderPanel, BorderLayout.SOUTH);
		ScaleValue.getInstance(SCALE).addObserver(scaleSliderPanel);

		this.scatterPlotPanel = new ScatterPlotPanel(fileNameDisplayPanel);
		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
				.addObserver(this.scatterPlotPanel);
		SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
				.addObserver(this.scatterPlotPanel);
		SelectedEntities.<GUIClone> getInstance(CLONE).addObserver(
				this.scatterPlotPanel);
		RNRValue.getInstance(RNR).addObserver(this.scatterPlotPanel);
		ScaleValue.getInstance(SCALE).addObserver(this.scatterPlotPanel);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.scatterPlotPanel);
		this.add(this.scatterPlotPanel.scrollPane, BorderLayout.CENTER);
	}

	public void init() {
	}

	public void reset() {
		this.scatterPlotPanel.reset();
	}

	final private ScatterPlotPanel scatterPlotPanel;
}
