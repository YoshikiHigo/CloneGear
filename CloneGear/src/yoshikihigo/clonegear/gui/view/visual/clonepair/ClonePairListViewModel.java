package yoshikihigo.clonegear.gui.view.visual.clonepair;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import yoshikihigo.clonegear.gui.MessagePrinter;
import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;

public class ClonePairListViewModel extends AbstractTableModel {

	public ClonePairListViewModel(final Collection<GUIClonePair> clonepairs) {
		this.clonePairs = clonepairs.toArray(new GUIClonePair[0]);
	}

	public int getRowCount() {
		return this.clonePairs.length;
	}

	public int getColumnCount() {
		return 2;
	}

	public Object getValueAt(int row, int col) {

		switch (col) {
		case COL_HORIZONTAL:
			return this.getClonePair(row).left;
		case COL_VERTICAL:
			return this.getClonePair(row).right;
		default:
			MessagePrinter.ERR.println("Here shouldn't be reached!");
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int row) {
		return GUIClone.class;
	}

	@Override
	public String getColumnName(int col) {
		return TITLES[col];
	}

	public GUIClonePair getClonePair(final int row) {
		return this.clonePairs[row];
	}

	public GUIClonePair[] getClonePairs() {
		return this.clonePairs;
	}

	static final int COL_HORIZONTAL = 0;

	static final int COL_VERTICAL = 1;

	static final String[] TITLES = new String[] { "In Horizontal File",
			"In Vertical File" };

	final private GUIClonePair[] clonePairs;

}
