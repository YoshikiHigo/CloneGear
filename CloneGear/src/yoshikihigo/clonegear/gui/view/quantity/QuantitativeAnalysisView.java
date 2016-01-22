package yoshikihigo.clonegear.gui.view.quantity;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.quantity.clone.CloneListView;
import yoshikihigo.clonegear.gui.view.quantity.filelist.FileListView;
import yoshikihigo.clonegear.gui.view.quantity.filepath.FilePathView;
import yoshikihigo.clonegear.gui.view.quantity.grouplist.GroupListView;
import yoshikihigo.clonegear.gui.view.quantity.relatedfilelist.RelatedFileListView;
import yoshikihigo.clonegear.gui.view.quantity.relatedgrouplist.RelatedGroupListView;
import yoshikihigo.clonegear.gui.view.quantity.rnr.RNRSliderView;
import yoshikihigo.clonegear.gui.view.quantity.sourcecode.SourceCodeWindow;
import yoshikihigo.clonegear.gui.view.quantity.statistic.roc.ROCPanel;
import yoshikihigo.clonegear.gui.view.quantity.toolbar.ToolBarPanel;

public class QuantitativeAnalysisView extends JPanel implements ViewScale,
		QuantitativeViewInterface, Observer {

	public QuantitativeAnalysisView(final int width, final int height) {

		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(this);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(this);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(this);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(this);
		RNRValue.getInstance(RNR).addObserver(this);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this);

		this.baseSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		final JSplitPane groupSplitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, true);
		final JTabbedPane fileStatisticsTabbedPane = new JTabbedPane(
				JTabbedPane.TOP);
		this.rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		final JSplitPane fileSplitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, true);
		this.rightRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				true);
		final JPanel sourceCodePanel = new JPanel();

		this.rnrSliderView = new RNRSliderView();
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.rnrSliderView);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.rnrSliderView);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.rnrSliderView);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.rnrSliderView);
		RNRValue.getInstance(RNR).addObserver(this.rnrSliderView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.rnrSliderView);

		this.toolBarPanel = new ToolBarPanel(this);
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.toolBarPanel);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.toolBarPanel);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.toolBarPanel);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.toolBarPanel);
		RNRValue.getInstance(RNR).addObserver(this.toolBarPanel);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.toolBarPanel);

		this.groupListView = new GroupListView();
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.groupListView);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.groupListView);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.groupListView);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.groupListView);
		RNRValue.getInstance(RNR).addObserver(this.groupListView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.groupListView);

		this.relatedGroupListView = new RelatedGroupListView();
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.relatedGroupListView);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.relatedGroupListView);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.relatedGroupListView);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.relatedGroupListView);
		RNRValue.getInstance(RNR).addObserver(this.relatedGroupListView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.relatedGroupListView);

		this.fileListView = new FileListView();
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.fileListView);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.fileListView);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.fileListView);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.fileListView);
		RNRValue.getInstance(RNR).addObserver(this.fileListView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.fileListView);

		this.relatedFileListView = new RelatedFileListView();
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.relatedFileListView);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.relatedFileListView);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.relatedFileListView);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.relatedFileListView);
		RNRValue.getInstance(RNR).addObserver(this.relatedFileListView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.relatedFileListView);

		this.cloneListView = new CloneListView();
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.cloneListView);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.cloneListView);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.cloneListView);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.cloneListView);
		RNRValue.getInstance(RNR).addObserver(this.cloneListView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.cloneListView);

		this.filePathView = new FilePathView();
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.filePathView);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.filePathView);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.filePathView);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.filePathView);
		RNRValue.getInstance(RNR).addObserver(this.filePathView);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.filePathView);

		this.sourceCodeWindow = new SourceCodeWindow();
		SelectedEntities.<GUIFile> getInstance(GROUP).addObserver(
				this.sourceCodeWindow);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.sourceCodeWindow);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.sourceCodeWindow);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.sourceCodeWindow);
		RNRValue.getInstance(RNR).addObserver(this.sourceCodeWindow);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.sourceCodeWindow);

		this.rocPanel = new ROCPanel();
		SelectedEntities.<GUIFile> getInstance(GROUP)
				.addObserver(this.rocPanel);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).addObserver(
				this.rocPanel);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).addObserver(
				this.rocPanel);
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).addObserver(
				this.rocPanel);
		RNRValue.getInstance(RNR).addObserver(this.rocPanel);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(
				this.rocPanel);

		this.setLayout(new BorderLayout());
		this.add(this.baseSplitPane, BorderLayout.CENTER);
		this.add(this.rnrSliderView, BorderLayout.SOUTH);
		this.add(this.toolBarPanel, BorderLayout.NORTH);

		this.baseSplitPane.setLeftComponent(groupSplitPane);
		this.baseSplitPane.setRightComponent(fileStatisticsTabbedPane);

		groupSplitPane.setTopComponent(this.groupListView.scrollPane);
		groupSplitPane.setBottomComponent(this.relatedGroupListView
				.getScrollPane());

		fileStatisticsTabbedPane.add("File Information", this.rightSplitPane);
		fileStatisticsTabbedPane.addTab("Statistical Information",
				this.rocPanel);

		this.rightSplitPane.setLeftComponent(fileSplitPane);
		this.rightSplitPane.setRightComponent(this.rightRightSplitPane);

		fileSplitPane.setTopComponent(this.fileListView.scrollPane);
		fileSplitPane.setBottomComponent(this.relatedFileListView
				.getScrollPane());

		this.rightRightSplitPane
				.setLeftComponent(this.cloneListView.scrollPane);
		this.rightRightSplitPane.setRightComponent(sourceCodePanel);

		sourceCodePanel.setLayout(new BorderLayout());
		sourceCodePanel.add(this.filePathView, BorderLayout.NORTH);
		sourceCodePanel.add(this.sourceCodeWindow.getScrollPane(),
				BorderLayout.CENTER);

		this.rightSplitPane
				.setDividerLocation(QUANTITATIVE_FILELISTVIEW_MIN_WIDTH + 29);
		groupSplitPane.setDividerLocation(height / 2 - 60);
		fileSplitPane.setDividerLocation(height / 2 - 80);

		{
			final int minWidth = QUANTITATIVE_GROUPLISTVIEW_MIN_WIDTH + 29;
			final int maxWidth = QUANTITATIVE_GROUPLISTVIEW_MAX_WIDTH + 29;
			this.baseSplitPane.addPropertyChangeListener(e -> {
				final int location = baseSplitPane.getDividerLocation();
				if (0 == location) {
				} else if (location < minWidth) {
					baseSplitPane.setDividerLocation(minWidth);
				} else if (maxWidth < location) {
					baseSplitPane.setDividerLocation(maxWidth);
				}
			});
			this.baseSplitPane.setDividerLocation(minWidth);
		}

		{
			final int minWidth = QUANTITATIVE_FILELISTVIEW_MIN_WIDTH + 29;
			final int maxWidth = QUANTITATIVE_FILELISTVIEW_MAX_WIDTH + 29;
			rightSplitPane.addPropertyChangeListener(e -> {
				final int location = rightSplitPane.getDividerLocation();
				if (0 == location) {
				} else if (location < minWidth) {
					rightSplitPane.setDividerLocation(minWidth);
				} else if (maxWidth < location) {
					rightSplitPane.setDividerLocation(maxWidth);
				}
			});
		}

		{
			final int minWidth = QUANTITATIVE_CLONELISTVIEW_MIN_WIDTH + 27;
			final int maxWidth = QUANTITATIVE_CLONELISTVIEW_MAX_WIDTH + 27;
			this.rightRightSplitPane.addPropertyChangeListener(e -> {
				final int location = rightRightSplitPane.getDividerLocation();
				if (0 == location) {
				} else if (location < minWidth) {
					rightRightSplitPane.setDividerLocation(minWidth);
				} else if (maxWidth < location) {
					rightRightSplitPane.setDividerLocation(maxWidth);
				}
			});
			this.rightRightSplitPane.setDividerLocation(minWidth);
		}

		this.setVisible(false);
	}

	public void init() {
		this.groupListView.init();
		this.relatedGroupListView.init();
		this.fileListView.init();
		this.relatedFileListView.init();
	}

	public void reset() {
		SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).clear(this);
		SelectedEntities.<GUIFile> getInstance(RELATED_FILE).clear(this);
		SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).clear(this);
		SelectedEntities.<GUIFile> getInstance(GROUP).clear(this);
		RNRValue.getInstance(RNR).set(50);
		UninterestingClonesDisplay.getInstance(UNINTERESTING).set(true);

		this.toolBarPanel.reset();
		this.rnrSliderView.reset();

		this.rnrSliderView.repaint();
		this.toolBarPanel.repaint();
		this.groupListView.repaint();
		this.relatedGroupListView.repaint();
		this.fileListView.repaint();
		this.relatedFileListView.repaint();
		this.cloneListView.repaint();
		this.filePathView.repaint();
		this.sourceCodeWindow.repaint();
		this.rocPanel.repaint();
	}

	public void showGroupPanel() {
		this.baseSplitPane
				.setDividerLocation(QUANTITATIVE_GROUPLISTVIEW_MIN_WIDTH);
	}

	public void hideGroupPanel() {
		this.baseSplitPane.setDividerLocation(0);
	}

	public void showFilePanel() {
		this.rightSplitPane
				.setDividerLocation(QUANTITATIVE_FILELISTVIEW_MIN_WIDTH);
	}

	public void hideFilePanel() {
		this.rightSplitPane.setDividerLocation(0);
	}

	public void showCodeFragmentListView() {
		this.rightRightSplitPane
				.setLeftComponent(this.cloneListView.scrollPane);
		this.rightRightSplitPane
				.setDividerLocation(QUANTITATIVE_CLONELISTVIEW_MIN_WIDTH);
	}

	public void hideCodeFragmentListView() {
		this.rightRightSplitPane.remove(this.cloneListView.scrollPane);
		this.rightRightSplitPane.setDividerLocation(0);
	}

	@Override
	public void update(final Observable o, final Object arg) {
	}

	private final RNRSliderView rnrSliderView;

	private final ToolBarPanel toolBarPanel;

	private final GroupListView groupListView;

	private final RelatedGroupListView relatedGroupListView;

	private final FileListView fileListView;

	private final RelatedFileListView relatedFileListView;

	private final CloneListView cloneListView;

	private final FilePathView filePathView;

	private final SourceCodeWindow sourceCodeWindow;

	private final ROCPanel rocPanel;

	private final JSplitPane baseSplitPane;

	private final JSplitPane rightSplitPane;

	private final JSplitPane rightRightSplitPane;
}
