package yoshikihigo.clonegear.gui.view.quantity.clone;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;


public class CloneListViewPopupMenu extends JPopupMenu {

    private CloneListView parentContainer;

    private JMenu exportMenu;

    private JMenu csvFormatMenu;

    private JMenuItem allCloneItem;

    private JMenuItem onlyScatteredCloneItem;

    private JMenuItem onlyMiddleCloneItem;

    private JMenuItem onlyDenseCloneItem;

    public CloneListViewPopupMenu(CloneListView parentContainer) {

        this.parentContainer = parentContainer;

        this.exportMenu = new JMenu("export");
        this.csvFormatMenu = new JMenu("CSV Format");
        this.allCloneItem = new JMenuItem("all clones");
        this.onlyScatteredCloneItem = new JMenuItem("only scattered clones");
        this.onlyMiddleCloneItem = new JMenuItem("only middle clones");
        this.onlyDenseCloneItem = new JMenuItem("only dense clones");

        this.add(this.exportMenu);
        this.exportMenu.add(this.csvFormatMenu);
        this.csvFormatMenu.add(this.allCloneItem);
        this.csvFormatMenu.add(new JSeparator());
        this.csvFormatMenu.add(this.onlyScatteredCloneItem);
        this.csvFormatMenu.add(this.onlyMiddleCloneItem);
        this.csvFormatMenu.add(this.onlyDenseCloneItem);

        this.allCloneItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CloneListViewPopupMenu.this.parentContainer.exportAllCloneCSVFormat();
            }
        });

        this.onlyScatteredCloneItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CloneListViewPopupMenu.this.parentContainer.exportScatteredCloneCSVFormat();
            }
        });

        this.onlyMiddleCloneItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CloneListViewPopupMenu.this.parentContainer.exportMiddleCloneCSVFormat();
            }
        });

        this.onlyDenseCloneItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CloneListViewPopupMenu.this.parentContainer.exportDenseCloneCSVFormat();
            }
        });

    }
}
