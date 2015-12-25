package yoshikihigo.clonegear.gui.view.quantity.filelist;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import yoshikihigo.clonegear.gui.MessagePrinter;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

class FileListViewRenderer extends JLabel implements TableCellRenderer,
		QuantitativeViewInterface {

	public Component getTableCellRendererComponent(final JTable table,
			final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {

		setOpaque(true);

		if (isSelected) {
			setBackground(ViewColors.TABLE_SELECTION_COLOR);
			setForeground(ViewColors.TABLE_FOREGROUND_COLOR);
		} else {
			setBackground(ViewColors.TABLE_UNSELECTION_COLOR);
			setForeground(ViewColors.TABLE_FOREGROUND_COLOR);
		}

		final int modelRow = table.convertRowIndexToModel(row);
		final int modelColumn = table.convertColumnIndexToModel(column);
		final FileListViewModel model = (FileListViewModel) table.getModel();
		final GUIFile file = model.getFiles().get(modelRow);
		final boolean display = UninterestingClonesDisplay.getInstance(
				UNINTERESTING).isDisplay();
		final int threshold = RNRValue.getInstance(RNR).get();
		switch (modelColumn) {
		case FileListViewModel.COL_NAME: {
			this.setHorizontalAlignment(JLabel.LEFT);
			this.setText(file.getFileName());
			break;
		}
		case FileListViewModel.COL_LOC: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(value.toString());
			break;
		}
		case FileListViewModel.COL_NOC: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			if (display) {
				final StringBuilder sb = new StringBuilder();
				sb.append(file.getNOC(threshold));
				sb.append("(");
				sb.append(file.getNOC());
				sb.append(")");
				this.setText(sb.toString());
			} else {
				this.setText(Integer.toString(file.getNOC(threshold)));
			}
			break;
		}
		case FileListViewModel.COL_ROC: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			if (display) {
				final StringBuilder sb = new StringBuilder();
				sb.append(ViewScale.ROC_FORMAT.format(file.getROC(threshold)));
				sb.append("(");
				sb.append(ViewScale.ROC_FORMAT.format(file.getROC()));
				sb.append(")");
				this.setText(sb.toString());
			} else {
				this.setText(ViewScale.ROC_FORMAT.format(file.getROC(threshold)));
			}
			break;
		}
		case FileListViewModel.COL_NOF: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			if (display) {
				final StringBuilder sb = new StringBuilder();
				sb.append(file.getNOF(threshold));
				sb.append("(");
				sb.append(file.getNOF());
				sb.append(")");
				this.setText(sb.toString());
			} else {
				this.setText(Integer.toString(file.getNOF(threshold)));
			}
			break;
		}
		default: {
			MessagePrinter.ERR.println("Here shouldn't be reached!");
		}
		}

		return this;
	}
}
