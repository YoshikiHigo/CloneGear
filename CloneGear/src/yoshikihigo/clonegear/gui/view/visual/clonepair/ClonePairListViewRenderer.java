package yoshikihigo.clonegear.gui.view.visual.clonepair;


import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneLabelManager;
import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;


/**
 * @author y-higo
 */
class ClonePairListViewRenderer extends JLabel implements TableCellRenderer, ViewColors,
        VisualViewInterface {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        setOpaque(true);

        final int modelRow = table.convertRowIndexToModel(row);
        final int modelColumn = table.convertColumnIndexToModel(column);
        final int threshold = RNRValue.getInstance(RNR).get();
        final ClonePairListViewModel model = (ClonePairListViewModel) table.getModel();
        final GUIClonePair clonePair = model.getClonePair(modelRow);

        switch (modelColumn) {
        case ClonePairListViewModel.COL_HORIZONTAL:
            setHorizontalAlignment(JLabel.CENTER);
            this.setText(GUICloneLabelManager.SINGLETON.getLocationLabel((GUIClone) value));
            break;
        case ClonePairListViewModel.COL_VERTICAL:
            setHorizontalAlignment(JLabel.CENTER);
            this.setText(GUICloneLabelManager.SINGLETON.getLocationLabel((GUIClone) value));
            break;
        }

        if (clonePair.getRNR() < threshold) {

            if (isSelected) {
                setBackground(VISUAL_CLONEPAIRLISTVIEW_SELECTION_BACKGROUND_COLOR);
                setForeground(VISUAL_CLONEPAIRLISTVIEW_FOREGROUND_COLOR);
            } else {
                setBackground(VISUAL_CLONEPAIRLISTVIEW_UNINTERESTING_BACKGROUND_COLOR);
                setForeground(VISUAL_CLONEPAIRLISTVIEW_FOREGROUND_COLOR);
            }

        } else {

            if (isSelected) {
                setBackground(VISUAL_CLONEPAIRLISTVIEW_SELECTION_BACKGROUND_COLOR);
                setForeground(VISUAL_CLONEPAIRLISTVIEW_FOREGROUND_COLOR);
            } else {
                setBackground(VISUAL_CLONEPAIRLISTVIEW_PRACTICAL_BACKGROUND_COLOR);
                setForeground(VISUAL_CLONEPAIRLISTVIEW_FOREGROUND_COLOR);
            }
        }

        return this;
    }
}
