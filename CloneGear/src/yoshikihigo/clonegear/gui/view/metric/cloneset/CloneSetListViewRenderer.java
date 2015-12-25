package yoshikihigo.clonegear.gui.view.metric.cloneset;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

class CloneSetListViewRenderer extends JLabel implements TableCellRenderer,
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

		final int modelColumn = table.convertColumnIndexToModel(column);

		switch (modelColumn) {
		case CloneSetListViewModel.COL_ID: {
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setText(value.toString());
			break;
		}
		case CloneSetListViewModel.COL_RAD: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(value.toString());
			break;
		}
		case CloneSetListViewModel.COL_LEN: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(value.toString());
			break;
		}
		case CloneSetListViewModel.COL_RNR: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(value.toString());
			break;
		}
		case CloneSetListViewModel.COL_NIF: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(value.toString());
			break;
		}
		case CloneSetListViewModel.COL_POP: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(value.toString());
			break;
		}
		case CloneSetListViewModel.COL_DFL: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(value.toString());
			break;
		}
		}

		return this;
	}
}
