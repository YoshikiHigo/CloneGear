package yoshikihigo.clonegear.gui.view.quantity.relatedfilelist;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class RelatedFileListViewPopupMenu extends JPopupMenu {

    private JMenuItem setAsSelectedFile;

    public RelatedFileListViewPopupMenu(final RelatedFileListView parentContainer) {

        this.setAsSelectedFile = new JMenuItem("Set as the Selected File");

        this.add(this.setAsSelectedFile);
        this.setAsSelectedFile.setEnabled(false);

        this.setAsSelectedFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                parentContainer.setAsSelectedFile();
            }
        });
    }

    public void setSelectable(final boolean b) {
        this.setAsSelectedFile.setEnabled(b);
    }
}
