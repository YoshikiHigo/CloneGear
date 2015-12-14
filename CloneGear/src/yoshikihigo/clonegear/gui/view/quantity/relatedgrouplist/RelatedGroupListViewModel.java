package yoshikihigo.clonegear.gui.view.quantity.relatedgrouplist;


import javax.swing.table.AbstractTableModel;

import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;


/**
 * @author y-higo
 */
public class RelatedGroupListViewModel extends AbstractTableModel implements
        QuantitativeViewInterface {

    static final int COL_LEFT_ID = 0;

    static final int COL_LEFT_NOC = 1;

    static final int COL_LEFT_ROC = 2;

    static final int COL_RIGHT_ROC = 3;

    static final int COL_RIGHT_NOC = 4;

    static final int COL_RIGHT_ID = 5;

    static final String COL_LEFT_ID_STRING = "Group ID";

    static final String COL_LEFT_NOC_STRING = "NOC(g)";

    static final String COL_LEFT_ROC_STRING = "ROC(g)";

    static final String COL_RIGHT_ROC_STRING = "ROC(g)";

    static final String COL_RIGHT_NOC_STRING = "NOC(g)";

    static final String COL_RIGHT_ID_STRING = "Group ID";

    final private RelatedGroup[] relatedGroup;

    public RelatedGroupListViewModel(final RelatedGroup[] relatedGroup) {

        this.relatedGroup = relatedGroup;
    }

    public int getRowCount() {
        return this.relatedGroup.length;
    }

    public int getColumnCount() {
        return 6;
    }

    public Object getValueAt(int row, int col) {

        final int currentRNR = RNRValue.getInstance(RNR).get();

        if (col == COL_LEFT_ID)
            return String.valueOf(this.relatedGroup[row].getLeftGroupID());
        else if (col == COL_LEFT_NOC)
            return String.valueOf(this.relatedGroup[row].getLeftNOC(currentRNR)) + "("
                    + String.valueOf(this.relatedGroup[row].getLeftNOC()) + ")";
        else if (col == COL_LEFT_ROC)
            return String.valueOf(this.relatedGroup[row].getLeftROC(currentRNR)) + "("
                    + String.valueOf(this.relatedGroup[row].getLeftROC()) + ")";
        else if (col == COL_RIGHT_ROC)
            return String.valueOf(this.relatedGroup[row].getRightROC(currentRNR)) + "("
                    + String.valueOf(this.relatedGroup[row].getRightROC()) + ")";
        else if (col == COL_RIGHT_NOC)
            return String.valueOf(this.relatedGroup[row].getRightNOC(currentRNR)) + "("
                    + String.valueOf(this.relatedGroup[row].getRightNOC()) + ")";
        else if (col == COL_RIGHT_ID)
            return String.valueOf(this.relatedGroup[row].getRightGroupID());
        else
            return null;
    }

    /**
     * @author y-higo
     */
    public Object getSortValueAt(int row, int col) {

        if (col == COL_LEFT_ID)
            return new Integer(this.relatedGroup[row].getLeftGroupID());
        else if (col == COL_LEFT_NOC)
            return new Integer(this.relatedGroup[row].getLeftNOC());
        else if (col == COL_LEFT_ROC)
            return new Integer(this.relatedGroup[row].getLeftROC());
        else if (col == COL_RIGHT_ROC)
            return new Integer(this.relatedGroup[row].getRightROC());
        else if (col == COL_RIGHT_NOC)
            return new Integer(this.relatedGroup[row].getRightNOC());
        else if (col == COL_RIGHT_ID)
            return new Integer(this.relatedGroup[row].getRightGroupID());
        else
            return null;
    }

    /**
     * @author y-higo
     */
    public String getColumnName(int col) {

        if (col == COL_LEFT_ID)
            return COL_LEFT_ID_STRING;
        else if (col == COL_LEFT_NOC)
            return COL_LEFT_NOC_STRING;
        else if (col == COL_LEFT_ROC)
            return COL_LEFT_ROC_STRING;
        else if (col == COL_RIGHT_ROC)
            return COL_RIGHT_ROC_STRING;
        else if (col == COL_RIGHT_NOC)
            return COL_RIGHT_NOC_STRING;
        else if (col == COL_RIGHT_ID)
            return COL_RIGHT_ID_STRING;
        else
            return null;
    }

    /**
     * @author y-higo
     */
    public RelatedGroup getRelatedGroup(final int index) {
        return this.relatedGroup[index];
    }
}
