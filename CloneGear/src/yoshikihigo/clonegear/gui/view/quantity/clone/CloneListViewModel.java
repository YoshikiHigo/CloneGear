package yoshikihigo.clonegear.gui.view.quantity.clone;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneLabelManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;

public class CloneListViewModel extends AbstractTableModel {

	static final int COL_LOCATION = 0;
	static final int COL_LENGTH = 1;
	static final int COL_DISPERSIVITY = 2;
	static final int COL_EQUIVALENCE = 3;

	static final String COL_LOCATION_STRING = "Location";
	static final String COL_LENGTH_STRING = "Length";
	static final String COL_DISPERSIVITY_STRING = "Dispersivity";
	static final String COL_EQUIVALENCE_STRING = "Equivalence";

	static final String DENSE_STRING = "dense";
	static final String MIDDLE_STRING = "middle";
	static final String SCATTERED_STRING = "scattered";

	final private List<GUIClone> clones;

	public CloneListViewModel(final List<GUIClone> clones) {
		this.clones = clones;
	}

	@Override
	public int getRowCount() {
		return this.clones.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int row, int col) {

		final GUIClone clone = this.clones.get(row);
		final GUICloneSet cloneset = GUICloneManager.SINGLETON
				.getCloneSet(clone);

		switch (col) {
		case COL_LOCATION:
			return GUICloneLabelManager.SINGLETON.getLocationLabel(clone);
		case COL_LENGTH:
			return clone.getLOC();
		case COL_DISPERSIVITY:
			return cloneset.getRAD();
		case COL_EQUIVALENCE:
			return cloneset.getPOP();
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(final int col) {

		switch (col) {
		case COL_LOCATION:
			return GUIClone.class;
		case COL_LENGTH:
		case COL_DISPERSIVITY:
		case COL_EQUIVALENCE:
			return Integer.class;
		default:
			return Object.class;
		}
	}

	@Override
	public String getColumnName(final int col) {

		if (col == COL_LOCATION)
			return COL_LOCATION_STRING;
		else if (col == COL_LENGTH)
			return COL_LENGTH_STRING;
		else if (col == COL_DISPERSIVITY)
			return COL_DISPERSIVITY_STRING;
		else if (col == COL_EQUIVALENCE)
			return COL_EQUIVALENCE_STRING;
		else
			return null;

	}

	public List<GUIClone> getClones() {
		return new ArrayList<GUIClone>(this.clones);
	}
}
