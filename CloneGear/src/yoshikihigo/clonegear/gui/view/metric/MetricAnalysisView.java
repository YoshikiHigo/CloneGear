package yoshikihigo.clonegear.gui.view.metric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.metric.clone.CloneListView;
import yoshikihigo.clonegear.gui.view.metric.cloneset.CloneSetListView;
import yoshikihigo.clonegear.gui.view.metric.graph.MetricView;
import yoshikihigo.clonegear.gui.view.metric.rnr.RNRSliderView;
import yoshikihigo.clonegear.gui.view.metric.scatterplot.ScatterPlotView;
import yoshikihigo.clonegear.gui.view.metric.source.SourceCodeView;
import yoshikihigo.clonegear.gui.view.metric.toolbar.ToolBarPanel;

public class MetricAnalysisView extends JInternalFrame implements ViewScale,
		MetricViewInterface, Observer {

	public MetricAnalysisView(final int width, final int height) {

		super("Metric Analysis View", true, false, true, true);

		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
				.addObserver(this);
		SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
				.addObserver(this);
		SelectedEntities.<GUIClone> getInstance(CLONE).addObserver(this);

		this.baseSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		this.rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		final JSplitPane cloneSetFragmentSplitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, true);
		final JTabbedPane leftTabbedPane = new JTabbedPane();

		this.rnrSliderView = new RNRSliderView();

		this.cloneSetListView = new CloneSetListView();
		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
				.addObserver(this.cloneSetListView);
		SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
				.addObserver(this.cloneSetListView);
		SelectedEntities.<GUIClone> getInstance(CLONE).addObserver(
				this.cloneSetListView);

		this.fragmentListView = new CloneListView();
		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
				.addObserver(this.fragmentListView);
		SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
				.addObserver(this.fragmentListView);
		SelectedEntities.<GUIClone> getInstance(CLONE).addObserver(
				this.fragmentListView);

		this.sourceCodeView = new SourceCodeView();
		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
				.addObserver(this.sourceCodeView);
		SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
				.addObserver(this.sourceCodeView);
		SelectedEntities.<GUIClone> getInstance(CLONE).addObserver(
				this.sourceCodeView);
		this.sourceCodeView.setBorder(new TitledBorder(new LineBorder(
				Color.black), "Source Code View"));

		this.metricView = new MetricView();
		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
				.addObserver(this.metricView);
		SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
				.addObserver(this.metricView);
		SelectedEntities.<GUIClone> getInstance(CLONE).addObserver(
				this.metricView);
		this.metricView.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Metric Graph View"));

		this.scatterPlotView = new ScatterPlotView();
		this.scatterPlotView.setBorder(new TitledBorder(new LineBorder(
				Color.black), "Scatter Plot View"));

		this.toolBarPanel = new ToolBarPanel(this);
		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
				.addObserver(this.toolBarPanel);
		SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
				.addObserver(this.toolBarPanel);
		SelectedEntities.<GUIClone> getInstance(CLONE).addObserver(
				this.toolBarPanel);

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.setBounds(0, 0, width, height);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.baseSplitPane, BorderLayout.CENTER);
		this.getContentPane().add(this.toolBarPanel, BorderLayout.NORTH);
		this.getContentPane().add(this.rnrSliderView, BorderLayout.SOUTH);

		this.baseSplitPane.setLeftComponent(leftTabbedPane);
		this.baseSplitPane.setRightComponent(this.rightSplitPane);

		leftTabbedPane.add("Metric Graph", this.metricView);
		leftTabbedPane.add("Scatter Plot", this.scatterPlotView);

		this.rightSplitPane.setLeftComponent(cloneSetFragmentSplitPane);
		this.rightSplitPane.setRightComponent(this.sourceCodeView);

		cloneSetFragmentSplitPane.setTopComponent(this.cloneSetListView
				.getScrollPane());
		cloneSetFragmentSplitPane.setBottomComponent(this.fragmentListView
				.getScrollPane());

		cloneSetFragmentSplitPane.setDividerLocation(height / 2 - 50);

		this.baseSplitPaneListener = e -> {
			final int location = MetricAnalysisView.this.baseSplitPane
					.getDividerLocation();
			if (location < METRIC_METRICVIEW_MIN_WIDTH) {
				MetricAnalysisView.this.baseSplitPane
						.setDividerLocation(METRIC_METRICVIEW_MIN_WIDTH);
			}
		};
		this.baseSplitPane
				.addPropertyChangeListener(this.baseSplitPaneListener);
		this.baseSplitPane.setDividerLocation(METRIC_METRICVIEW_INIT_WIDTH);

		final int minWidth = METRIC_CLONESETLISTVIEW_MIN_WIDTH + 29;
		final int maxWidth = METRIC_CLONESETLISTVIEW_MAX_WIDTH + 29;
		this.rightSplitPane.addPropertyChangeListener(e -> {
			final int location = MetricAnalysisView.this.rightSplitPane
					.getDividerLocation();
			if (0 == location) {
			} else if (location < minWidth) {
				MetricAnalysisView.this.rightSplitPane
						.setDividerLocation(minWidth);
			} else if (maxWidth < location) {
				MetricAnalysisView.this.rightSplitPane
						.setDividerLocation(maxWidth);
			}
		});
		this.rightSplitPane.setDividerLocation(minWidth);

		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET).setAll(
				GUICloneManager.SINGLETON.getCloneSets(), this);

		this.setVisible(false);
	}

	public void init() {
		this.cloneSetListView.init();
		this.fragmentListView.init();
		this.metricView.init();
		this.scatterPlotView.init();
		this.sourceCodeView.init();
	}

	public void reset() {
		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET).setAll(
				GUICloneManager.SINGLETON.getCloneSets(), this);
		SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET).clear(
				this);
		SelectedEntities.<GUIClone> getInstance(CLONE).clear(this);

		this.toolBarPanel.reset();
		this.rnrSliderView.reset();
		this.metricView.reset();

		this.metricView.repaint();
		this.scatterPlotView.repaint();
		this.sourceCodeView.repaint();
		this.cloneSetListView.repaint();
		this.fragmentListView.repaint();
		this.toolBarPanel.repaint();
	}

	public void showMetricGraphView() {
		this.baseSplitPane
				.addPropertyChangeListener(this.baseSplitPaneListener);
		this.baseSplitPane.setDividerLocation(METRIC_METRICVIEW_INIT_WIDTH);
	}

	public void hideMetricGraphView() {
		this.baseSplitPane
				.removePropertyChangeListener(this.baseSplitPaneListener);
		this.baseSplitPane.setDividerLocation(0);
	}

	@Override
	public void update(Observable o, Object arg) {
	}

	private final MetricView metricView;

	private final RNRSliderView rnrSliderView;

	private final ScatterPlotView scatterPlotView;

	private final SourceCodeView sourceCodeView;

	private final CloneSetListView cloneSetListView;

	private final CloneListView fragmentListView;

	private final ToolBarPanel toolBarPanel;

	private final JSplitPane baseSplitPane;

	private final JSplitPane rightSplitPane;

	private final PropertyChangeListener baseSplitPaneListener;
}
