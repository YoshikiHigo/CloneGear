package yoshikihigo.clonegear.gui.view.quantity.grouplist;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class GroupListViewModel extends AbstractTableModel implements
		QuantitativeViewInterface {

	static final int COL_ID = 0;
	static final int COL_NOF = 1;
	static final int COL_LOC = 2;
	static final int COL_NOC = 3;
	static final int COL_ROC = 4;

	private static final String[] TITLE = new String[] { "ID", "NOF(g)",
			"LOC(g)", "NOC(g)", "ROC(g)" };

	public GroupListViewModel() {
	}

	public int getRowCount() {
		return GUIFileManager.SINGLETON.getGroupCount();
	}

	public int getColumnCount() {
		return TITLE.length;
	}

	public Object getValueAt(int row, int col) {

		final int threshold = RNRValue.getInstance(RNR).get();
		switch (col) {
		case COL_ID:
			return Integer.valueOf(row);
		case COL_NOF:
			return Integer.valueOf(GUIFileManager.SINGLETON.getFileCount(row));
		case COL_LOC:
			return Integer.valueOf(GUIFileManager.SINGLETON.getGroupLOC(row));
		case COL_NOC:
			return Integer.valueOf(GUIFileManager.SINGLETON.getGroupNOC(row,
					threshold));
		case COL_ROC:
			return Double.valueOf(GUIFileManager.SINGLETON.getGroupROC(row,
					threshold));
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
		case COL_ID:
		case COL_NOF:
		case COL_LOC:
		case COL_NOC:
			return Integer.class;
		case COL_ROC:
			return Double.class;
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int col) {
		return TITLE[col];
	}

	public Collection<GUIFile> getFiles(final int index) {
		return GUIFileManager.SINGLETON.getFiles(index);
	}
}
