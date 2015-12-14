package yoshikihigo.clonegear.gui.view.visual.clonepair;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

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

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;

public class ClonePairListView extends JTable implements ViewScale, Observer,
		VisualViewInterface {

	public ClonePairListView() {

		this.selectionEventHandler = new SelectionEventHandler();

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.scrollPane.setBorder(new TitledBorder(new LineBorder(
				java.awt.Color.black), "Clone Pair List"));

		this.setTable(new HashSet<GUIClonePair>());
	}

	@Override
	public String getToolTipText(final MouseEvent e) {

		final Point stopPoint = e.getPoint();
		final int stopRow = this.rowAtPoint(stopPoint);
		final int modelRow = this.convertRowIndexToModel(stopRow);

		final int stopColumn = this.columnAtPoint(stopPoint);
		final int modelColumn = this.convertColumnIndexToModel(stopColumn);

		final ClonePairListViewModel model = (ClonePairListViewModel) this
				.getModel();
		final GUIClonePair[] clonePairs = model.getClonePairs();

		switch (modelColumn) {
		case 0:
			final GUIClone leftFragment = clonePairs[modelRow].left;
			return leftFragment.file.path;
		case 1:
			final GUIClone rightFragment = clonePairs[modelRow].right;
			return rightFragment.file.path;
		default:
			return null;
		}
	}

	public void update(Observable o, Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(HORIZONTAL_FILE)
					|| selectedEntities.getLabel().equals(VERTICAL_FILE)) {

				final SelectedEntities horizontalSelectedFiles = SelectedEntities
						.getInstance(HORIZONTAL_FILE);
				final SelectedEntities verticalSelectedFiles = SelectedEntities
						.getInstance(VERTICAL_FILE);

				if (horizontalSelectedFiles.isSet()
						&& verticalSelectedFiles.isSet()) {

					final GUIFile horizontalFile = (GUIFile) horizontalSelectedFiles
							.get().get(0);
					final GUIFile verticalFile = (GUIFile) verticalSelectedFiles
							.get().get(0);

					final int rnr = RNRValue.getInstance(RNR).get();
					final boolean uninterestingClonesDisplay = UninterestingClonesDisplay
							.getInstance(UNINTERESTING).isDisplay();

					final List<GUIClonePair> clonePairs;
					if (uninterestingClonesDisplay) {
						clonePairs = GUICloneManager.SINGLETON.getClonePairs(
								horizontalFile, verticalFile);
					} else {
						clonePairs = GUICloneManager.SINGLETON.getClonePairs(
								horizontalFile, verticalFile, rnr);
					}

					this.setTable(clonePairs);
					this.repaint();
				} else {
					this.setTable(new HashSet<GUIClonePair>());
					this.repaint();
				}

			} else if (selectedEntities.getLabel().equals(CLONEPAIR)) {

				if (!arg.equals(this)) {
				}
			}

		} else if ((o instanceof RNRValue)
				|| (o instanceof UninterestingClonesDisplay)) {

			final boolean display = UninterestingClonesDisplay.getInstance(
					UNINTERESTING).isDisplay();
			final TableRowSorter<ClonePairListViewModel> sorter = (TableRowSorter) this
					.getRowSorter();
			if (display) {
				sorter.setRowFilter(null);
			} else {
				sorter.setRowFilter(new RowFilter<ClonePairListViewModel, Integer>() {
					public boolean include(
							Entry<? extends ClonePairListViewModel, ? extends Integer> entry) {
						final int threshold = RNRValue.getInstance(RNR).get();
						final ClonePairListViewModel model = entry.getModel();
						final GUIClonePair clonePair = model.getClonePair(entry
								.getIdentifier());
						final int rnr = clonePair.getRNR();
						return threshold <= rnr;
					}
				});
			}

			final ClonePairListViewRenderer renderer = new ClonePairListViewRenderer();
			final TableColumnModel columnModel = this.getColumnModel();
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				columnModel.getColumn(i).setCellRenderer(renderer);
			}
			this.repaint();
		}

		this.addListeners();
	}

	private void setTable(final Collection<GUIClonePair> clonepairs) {

		final ClonePairListViewModel model = new ClonePairListViewModel(
				clonepairs);
		this.setModel(model);
		final RowSorter<ClonePairListViewModel> sorter = new TableRowSorter<ClonePairListViewModel>(
				model);
		this.setRowSorter(sorter);

		final ClonePairListViewRenderer renderer = new ClonePairListViewRenderer();

		final TableColumnModel columnModel = this.getColumnModel();
		final TableColumn[] column = new TableColumn[model.getColumnCount()];
		for (int i = 0; i < column.length; i++) {
			column[i] = columnModel.getColumn(i);
			column[i].setCellRenderer(renderer);
		}

		column[0].setMinWidth(VISUAL_CLONEPAIRLISTVIEW_COL_MIN_WIDTH);
		column[0].setMaxWidth(VISUAL_CLONEPAIRLISTVIEW_COL_MAX_WIDTH);
		column[1].setMinWidth(VISUAL_CLONEPAIRLISTVIEW_COL_MIN_WIDTH);
		column[1].setMaxWidth(VISUAL_CLONEPAIRLISTVIEW_COL_MAX_WIDTH);
	}

	private void addListeners() {
		this.getSelectionModel().addListSelectionListener(
				this.selectionEventHandler);
	}

	private void removeListeners() {
		this.getSelectionModel().removeListSelectionListener(
				this.selectionEventHandler);
	}

	final public JScrollPane scrollPane;

	final private SelectionEventHandler selectionEventHandler;

	class SelectionEventHandler implements ListSelectionListener {

		@Override
		public void valueChanged(final ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			final int[] selectedRow = ClonePairListView.this.getSelectedRows();
			final SortedSet<GUIClonePair> selectedClonePairs = new TreeSet<GUIClonePair>();
			for (int i = 0; i < selectedRow.length; i++) {

				final int modelIndex = ClonePairListView.this
						.convertRowIndexToModel(selectedRow[i]);
				final ClonePairListViewModel model = (ClonePairListViewModel) ClonePairListView.this
						.getModel();
				final GUIClonePair clonePair = model.getClonePair(modelIndex);
				selectedClonePairs.add(clonePair);
			}

			SelectedEntities.<GUIClonePair> getInstance(CLONEPAIR).setAll(
					selectedClonePairs, ClonePairListView.this);
		}
	}
}
