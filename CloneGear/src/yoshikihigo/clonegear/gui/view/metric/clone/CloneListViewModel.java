package yoshikihigo.clonegear.gui.view.metric.clone;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;

public class CloneListViewModel extends AbstractTableModel {

	static final int COL_ID = 0;
	static final int COL_LOCATION = 1;
	static final int COL_LENGTH = 2;

	static final String[] TITLES = new String[] { "File ID", "Location",
			"Length" };

	final private List<GUIClone> clones;

	public CloneListViewModel(final List<GUIClone> clones) {
		this.clones = new ArrayList<>(clones);
	}

	@Override
	public int getRowCount() {
		return this.clones.size();
	}

	@Override
	public int getColumnCount() {
		return TITLES.length;
	}

	@Override
	public Object getValueAt(final int row, final int col) {

		switch (col) {
		case COL_ID:
		case COL_LOCATION:
			return this.clones.get(row);
		case COL_LENGTH:
			return this.clones.get(row).getLOC();
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int col) {

		switch (col) {
		case COL_ID:
		case COL_LOCATION:
			return GUIClone.class;
		case COL_LENGTH:
			return Integer.class;
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int col) {
		return TITLES[col];
	}

	public List<GUIClone> getClones() {
		return new ArrayList<GUIClone>(this.clones);
	}
}
