package yoshikihigo.clonegear.gui.view.quantity.relatedgrouplist;


import java.awt.Component;
import java.lang.Object;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 * @author y-higo
 */
class RelatedGroupListViewRenderer extends JLabel implements TableCellRenderer,
        RelatedGroupListViewColor {

    final private RelatedGroupListViewSorter relatedGroupListViewSorter;

    /**
     * @author y-higo
     */
    public RelatedGroupListViewRenderer(final RelatedGroupListViewSorter relatedGroupListViewSorter) {
        this.relatedGroupListViewSorter = relatedGroupListViewSorter;
    }

    /**
     * @author y-higo
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        setOpaque(true);

        if (column == 0)
            setHorizontalAlignment(JLabel.CENTER);
        else if (column == 1)
            setHorizontalAlignment(JLabel.CENTER);
        else if (column == 2)
            setHorizontalAlignment(JLabel.CENTER);
        else if (column == 3)
            setHorizontalAlignment(JLabel.CENTER);
        else if (column == 4)
            setHorizontalAlignment(JLabel.CENTER);
        else if (column == 5)
            setHorizontalAlignment(JLabel.CENTER);

        if (isSelected) {
            setBackground(TABLE_SELECTION_COLOR);
            setForeground(TABLE_FOREGROUND_COLOR);
        } else {

            if (column <= 2) {
                setBackground(TABLE_LEFT_UNSELECTION_COLOR);
                setForeground(TABLE_FOREGROUND_COLOR);
            } else if (3 <= column) {
                setBackground(TABLE_RIGHT_UNSELECTION_COLOR);
                setForeground(TABLE_FOREGROUND_COLOR);
            }
        }

        setText(value.toString());

        return this;
    }
}
