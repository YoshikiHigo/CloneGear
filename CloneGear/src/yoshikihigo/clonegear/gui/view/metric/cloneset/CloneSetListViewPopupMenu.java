package yoshikihigo.clonegear.gui.view.metric.cloneset;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class CloneSetListViewPopupMenu extends javax.swing.JPopupMenu {

	final private CloneSetListView parentContainer;

	private JMenu exportMenu;
	private JMenu csvFormatMenu;

	private JMenuItem cvsAllCloneSetItem;
	private JMenuItem cvsOnlySelectedCloneSetItem;

	public CloneSetListViewPopupMenu(final CloneSetListView parentContainer) {

		this.parentContainer = parentContainer;

		this.exportMenu = new JMenu("export");
		this.add(this.exportMenu);

		this.csvFormatMenu = new JMenu("CSV Format");
		this.cvsAllCloneSetItem = new JMenuItem("all clone set");
		this.cvsOnlySelectedCloneSetItem = new JMenuItem(
				"only selected clone sets");

		this.exportMenu.add(this.csvFormatMenu);
		this.csvFormatMenu.add(this.cvsAllCloneSetItem);
		this.csvFormatMenu.add(this.cvsOnlySelectedCloneSetItem);

		this.cvsAllCloneSetItem.addActionListener(e -> {
			CloneSetListViewPopupMenu.this.parentContainer
					.exportAllCloneSetCSVFormat();
		});

		this.cvsOnlySelectedCloneSetItem.addActionListener(e -> {
			CloneSetListViewPopupMenu.this.parentContainer
					.exportSelectedCloneSetCSVFormat();
		});
	}
}
