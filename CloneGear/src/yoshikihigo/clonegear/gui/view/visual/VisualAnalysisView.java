package yoshikihigo.clonegear.gui.view.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.visual.clonepair.ClonePairListView;
import yoshikihigo.clonegear.gui.view.visual.directorytree.DirectoryTreeView;
import yoshikihigo.clonegear.gui.view.visual.filelist.FileListView;
import yoshikihigo.clonegear.gui.view.visual.filepath.FilePathView;
import yoshikihigo.clonegear.gui.view.visual.rnr.RNRSliderView;
import yoshikihigo.clonegear.gui.view.visual.scatterplot.ScatterPlotView;
import yoshikihigo.clonegear.gui.view.visual.sourcecode.SourceCodeWindow;
import yoshikihigo.clonegear.gui.view.visual.toolbar.ToolBarPanel;
import yoshikihigo.clonegear.gui.view.visual.uninterest.UninterestingClonesReportingPanel;

public class VisualAnalysisView extends JPanel implements ViewScale, VisualViewInterface, Observer {

	public VisualAnalysisView(final int width, final int height) {

		this.baseSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		this.rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		this.sourceSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		final JTabbedPane fileSelectionPane = new JTabbedPane(JTabbedPane.TOP);
		fileSelectionPane.setBorder(new TitledBorder(new LineBorder(Color.black), "File Selection Panel"));
		final JSplitPane fileListPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		final JSplitPane directoryTreePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);

		this.toolBarPanel = new ToolBarPanel(this);
		this.rnrSliderView = new RNRSliderView();

		this.horizontalFileListView = new FileListView(DIRECTION.HORIZONTAL);
		this.horizontalFileListView.getScrollPane()
				.setBorder(new TitledBorder(new LineBorder(Color.black), "Horizontal"));
		SelectedEntities.<GUIFile>getInstance(HORIZONTAL_FILE).addObserver(this.horizontalFileListView);
		RNRValue.getInstance(RNR).addObserver(this.horizontalFileListView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this.horizontalFileListView);

		this.verticalFileListView = new FileListView(DIRECTION.VERTICAL);
		this.verticalFileListView.getScrollPane().setBorder(new TitledBorder(new LineBorder(Color.black), "Vertical"));
		SelectedEntities.<GUIFile>getInstance(VERTICAL_FILE).addObserver(this.verticalFileListView);
		RNRValue.getInstance(RNR).addObserver(this.verticalFileListView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this.verticalFileListView);

		this.horizontalDirectoryTreeView = new DirectoryTreeView(DIRECTION.HORIZONTAL);
		this.horizontalDirectoryTreeView.getScrollPane()
				.setBorder(new TitledBorder(new LineBorder(Color.black), "Horizontal"));
		SelectedEntities.<GUIFile>getInstance(HORIZONTAL_FILE).addObserver(this.horizontalDirectoryTreeView);
		RNRValue.getInstance(RNR).addObserver(this.horizontalDirectoryTreeView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this.horizontalDirectoryTreeView);

		this.verticalDirectoryTreeView = new DirectoryTreeView(DIRECTION.VERTICAL);
		this.verticalDirectoryTreeView.getScrollPane()
				.setBorder(new TitledBorder(new LineBorder(Color.black), "Vertical"));
		SelectedEntities.<GUIFile>getInstance(VERTICAL_FILE).addObserver(this.verticalDirectoryTreeView);
		RNRValue.getInstance(RNR).addObserver(this.verticalDirectoryTreeView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this.verticalDirectoryTreeView);

