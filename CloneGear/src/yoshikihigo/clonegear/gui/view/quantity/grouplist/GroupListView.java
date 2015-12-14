package yoshikihigo.clonegear.gui.view.quantity.grouplist;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class GroupListView extends JTable implements ViewScale, Observer,
		QuantitativeViewInterface {

	class SelectionEventHandler implements ListSelectionListener {

		public void valueChanged(final ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			SelectedEntities.<GUIFile> getInstance(RELATED_FILE).clear(
					GroupListView.this);
			SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).clear(
					GroupListView.this);
			SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).clear(
					GroupListView.this);

			final int firstIndex = e.getFirstIndex();
			final int lastIndex = e.getLastIndex();

			for (int i = firstIndex; i <= lastIndex; i++) {

				final int modelIndex = GroupListView.this
						.convertRowIndexToModel(i);
				final GroupListViewModel model = (GroupListViewModel) GroupListView.this
						.getModel();
				final Collection<GUIFile> group = model.getFiles(modelIndex);

				if (GroupListView.this.getSelectionModel().isSelectedIndex(i)) {
					SelectedEntities.<GUIFile> getInstance(GROUP).addAll(group,
							GroupListView.this);
				} else {
					SelectedEntities.<GUIFile> getInstance(GROUP).removeAll(
							group, GroupListView.this);
				}
			}
		}
	}

	final public JScrollPane scrollPane;

	final private SelectionEventHandler selectionEventHandler;

	public GroupListView() {

		super();

		{
			final GroupListViewModel model = new GroupListViewModel();
			this.setModel(model);
			final RowSorter<GroupListViewModel> sorter = new TableRowSorter<>(
					model);
			this.setRowSorter(sorter);

			final GroupListViewRenderer renderer = new GroupListViewRenderer();

			final TableColumnModel columnModel = this.getColumnModel();
			final TableColumn[] column = new TableColumn[model.getColumnCount()];
			for (int i = 0; i < column.length; i++) {
				column[i] = columnModel.getColumn(i);
				column[i].setCellRenderer(renderer);
			}

			column[0].setMinWidth(QUANTITATIVE_GROUPLISTVIEW_ID_COL_MIN_WIDTH);
			column[0].setMaxWidth(QUANTITATIVE_GROUPLISTVIEW_ID_COL_MAX_WIDTH);
			column[1].setMinWidth(QUANTITATIVE_GROUPLISTVIEW_NOF_COL_MIN_WIDTH);
			column[1].setMaxWidth(QUANTITATIVE_GROUPLISTVIEW_NOF_COL_MAX_WIDTH);
			column[2].setMinWidth(QUANTITATIVE_GROUPLISTVIEW_LOC_COL_MIN_WIDTH);
			// column[2].setMaxWidth(QUANTITATIVE_GROUPLISTVIEW_LOC_COL_MAX_WIDTH);
			column[3].setMinWidth(QUANTITATIVE_GROUPLISTVIEW_NOC_COL_MIN_WIDTH);
			// column[3].setMaxWidth(QUANTITATIVE_GROUPLISTVIEW_NOC_COL_MAX_WIDTH);
			column[4].setMinWidth(QUANTITATIVE_GROUPLISTVIEW_ROC_COL_MIN_WIDTH);
		}

		{
			this.scrollPane = new JScrollPane();
			this.scrollPane.setViewportView(this);
			this.scrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			this.scrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}

		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Group List"));

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.selectionEventHandler = new SelectionEventHandler();

		this.addListeners();
	}

	public void init() {
	}

	@Override
	public void update(final Observable o, final Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(SELECTED_FILE)) {

			} else if (selectedEntities.getLabel().equals(RELATED_FILE)) {

			} else if (selectedEntities.getLabel().equals(GROUP)) {

				if (!arg.equals(this)) {

					this.clearSelection();

					for (final GUIFile file : (List<GUIFile>) selectedEntities
							.get()) {
						final int groupID = file.groupID;
						final int viewIndex = this
								.convertRowIndexToView(groupID);
						this.setRowSelectionInterval(viewIndex, viewIndex);
					}
				}
			}

		} else if (o instanceof RNRValue) {

			RNRValue rnrValue = (RNRValue) o;
			if (rnrValue.getLabel().equals(RNR)) {

				final GroupListViewRenderer renderer = new GroupListViewRenderer();
				final TableColumnModel columnModel = this.getColumnModel();
				for (int i = 0; i < columnModel.getColumnCount(); i++) {
					columnModel.getColumn(i).setCellRenderer(renderer);
				}

				this.repaint();
			}

		} else if (o instanceof UninterestingClonesDisplay) {

			final GroupListViewRenderer renderer = new GroupListViewRenderer();
			final TableColumnModel columnModel = this.getColumnModel();
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				columnModel.getColumn(i).setCellRenderer(renderer);
			}

			this.repaint();
		}

		this.addListeners();
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
