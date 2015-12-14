package yoshikihigo.clonegear.gui.view.quantity.clone;

import java.awt.Component;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

class CloneListViewRenderer extends JLabel implements TableCellRenderer,
		ViewColors, QuantitativeViewInterface {

	static final String DENSE_STRING = "dense";

	static final String MIDDLE_STRING = "middle";

	static final String SCATTERED_STRING = "scattered";

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		setOpaque(true);

		final int threshold = RNRValue.getInstance(RNR).get();
		final int modelRow = table.convertRowIndexToModel(row);
		final int modelColumn = table.convertColumnIndexToModel(column);
		final CloneListViewModel model = (CloneListViewModel) table.getModel();
		final List<GUIClone> clones = model.getClones();
		final int rnr = clones.get(modelRow).getRNR();

		if (rnr < threshold) {

			if (isSelected) {
				setBackground(QUANTITATIVE_CODEFRAGMENTVIEW_SELECTION_COLOR);
				setForeground(QUANTITATIVE_CODEFRAGMENTVIEW_FOREGROUND_COLOR);
			} else {
				setBackground(QUANTITATIVE_CODEFRAGMENTVIEW_UNSELECTION_COLOR2);
				setForeground(QUANTITATIVE_CODEFRAGMENTVIEW_FOREGROUND_COLOR);
			}

		} else {

			if (isSelected) {
				setBackground(QUANTITATIVE_CODEFRAGMENTVIEW_SELECTION_COLOR);
				setForeground(QUANTITATIVE_CODEFRAGMENTVIEW_FOREGROUND_COLOR);
			} else {
				setBackground(QUANTITATIVE_CODEFRAGMENTVIEW_UNSELECTION_COLOR);
				setForeground(QUANTITATIVE_CODEFRAGMENTVIEW_FOREGROUND_COLOR);
			}
		}

		switch (modelColumn) {
		case CloneListViewModel.COL_LOCATION:
			setHorizontalAlignment(JLabel.CENTER);
			setText((String)value);
			break;
		case CloneListViewModel.COL_LENGTH:
			setHorizontalAlignment(JLabel.CENTER);
			setText(value.toString());
			break;
		case CloneListViewModel.COL_DISPERSIVITY:
			setHorizontalAlignment(JLabel.CENTER);
			switch (((Integer) value).intValue()) {
			case 0:
				setText(DENSE_STRING);
				break;
			case 1:
				setText(MIDDLE_STRING);
				break;
			default:
				setText(SCATTERED_STRING);
			}
			break;
		case CloneListViewModel.COL_EQUIVALENCE:
			setHorizontalAlignment(JLabel.CENTER);
			setText(value.toString());
			break;
		}

		return this;
	}
}