		this.horizontalSourceCodeWindow = new SourceCodeWindow(DIRECTION.HORIZONTAL);
		SelectedEntities.<GUIFile>getInstance(HORIZONTAL_FILE).addObserver(this.horizontalSourceCodeWindow);
		SelectedEntities.<GUIFile>getInstance(VERTICAL_FILE).addObserver(this.horizontalSourceCodeWindow);
		SelectedEntities.<GUIClonePair>getInstance(CLONEPAIR).addObserver(this.horizontalSourceCodeWindow);
		RNRValue.getInstance(RNR).addObserver(this.horizontalSourceCodeWindow);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this.horizontalSourceCodeWindow);

		this.verticalSourceCodeWindow = new SourceCodeWindow(DIRECTION.VERTICAL);
		SelectedEntities.<GUIFile>getInstance(VERTICAL_FILE).addObserver(this.verticalSourceCodeWindow);
		SelectedEntities.<GUIFile>getInstance(HORIZONTAL_FILE).addObserver(this.verticalSourceCodeWindow);
		SelectedEntities.<GUIClonePair>getInstance(CLONEPAIR).addObserver(this.verticalSourceCodeWindow);
		RNRValue.getInstance(RNR).addObserver(this.verticalSourceCodeWindow);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this.verticalSourceCodeWindow);

		this.clonePairListView = new ClonePairListView();
		SelectedEntities.<GUIFile>getInstance(HORIZONTAL_FILE).addObserver(this.clonePairListView);
		SelectedEntities.<GUIFile>getInstance(VERTICAL_FILE).addObserver(this.clonePairListView);
		SelectedEntities.<GUIClonePair>getInstance(CLONEPAIR).addObserver(this.clonePairListView);
		RNRValue.getInstance(RNR).addObserver(this.clonePairListView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this.clonePairListView);

		this.uninterestingPane = new UninterestingClonesReportingPanel();
		SelectedEntities.<GUIFile>getInstance(HORIZONTAL_FILE).addObserver(uninterestingPane);
		SelectedEntities.<GUIFile>getInstance(VERTICAL_FILE).addObserver(uninterestingPane);
		SelectedEntities.<GUIClonePair>getInstance(CLONEPAIR).addObserver(uninterestingPane);
		RNRValue.getInstance(RNR).addObserver(uninterestingPane);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(uninterestingPane);

		this.horizontalFilePathView = new FilePathView();
		SelectedEntities.<GUIFile>getInstance(HORIZONTAL_FILE).addObserver(this.horizontalFilePathView);
		this.horizontalFilePathView.setBorder(new TitledBorder(new LineBorder(Color.black), "Horizontal File Path"));

		this.verticalFilePathView = new FilePathView();
		SelectedEntities.<GUIFile>getInstance(VERTICAL_FILE).addObserver(this.verticalFilePathView);
		this.verticalFilePathView.setBorder(new TitledBorder(new LineBorder(Color.black), "Vertical File Path"));

		this.scatterPlotView = new ScatterPlotView();

		this.setLayout(new BorderLayout());
		this.add(baseSplitPane, BorderLayout.CENTER);
		this.add(this.toolBarPanel, BorderLayout.NORTH);
		this.add(this.rnrSliderView, BorderLayout.SOUTH);

		fileListPane.setTopComponent(this.horizontalFileListView.getScrollPane());
		fileListPane.setBottomComponent(this.verticalFileListView.getScrollPane());

		directoryTreePane.setTopComponent(this.horizontalDirectoryTreeView.getScrollPane());
		directoryTreePane.setBottomComponent(this.verticalDirectoryTreeView.getScrollPane());

		fileSelectionPane.addTab("Scatter Plot", this.scatterPlotView);
		fileSelectionPane.addTab("File List", fileListPane);
		fileSelectionPane.addTab("Directory Tree", directoryTreePane);

		this.baseSplitPane.setLeftComponent(fileSelectionPane);
		this.baseSplitPane.setRightComponent(this.rightSplitPane);

		final JPanel sourcePanel = new JPanel(new BorderLayout());
		sourcePanel.setBorder(new TitledBorder(new LineBorder(Color.black), "Source Code View"));

		this.rightSplitPane.setLeftComponent(this.clonePairListView.scrollPane);
		this.rightSplitPane.setRightComponent(sourcePanel);

		sourcePanel.add(uninterestingPane, BorderLayout.NORTH);
		sourcePanel.add(this.sourceSplitPane, BorderLayout.CENTER);

		final JPanel horizontalFilePanel = new JPanel(new BorderLayout());
		final JPanel verticalFilePanel = new JPanel(new BorderLayout());

		this.sourceSplitPane.setTopComponent(horizontalFilePanel);
		this.sourceSplitPane.setBottomComponent(verticalFilePanel);

		horizontalFilePanel.add(this.horizontalSourceCodeWindow.scrollPane, BorderLayout.CENTER);
		horizontalFilePanel.add(this.horizontalFilePathView, BorderLayout.NORTH);
		verticalFilePanel.add(this.verticalSourceCodeWindow.scrollPane, BorderLayout.CENTER);
		verticalFilePanel.add(this.verticalFilePathView, BorderLayout.NORTH);

		fileListPane.setDividerLocation(height / 2 - 70);
		directoryTreePane.setDividerLocation(height / 2 - 70);

		{
			final int minWidth = VISUAL_FILELISTVIEW_MIN_WIDTH + 47;
			this.baseSplitPane.addPropertyChangeListener(e -> {
				final int location = baseSplitPane.getDividerLocation();
				if (0 == location) {
				} else if (location < minWidth) {
					baseSplitPane.setDividerLocation(minWidth);
				}
			});
		}

		{
			final int minWidth = VISUAL_CLONEPAIRLISTVIEW_MIN_WIDTH + 27;
			final int maxWidth = VISUAL_CLONEPAIRLISTVIEW_MAX_WIDTH + 27;
			this.rightSplitPane.addPropertyChangeListener(e -> {
				final int location = rightSplitPane.getDividerLocation();
				if (0 == location) {
				} else if (location < minWidth) {
					rightSplitPane.setDividerLocation(minWidth);
				} else if (maxWidth < location) {
					rightSplitPane.setDividerLocation(maxWidth);
				}
			});
			this.rightSplitPane.setDividerLocation(minWidth);
		}

		this.setVisible(false);
	}

	public void init() {
		this.horizontalFileListView.init();
		this.horizontalDirectoryTreeView.init();
		this.verticalFileListView.init();
		this.verticalDirectoryTreeView.init();
		this.scatterPlotView.init();
	}

	public void reset() {
		SelectedEntities.<GUIFile>getInstance(HORIZONTAL_FILE).clear(this);
		SelectedEntities.<GUIFile>getInstance(VERTICAL_FILE).clear(this);

		this.toolBarPanel.reset();
		this.rnrSliderView.reset();
		this.scatterPlotView.reset();

		this.rnrSliderView.repaint();
		this.horizontalFileListView.repaint();
		this.verticalFileListView.repaint();
		this.horizontalDirectoryTreeView.repaint();
		this.verticalDirectoryTreeView.repaint();
		this.scatterPlotView.repaint();
		this.clonePairListView.repaint();
		this.horizontalSourceCodeWindow.repaint();
		this.verticalSourceCodeWindow.repaint();
		this.horizontalFilePathView.repaint();
		this.verticalFilePathView.repaint();
		this.toolBarPanel.repaint();
	}

	public void setHorizontalSplitSourceCodeView() {
		this.sourceSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		final int width = this.sourceSplitPane.getWidth();
		this.sourceSplitPane.setDividerLocation(width / 2);

		this.repaint();
	}

	public void setVerticalSplitSourceCodeView() {
		this.sourceSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		final int height = this.sourceSplitPane.getHeight();
		this.sourceSplitPane.setDividerLocation(height / 2);

		this.repaint();
	}

	public void showFileSelectionPanel() {
		this.baseSplitPane.setDividerLocation(FILELISTWIDTH);
	}

	public void hideFileSelectionPanel() {
		this.baseSplitPane.setDividerLocation(0);
	}

	public void showClonePairList() {
		if (null == this.rightSplitPane.getLeftComponent()) {
			this.rightSplitPane.setLeftComponent(this.clonePairListView.scrollPane);
		}
		this.rightSplitPane.setLastDividerLocation(VISUAL_CLONEPAIRLISTVIEW_MIN_WIDTH);
	}

	public void hideClonePairList() {
		if (null != this.rightSplitPane.getLeftComponent()) {
			this.rightSplitPane.remove(this.clonePairListView.scrollPane);
		}
		this.rightSplitPane.setLastDividerLocation(0);
	}

	@Override
	public void update(final Observable o, final Object arg) {
	}

	final private static int FILELISTWIDTH = 450;

	private final RNRSliderView rnrSliderView;

	private final FileListView horizontalFileListView;

	private final FileListView verticalFileListView;

	private final DirectoryTreeView horizontalDirectoryTreeView;

	private final DirectoryTreeView verticalDirectoryTreeView;

	private final ScatterPlotView scatterPlotView;

	private final ClonePairListView clonePairListView;

	private final SourceCodeWindow horizontalSourceCodeWindow;

	private final SourceCodeWindow verticalSourceCodeWindow;

	private final FilePathView horizontalFilePathView;

	private final FilePathView verticalFilePathView;

	private final ToolBarPanel toolBarPanel;

	private final JSplitPane baseSplitPane;

	private final JSplitPane rightSplitPane;

	private final UninterestingClonesReportingPanel uninterestingPane;

	private final JSplitPane sourceSplitPane;
}
