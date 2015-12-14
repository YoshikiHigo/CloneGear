package yoshikihigo.clonegear.gui.view.visual.filelist;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;

public class FileListViewModel extends AbstractTableModel implements
		VisualViewInterface {

	static final int COL_NAME = 0;
	static final int COL_LOC = 1;
	static final int COL_NOC = 2;
	static final int COL_ROC = 3;
	static final int COL_NOF = 4;

	static final String[] TITLES = new String[] { "File Name", "LOC(f)",
			"NOC(f)", "ROC(f)", "NOF(f)" };

	final private GUIFile[] files;

	public FileListViewModel(final Collection<GUIFile> files) {
		this.files = files.toArray(new GUIFile[0]);
	}

	@Override
	public int getRowCount() {
		return this.files.length;
	}

	@Override
	public int getColumnCount() {
		return TITLES.length;
	}

	@Override
	public Object getValueAt(final int row, final int col) {

		final int rnr = RNRValue.getInstance(RNR).get();
		switch (col) {
		case COL_NAME:
			return this.files[row].getFileName();
		case COL_LOC:
			return Integer.valueOf(this.files[row].loc);
		case COL_NOC:
			return Integer.valueOf(this.files[row].getNOC(rnr));
		case COL_ROC:
			return Double.valueOf(this.files[row].getROC(rnr));
		case COL_NOF:
			return Integer.valueOf(this.files[row].getNOF(rnr));

		default:
			return null;
		}
	}

	public Class<?> getColumnClass(final int col) {
		switch (col) {
		case COL_NAME:
			return String.class;
		case COL_LOC:
		case COL_NOC:
			return Integer.class;
		case COL_ROC:
			return Double.class;
		case COL_NOF:
			return Integer.class;
		default:
			return Object.class;
		}
	}

	@Override
	public String getColumnName(final int col) {
		return TITLES[col];
	}

	public GUIFile getFile(int row) {
		return this.files[row];
	}
}
