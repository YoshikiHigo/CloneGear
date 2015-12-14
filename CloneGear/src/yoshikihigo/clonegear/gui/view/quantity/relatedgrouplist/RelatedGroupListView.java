package yoshikihigo.clonegear.gui.view.quantity.relatedgrouplist;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class RelatedGroupListView extends JTable implements ViewScale,
		Observer, QuantitativeViewInterface {

	class SelectionEventHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {

			if (!e.getValueIsAdjusting()) {
			}
		}
	}

	class MouseEventHandler implements MouseListener {

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	class MouseTableHeadEventHandler implements MouseListener {

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	final private JScrollPane scrollPane;

	private RelatedGroupListViewModel relatedGroupListViewModel;

	private RelatedGroupListViewSorter relatedGroupListViewSorter;

	// private RelatedGroupListViewPopupMenu relatedGroupListViewPopupMenu;

	final private SelectionEventHandler selectionEventHandler;

	final private MouseEventHandler mouseEventHandler;

	final private MouseTableHeadEventHandler mouseTableHeadEventHandler;

	public RelatedGroupListView() {

		super();

		// initialize table
		this.init();

		// initialize pop up menu
		// this.relatedGroupListViewPopupMenu = new
		// RelatedGroupListViewPopupMenu(this);

		// set scroll pane
		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Groups sharing clones with the selected group"));

		// elements of this list is selected only 1 at same time
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.relatedGroupListViewModel = new RelatedGroupListViewModel(
				new RelatedGroup[0]);
		this.relatedGroupListViewSorter = new RelatedGroupListViewSorter(
				this.relatedGroupListViewModel);
		this.setModel(this.relatedGroupListViewSorter);

		this.relatedGroupListViewSorter.addMouseListenerToHeaderInTable(this);

		this.getTableHeader().setReorderingAllowed(false);

		// initialize listeners
		this.selectionEventHandler = new SelectionEventHandler();
		this.mouseEventHandler = new MouseEventHandler();
		this.mouseTableHeadEventHandler = new MouseTableHeadEventHandler();
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	@Override
	public void update(final Observable o, final Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(SELECTED_FILE)) {

			} else if (selectedEntities.getLabel().equals(RELATED_FILE)) {

			} else if (selectedEntities.getLabel().equals(GROUP)) {

			}

		} else if (o instanceof RNRValue) {

			final RNRValue rnrValue = (RNRValue) o;
			if (rnrValue.getLabel().equals(RNR)) {
				this.repaint();
			}
		}

		this.addListeners();
	}

	public void init() {
	}

	private void addListeners() {
		this.getSelectionModel().addListSelectionListener(
				this.selectionEventHandler);
		this.addMouseListener(this.mouseEventHandler);
		this.getTableHeader().addMouseListener(this.mouseTableHeadEventHandler);
		this.relatedGroupListViewSorter.addMouseListenerToHeaderInTable(this);
	}

	private void removeListeners() {
		this.getSelectionModel().removeListSelectionListener(
				this.selectionEventHandler);
		this.removeMouseListener(this.mouseEventHandler);
		this.getTableHeader().removeMouseListener(
				this.mouseTableHeadEventHandler);
	}
}
