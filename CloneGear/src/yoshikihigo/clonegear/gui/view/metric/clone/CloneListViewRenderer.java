package yoshikihigo.clonegear.gui.view.metric.clone;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneLabelManager;
import yoshikihigo.clonegear.gui.view.ViewColors;

class CloneListViewRenderer extends JLabel implements TableCellRenderer {

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

		final int modelColumn = table.convertColumnIndexToModel(column);

		switch (modelColumn) {
		case CloneListViewModel.COL_ID: {
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setText(GUICloneLabelManager.SINGLETON
					.getIDLabel((GUIClone) value));
			break;
		}
		case CloneListViewModel.COL_LOCATION: {
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setText(GUICloneLabelManager.SINGLETON
					.getLocationLabel((GUIClone) value));
			break;
		}
		case CloneListViewModel.COL_LENGTH: {
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setText(value.toString());
			break;
		}
		}

		return this;
	}
}
