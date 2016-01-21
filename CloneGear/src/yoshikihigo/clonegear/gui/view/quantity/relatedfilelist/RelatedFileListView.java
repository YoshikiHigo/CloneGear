package yoshikihigo.clonegear.gui.view.quantity.relatedfilelist;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class RelatedFileListView extends JTable implements ViewScale, Observer,
		QuantitativeViewInterface {

	class SelectionEventHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			final int firstIndex = e.getFirstIndex();
			final int lastIndex = e.getLastIndex();

			for (int i = firstIndex; i <= lastIndex; i++) {

				final int modelIndex = RelatedFileListView.this
						.convertRowIndexToModel(i);
				final RelatedFileListViewModel model = (RelatedFileListViewModel) RelatedFileListView.this
						.getModel();
				final GUIFile relatedFile = model.getFiles().get(modelIndex);

				if (RelatedFileListView.this.getSelectionModel()
						.isSelectedIndex(i)) {
					SelectedEntities.<GUIFile> getInstance(RELATED_FILE).add(
							relatedFile, RelatedFileListView.this);
				} else {
					SelectedEntities.<GUIFile> getInstance(RELATED_FILE)
							.remove(relatedFile, RelatedFileListView.this);
				}
			}

			if (RelatedFileListView.this.getSelectionModel().isSelectionEmpty()) {
				RelatedFileListView.this.relatedFileViewPopupMenu
						.setSelectable(false);
			} else {
				RelatedFileListView.this.relatedFileViewPopupMenu
						.setSelectable(true);
			}
		}
	}

	class MouseEventHandler extends MouseAdapter {

		public void mouseClicked(final MouseEvent e) {

			int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {
				RelatedFileListView.this.relatedFileViewPopupMenu.show(
						e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	final private JScrollPane scrollPane;
	final private RelatedFileListViewPopupMenu relatedFileViewPopupMenu;
	final private SelectionEventHandler selectionEventHandler;
	final private MouseEventHandler mouseEventHandler;

	public RelatedFileListView() {

		super();

		this.relatedFileViewPopupMenu = new RelatedFileListViewPopupMenu(this);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Releted File List"));

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.selectionEventHandler = new SelectionEventHandler();
		this.mouseEventHandler = new MouseEventHandler();

		this.setTable(null, new ArrayList<GUIFile>());

		this.addListeners();
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	@Override
	public void update(final Observable o, final Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedFiles = (SelectedEntities) o;
			if (selectedFiles.getLabel().equals(SELECTED_FILE)) {

				if (selectedFiles.isSet()) {

					final GUIFile selectedFile = (GUIFile) selectedFiles.get()
							.get(0);
					final List<GUIFile> relatedFiles = GUIFileManager
							.instance().getRelatedFiles(selectedFile);
					this.setTable(selectedFile, relatedFiles);
					this.repaint();
				}

				else {

					this.setTable(null, new ArrayList<GUIFile>());
					this.repaint();
				}
			}

			else if (selectedFiles.getLabel().equals(RELATED_FILE)) {
			}

			else if (selectedFiles.getLabel().equals(GROUP)) {
				this.setTable(null, new ArrayList<>());
				this.repaint();
			}

		} else if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(CODEFRAGMENT)) {
			}

		} else if (o instanceof RNRValue) {

			final RNRValue rnrValue = (RNRValue) o;
			if (rnrValue.getLabel().equals(RNR)) {

				final RelatedFileListViewRenderer renderer = new RelatedFileListViewRenderer();
				final TableColumnModel columnModel = this.getColumnModel();
				for (int i = 0; i < columnModel.getColumnCount(); i++) {
					columnModel.getColumn(i).setCellRenderer(renderer);
				}
				this.repaint();
			}

		} else if (o instanceof UninterestingClonesDisplay) {

			final boolean display = ((UninterestingClonesDisplay) o)
					.isDisplay();
			final TableRowSorter<RelatedFileListViewModel> sorter = (TableRowSorter) this
					.getRowSorter();
			if (display) {
				sorter.setRowFilter(null);
			} else {
				sorter.setRowFilter(new RowFilter<RelatedFileListViewModel, Integer>() {
					public boolean include(
							Entry<? extends RelatedFileListViewModel, ? extends Integer> entry) {
						final int threshold = RNRValue.getInstance(RNR).get();
						final RelatedFileListViewModel model = entry.getModel();
						final GUIFile file = model.getFiles().get(
								entry.getIdentifier());
						final int noc = file.getNOC(threshold);
						return 0 < noc;
					}
				});
			}

			final RelatedFileListViewRenderer renderer = new RelatedFileListViewRenderer();
			final TableColumnModel columnModel = this.getColumnModel();
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				columnModel.getColumn(i).setCellRenderer(renderer);
			}
			this.repaint();
		}

		this.addListeners();
	}

	public void init() {
	}

	private void addListeners() {

		this.getSelectionModel().addListSelectionListener(
				this.selectionEventHandler);
		this.addMouseListener(this.mouseEventHandler);
	}

	private void removeListeners() {

		this.getSelectionModel().removeListSelectionListener(
				this.selectionEventHandler);
		this.removeMouseListener(this.mouseEventHandler);
	}

	@Override
	public String getToolTipText(MouseEvent e) {

		final Point stopPoint = e.getPoint();
		final int stopRow = this.rowAtPoint(stopPoint);
		final int modelIndex = this.convertRowIndexToModel(stopRow);

		final RelatedFileListViewModel model = (RelatedFileListViewModel) this
				.getModel();
		final GUIFile relatedFile = model.getFiles().get(modelIndex);
		return relatedFile.path;
	}

	void setAsSelectedFile() {

		final RelatedFileListViewModel model = (RelatedFileListViewModel) this
				.getModel();

		final int[] selectedIndexes = this.getSelectedRows();
		final Set<GUIFile> selectedFiles = new HashSet<GUIFile>();

		for (int i = 0; i < selectedIndexes.length; i++) {
			final int modelIndex = this
					.convertRowIndexToModel(selectedIndexes[i]);
			final GUIFile relatedFile = model.getFiles().get(modelIndex);
			final int groupID = relatedFile.groupID;
			final int fileID = relatedFile.fileID;
			final GUIFile file = GUIFileManager.instance().getFile(groupID,
					fileID);
			selectedFiles.add(file);
		}
	}

	private void setTable(final GUIFile target, final List<GUIFile> files) {

		final RelatedFileListViewModel model = new RelatedFileListViewModel(
				target, files);
		this.setModel(model);
		final RowSorter<RelatedFileListViewModel> sorter = new TableRowSorter<>(
				model);
		this.setRowSorter(sorter);

		final RelatedFileListViewRenderer renderer = new RelatedFileListViewRenderer();
		final TableColumnModel columnModel = this.getColumnModel();
		final TableColumn[] column = new TableColumn[model.getColumnCount()];
		for (int i = 0; i < column.length; i++) {
			column[i] = columnModel.getColumn(i);
			column[i].setCellRenderer(renderer);
		}

		column[0]
				.setMinWidth(QUANTITATIVE_RELATEDFILELISTVIEW_FILENAME_COL_MIN_WIDTH);
		column[0]
				.setMaxWidth(QUANTITATIVE_RELATEDFILELISTVIEW_FILENAME_COL_MAX_WIDTH);
		column[1]
				.setMinWidth(QUANTITATIVE_RELATEDFILELISTVIEW_LOC_COL_MIN_WIDTH);
		column[1]
				.setMaxWidth(QUANTITATIVE_RELATEDFILELISTVIEW_LOC_COL_MAX_WIDTH);
		column[2]
				.setMinWidth(QUANTITATIVE_RELATEDFILELISTVIEW_NOC_COL_MIN_WIDTH);
		column[2]
				.setMaxWidth(QUANTITATIVE_RELATEDFILELISTVIEW_NOC_COL_MAX_WIDTH);
		column[3]
				.setMinWidth(QUANTITATIVE_RELATEDFILELISTVIEW_ROC_COL_MIN_WIDTH);
		column[3]
				.setMaxWidth(QUANTITATIVE_RELATEDFILELISTVIEW_ROC_COL_MAX_WIDTH);
		column[4]
				.setMinWidth(QUANTITATIVE_RELATEDFILELISTVIEW_NOF_COL_MIN_WIDTH);
		column[4]
				.setMaxWidth(QUANTITATIVE_RELATEDFILELISTVIEW_NOF_COL_MAX_WIDTH);

	}
}
