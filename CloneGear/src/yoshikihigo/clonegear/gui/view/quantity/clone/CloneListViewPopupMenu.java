package yoshikihigo.clonegear.gui.view.quantity.clone;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class CloneListViewPopupMenu extends JPopupMenu {

	public CloneListViewPopupMenu(final CloneListView parentContainer) {

		JMenu exportMenu = new JMenu("export");
		JMenu csvFormatMenu = new JMenu("CSV Format");
		JMenuItem allCloneItem = new JMenuItem("all clones");

		this.add(exportMenu);
		exportMenu.add(csvFormatMenu);
		csvFormatMenu.add(allCloneItem);
		allCloneItem.addActionListener(e -> parentContainer
				.exportAllCloneCSVFormat());
	}
}
