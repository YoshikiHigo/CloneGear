package yoshikihigo.clonegear.gui.view.quantity.relatedfilelist;

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

class RelatedFileListViewRenderer extends JLabel implements TableCellRenderer,
		QuantitativeViewInterface, ViewColors {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		setOpaque(true);

		final int modelRow = table.convertRowIndexToModel(row);
		final int modelColumn = table.convertColumnIndexToModel(column);
		final RelatedFileListViewModel model = (RelatedFileListViewModel) table
				.getModel();
		final GUIFile file = model.getFiles().get(modelRow);
		final int threshold = RNRValue.getInstance(RNR).get();

		if (isSelected) {
			setBackground(QUANTITATIVE_RELATEDFILELISTVIEW_SELECTION_BACKGROUND_COLOR);
			setForeground(QUANTITATIVE_RELATEDFILELISTVIEW_FOREGROUND_COLOR);
		} else {

			if (0 < file.getNOC(threshold)) {
				setBackground(QUANTITATIVE_RELATEDFILELISTVIEW_PRACTICAL_BACKGROUND_COLOR);
				setForeground(QUANTITATIVE_RELATEDFILELISTVIEW_FOREGROUND_COLOR);
			} else {
				setBackground(QUANTITATIVE_RELATEDFILELISTVIEW_UNINTERESTING_BACKGROUND_COLOR);
				setForeground(QUANTITATIVE_RELATEDFILELISTVIEW_FOREGROUND_COLOR);
			}
		}

		final boolean display = UninterestingClonesDisplay.getInstance(
				UNINTERESTING).isDisplay();

		switch (modelColumn) {
		case RelatedFileListViewModel.COL_NAME:
			this.setHorizontalAlignment(JLabel.LEFT);
			this.setText(file.getFileName());
			break;
		case RelatedFileListViewModel.COL_LOC:
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(Integer.toString(file.loc));
			break;
		case RelatedFileListViewModel.COL_NOC:
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
		case RelatedFileListViewModel.COL_ROC:
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
		case RelatedFileListViewModel.COL_NOF:
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
		default:
			MessagePrinter.ERR.println("Here shouldn't be reached!");
		}

		return this;
	}
}
