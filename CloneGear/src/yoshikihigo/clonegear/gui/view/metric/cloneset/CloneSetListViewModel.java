package yoshikihigo.clonegear.gui.view.metric.cloneset;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;

public class CloneSetListViewModel extends AbstractTableModel {

	static final int COL_ID = 0;
	static final int COL_RAD = 1;
	static final int COL_LEN = 2;
	static final int COL_RNR = 3;
	static final int COL_NIF = 4;
	static final int COL_POP = 5;
	static final int COL_DFL = 6;

	static final String[] TITLES = new String[] { "ID", "RAD(s)", "LEN(s)",
			"RNR(s)", "NIF(s)", "POP(s)", "DFL(s)" };

	final private List<GUICloneSet> clonesets;

	public CloneSetListViewModel(final List<GUICloneSet> clonesets) {
		this.clonesets = clonesets;
	}

	@Override
	public int getRowCount() {
		return this.clonesets.size();
	}

	@Override
	public int getColumnCount() {
		return TITLES.length;
	}

	@Override
	public Object getValueAt(int row, int col) {

		switch (col) {
		case COL_ID:
			return this.clonesets.get(row).getID();
		case COL_RAD:
			return this.clonesets.get(row).getRAD();
		case COL_LEN:
			return this.clonesets.get(row).getLEN();
		case COL_RNR:
			return this.clonesets.get(row).getRNR();
		case COL_NIF:
			return this.clonesets.get(row).getNIF();
		case COL_POP:
			return this.clonesets.get(row).getPOP();
		case COL_DFL:
			return this.clonesets.get(row).getDFL();
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int col) {
		return TITLES[col];
	}

	@Override
	public Class<Integer> getColumnClass(int col) {
		switch (col) {
		case COL_ID:
		case COL_RAD:
		case COL_LEN:
		case COL_RNR:
		case COL_NIF:
		case COL_POP:
		case COL_DFL:
			return Integer.class;
		default:
			return null;
		}
	}

	public GUICloneSet getCloneSet(final int row) {
		return this.clonesets.get(row);
	}
}
