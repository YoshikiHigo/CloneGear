package yoshikihigo.clonegear.gui.view.visual.filelist;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.data.file.IDIndexMap;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.visual.DIRECTION;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;

public class FileListView extends JTable implements ViewScale, Observer,
		VisualViewInterface {

	class SelectionEventHandler implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			final int firstIndex = e.getFirstIndex();
			final int lastIndex = e.getLastIndex();

			for (int i = firstIndex; i <= lastIndex; i++) {

				final int modelIndex = FileListView.this
						.convertRowIndexToModel(i);
				final GUIFile file = FileListView.this.model
						.getFile(modelIndex);

				switch (direction) {
				case HORIZONTAL:
					if (FileListView.this.getSelectionModel()
							.isSelectedIndex(i)) {
						SelectedEntities.<GUIFile> getInstance(HORIZONTAL_FILE)
								.add(file, FileListView.this);
					} else {
						SelectedEntities.<GUIFile> getInstance(HORIZONTAL_FILE)
								.remove(file, FileListView.this);
					}
					break;
				case VERTICAL:
					if (FileListView.this.getSelectionModel()
							.isSelectedIndex(i)) {
						SelectedEntities.<GUIFile> getInstance(VERTICAL_FILE)
								.add(file, FileListView.this);
					} else {
						SelectedEntities.<GUIFile> getInstance(VERTICAL_FILE)
								.remove(file, FileListView.this);
					}
					break;
				}
			}
		}
	}

	private final JScrollPane scrollPane;
	private final SelectionEventHandler selectionEventHandler;
	private final DIRECTION direction;
	private FileListViewModel model;

	public FileListView(final DIRECTION direction) {

		super();

		this.direction = direction;

		this.model = new FileListViewModel(GUIFileManager.SINGLETON.getFiles());
		this.setModel(this.model);
		final RowSorter<FileListViewModel> sorter = new TableRowSorter<>(
				this.model);
		this.setRowSorter(sorter);

		final FileListViewRenderer renderer = new FileListViewRenderer(
				direction);
		final TableColumnModel columnModel = this.getColumnModel();
		final TableColumn[] column = new TableColumn[this.model
				.getColumnCount()];
		for (int i = 0; i < column.length; i++) {
			column[i] = columnModel.getColumn(i);
			column[i].setCellRenderer(renderer);
		}

		column[0].setMinWidth(VISUAL_FILELISTVIEW_FILENAME_COL_MIN_WIDTH);
		column[1].setMinWidth(VISUAL_FILELISTVIEW_LOC_COL_MIN_WIDTH);
		column[1].setMaxWidth(VISUAL_FILELISTVIEW_LOC_COL_MAX_WIDTH);
		column[2].setMinWidth(VISUAL_FILELISTVIEW_NOC_COL_MIN_WIDTH);
		column[2].setMaxWidth(VISUAL_FILELISTVIEW_NOC_COL_MAX_WIDTH);
		column[3].setMinWidth(VISUAL_FILELISTVIEW_ROC_COL_MIN_WIDTH);
		column[3].setMaxWidth(VISUAL_FILELISTVIEW_ROC_COL_MAX_WIDTH);
		column[4].setMinWidth(VISUAL_FILELISTVIEW_NOF_COL_MIN_WIDTH);
		column[4].setMaxWidth(VISUAL_FILELISTVIEW_NOF_COL_MAX_WIDTH);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.scrollPane.setBorder(new javax.swing.border.LineBorder(
				java.awt.Color.black));

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.selectionEventHandler = new SelectionEventHandler();

		this.addListeners();
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	public void init() {
		this.clearSelection();
	}

	@Override
	public void update(Observable o, Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;

			if ((selectedEntities.getLabel().equals(HORIZONTAL_FILE) && this.direction == DIRECTION.HORIZONTAL)
					|| (selectedEntities.getLabel().equals(VERTICAL_FILE) && this.direction == DIRECTION.VERTICAL)) {

				if (!arg.equals(this)) {

					this.clearSelection();

					final List<GUIFile> files = ((SelectedEntities) o).get();

					for (final GUIFile file : files) {
						final int groupID = file.groupID;
						final int fileID = file.fileID;
						final int modelIndex = IDIndexMap.SINGLETON.getIndex(
								groupID, fileID);
						final int viewIndex = this
								.convertRowIndexToView(modelIndex);
						this.addRowSelectionInterval(viewIndex, viewIndex);
					}
				}
			}
		} else if (o instanceof RNRValue) {

			final FileListViewRenderer renderer = new FileListViewRenderer(
					this.direction);
			final TableColumnModel columnModel = this.getColumnModel();
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				columnModel.getColumn(i).setCellRenderer(renderer);
			}

			this.repaint();

		} else if (o instanceof UninterestingClonesDisplay) {

			final FileListViewRenderer renderer = new FileListViewRenderer(
					this.direction);
			final TableColumnModel columnModel = this.getColumnModel();
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				columnModel.getColumn(i).setCellRenderer(renderer);
			}

			this.repaint();
		}

		this.addListeners();
	}

	@Override
	public String getToolTipText(final MouseEvent e) {

		final Point stopPoint = e.getPoint();
		final int stopRow = this.rowAtPoint(stopPoint);
		final int modelRow = this.convertRowIndexToModel(stopRow);

		final FileListViewModel model = (FileListViewModel) this.getModel();
		final GUIFile file = model.getFile(modelRow);
		return file.path;
	}

	private void addListeners() {
		this.getSelectionModel().addListSelectionListener(
				this.selectionEventHandler);
	}

	private void removeListeners() {
		this.getSelectionModel().removeListSelectionListener(
				this.selectionEventHandler);
	}
}
