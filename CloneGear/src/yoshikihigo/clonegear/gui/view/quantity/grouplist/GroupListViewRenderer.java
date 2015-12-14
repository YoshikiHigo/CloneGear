package yoshikihigo.clonegear.gui.view.quantity.grouplist;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

class GroupListViewRenderer extends JLabel implements TableCellRenderer,
		QuantitativeViewInterface, ViewScale {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		setOpaque(true);

		if (isSelected) {
			setBackground(ViewColors.TABLE_SELECTION_COLOR);
			setForeground(ViewColors.TABLE_FOREGROUND_COLOR);
		} else {
			setBackground(ViewColors.TABLE_UNSELECTION_COLOR);
			setForeground(ViewColors.TABLE_FOREGROUND_COLOR);
		}

		final int threshold = RNRValue.getInstance(RNR).get();
		final boolean display = UninterestingClonesDisplay.getInstance(
				UNINTERESTING).isDisplay();
		final int modelRow = table.convertRowIndexToModel(row);
		final int modelColumn = table.convertColumnIndexToModel(column);

		switch (modelColumn) {
		case GroupListViewModel.COL_ID:
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setText(value.toString());
			break;
		case GroupListViewModel.COL_NOF:
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(value.toString());
			break;
		case GroupListViewModel.COL_LOC:
			this.setHorizontalAlignment(JLabel.RIGHT);
			// this.setText(Integer.toString(FileManager.SINGLETON.getGroupLOC(modelRow)));
			this.setText(value.toString());
			break;
		case GroupListViewModel.COL_NOC:
			this.setHorizontalAlignment(JLabel.RIGHT);
			if (display) {
				final StringBuilder sb = new StringBuilder();
				sb.append(GUIFileManager.SINGLETON.getGroupNOC(modelRow,
						threshold));
				sb.append("(");
				sb.append(GUIFileManager.SINGLETON.getGroupNOC(modelRow));
				sb.append(")");
				this.setText(sb.toString());
			} else {
				this.setText(Integer.toString(GUIFileManager.SINGLETON
						.getGroupNOC(modelRow, threshold)));
			}
			break;
		case GroupListViewModel.COL_ROC:
			this.setHorizontalAlignment(JLabel.RIGHT);
			if (display) {
				final StringBuilder sb = new StringBuilder();
				sb.append(ROC_FORMAT.format(GUIFileManager.SINGLETON
						.getGroupROC(modelRow, threshold)));
				sb.append("(");
				sb.append(ROC_FORMAT.format(GUIFileManager.SINGLETON
						.getGroupROC(modelRow)));
				sb.append(")");
				this.setText(sb.toString());
			} else {
				this.setText(ROC_FORMAT.format(GUIFileManager.SINGLETON
						.getGroupROC(modelRow, threshold)));
			}
			break;
		}

		return this;
	}
}
