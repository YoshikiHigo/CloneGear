package yoshikihigo.clonegear.gui.view.visual.directorytree;


import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.visual.DIRECTION;


class DirectoryTreeViewRenderer extends DefaultTreeCellRenderer implements ViewColors {

    final private DIRECTION direction;

    DirectoryTreeViewRenderer(final DIRECTION direction) {
        this.direction = direction;
    }

    public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        DefaultTreeCellRenderer node = (DefaultTreeCellRenderer) super
                .getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (selected) {

            switch (this.direction) {
            case HORIZONTAL:
                node.setBackgroundSelectionColor(VISUAL_HORIZONTAL_COLOR);
                break;

            case VERTICAL:
                node.setBackgroundSelectionColor(VISUAL_VERTICAL_COLOR);
                break;
            }

            node.setBackgroundNonSelectionColor(VISUAL_DIRECTORYTREE_UNSELECTED_BACKGROUND_COLOR);
            node.setTextSelectionColor(VISUAL_DIRECTORYTREE_SELECTED_FOREGROUND_COLOR);
            node.setTextNonSelectionColor(VISUAL_DIRECTORYTREE_UNSELECTED_FOREGROUND_COLOR);
        }

        return node;
    }
}
