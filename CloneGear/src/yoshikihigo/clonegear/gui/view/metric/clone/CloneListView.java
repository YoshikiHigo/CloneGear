package yoshikihigo.clonegear.gui.view.metric.clone;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;

public class CloneListView extends JTable implements ViewScale, Observer,
		MetricViewInterface {

	class SelectionEventHandler implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			final int startIndex = e.getFirstIndex();
			final int endIndex = e.getLastIndex();

			final CloneListViewModel model = (CloneListViewModel) CloneListView.this
					.getModel();
			final List<GUIClone> clones = model.getClones();

			for (int i = startIndex; i <= endIndex; i++) {

				if (clones.size() <= i) {
					break;
				}

				final int modelIndex = CloneListView.this
						.convertRowIndexToModel(i);
				if (CloneListView.this.isRowSelected(i)) {
					SelectedEntities.<GUIClone> getInstance(CLONE).add(
							clones.get(modelIndex), CloneListView.this);
				} else {
					SelectedEntities.<GUIClone> getInstance(CLONE).remove(
							clones.get(modelIndex), CloneListView.this);
				}
			}
		}
	}

	final private JScrollPane scrollPane;
	final private SelectionEventHandler selectionEventHandler;

	public CloneListView() {

		super();

		this.setTable(new ArrayList<GUIClone>());

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Clone List"));

		this.selectionEventHandler = new SelectionEventHandler();
	}

	public void init() {
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	@Override
	public void update(Observable o, Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(FILTERED_CLONESET)) {

				this.setTable(new ArrayList<GUIClone>());
				this.repaint();

			} else if (selectedEntities.getLabel().equals(SELECTED_CLONESET)) {

				final List<GUICloneSet> clonesets = selectedEntities.get();
				final List<GUIClone> clones = GUICloneManager
						.getClones(clonesets);

				this.setTable(clones);
				this.repaint();

			} else if (selectedEntities.getLabel().equals(CLONE)) {

				if (!arg.equals(this)) {
					final List<GUICloneSet> clonesets = selectedEntities.get();
					final List<GUIClone> clones = GUICloneManager
							.getClones(clonesets);

					this.setTable(clones);
					this.repaint();
				}
			}
		}

		this.addListeners();

	}

	@Override
	public String getToolTipText(final MouseEvent e) {

		final Point stopPoint = e.getPoint();
		final int stopRow = this.rowAtPoint(stopPoint);
		final int modelRow = this.convertRowIndexToModel(stopRow);

		final CloneListViewModel model = (CloneListViewModel) this.getModel();
		final List<GUIClone> clones = model.getClones();
		final int groupID = clones.get(modelRow).groupID;
		final int fileID = clones.get(modelRow).fileID;

		final GUIFile file = GUIFileManager.SINGLETON.getFile(groupID, fileID);
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

	private void setTable(final List<GUIClone> clones) {

		final CloneListViewModel model = new CloneListViewModel(clones);
		this.setModel(model);
		final TableRowSorter<CloneListViewModel> sorter = new TableRowSorter<>(
				model);
		sorter.setComparator(CloneListViewModel.COL_ID, GUIClone.ID_COMPARATOR);
		sorter.setComparator(CloneListViewModel.COL_LOCATION,
				GUIClone.LOCATION_COMPARATOR);
		this.setRowSorter(sorter);

		final CloneListViewRenderer renderer = new CloneListViewRenderer();
		final TableColumnModel columnModel = this.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setCellRenderer(renderer);
		}
	}
}
