package yoshikihigo.clonegear.gui.view.quantity.clone;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneLabelManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.CSVFileFilter;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class CloneListView extends JTable implements ViewScale, Observer,
		QuantitativeViewInterface {

	class SelectionEventHandler implements ListSelectionListener {

		@Override
		public void valueChanged(final ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			final int startIndex = e.getFirstIndex();
			final int endIndex = e.getLastIndex();

			final CloneListViewModel model = (CloneListViewModel) CloneListView.this
					.getModel();
			final List<GUIClone> clones = model.getClones();

			for (int i = startIndex; i <= endIndex; i++) {

				if (clones.size() <= i) {
					break;
				}

				final int modelIndex = CloneListView.this
						.convertRowIndexToModel(i);

				if (CloneListView.this.getSelectionModel().isSelectedIndex(i) == true) {

					SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT).add(
							clones.get(modelIndex), CloneListView.this);

				} else if (CloneListView.this.getSelectionModel()
						.isSelectedIndex(i) == false) {

					SelectedEntities.<GUIClone> getInstance(CODEFRAGMENT)
							.remove(clones.get(modelIndex), CloneListView.this);
				}
			}
		}
	}

	class MouseEventHandler extends MouseAdapter {

		@Override
		public void mouseClicked(final MouseEvent e) {

			int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {

				CloneListView.this.fragmentViewPopupMenu.show(e.getComponent(),
						e.getX(), e.getY());
			}
		}
	}

	final public JScrollPane scrollPane;

	private SelectionEventHandler selectionEventHandler;

	private MouseEventHandler mouseEventHandler;

	private final CloneListViewPopupMenu fragmentViewPopupMenu;

	public CloneListView() {

		super();

		this.selectionEventHandler = new SelectionEventHandler();
		this.mouseEventHandler = new MouseEventHandler();

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.fragmentViewPopupMenu = new CloneListViewPopupMenu(this);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Clone List"));

		this.addListeners();

		this.setTable(new ArrayList<GUIClone>());
	}

	@Override
	public void update(final Observable o, final Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(SELECTED_FILE)) {

				final List<GUIFile> files = selectedEntities.get();
				final List<GUIClone> codeFragments = GUICloneManager
						.getClones(files);
				this.setTable(codeFragments);

			} else if (selectedEntities.getLabel().equals(RELATED_FILE)) {

			} else if (selectedEntities.getLabel().equals(GROUP)) {

				this.setTable(new ArrayList<GUIClone>());
			}

		} else if (o instanceof RNRValue) {

			RNRValue rnrValue = (RNRValue) o;
			if (rnrValue.getLabel().equals(RNR)) {

				final CloneListViewRenderer renderer = new CloneListViewRenderer();
				final TableColumnModel columnModel = this.getColumnModel();
				for (int i = 0; i < columnModel.getColumnCount(); i++) {
					columnModel.getColumn(i).setCellRenderer(renderer);
				}

				this.repaint();
			}

		} else if (o instanceof UninterestingClonesDisplay) {

			final boolean display = ((UninterestingClonesDisplay) o)
					.isDisplay();
			final TableRowSorter<CloneListViewModel> sorter = (TableRowSorter) this
					.getRowSorter();
			if (display) {
				sorter.setRowFilter(null);
			} else {
				sorter.setRowFilter(new RowFilter<CloneListViewModel, Integer>() {
					public boolean include(
							Entry<? extends CloneListViewModel, ? extends Integer> entry) {
						final int threshold = RNRValue.getInstance(RNR).get();
						final CloneListViewModel model = entry.getModel();
						final GUIClone clone = model.getClones().get(
								entry.getIdentifier());
						final int rnr = clone.getRNR();
						return threshold <= rnr;
					}
				});
			}
		}

		this.addListeners();
	}

	private void addListeners() {
		this.getSelectionModel().addListSelectionListener(
				this.selectionEventHandler);
		this.addMouseListener(this.mouseEventHandler);
	}

	private void removeListeners() {
		this.getSelectionModel().removeListSelectionListener(
				this.selectionEventHandler);
		this.removeMouseListener(this.mouseEventHandler);
	}

	void exportAllCloneCSVFormat() {

		try {

			String exportFilePath = this.getExportFilePath();
			if (exportFilePath == null) {
				return;
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					exportFilePath));

			for (final GUIFile file : SelectedEntities.<GUIFile> getInstance(
					SELECTED_FILE).get()) {
				final String filePath = file.path;
				bw.write("File Path :" + filePath + "\n");
				bw.write("Location, Length, Despersivity, Equivalence\n");

				for (GUIClone codeFragment : file.getClones()) {

					bw.write("\""
							+ GUICloneLabelManager.SINGLETON
									.getLocationLabel(codeFragment) + "\",\""
							+ codeFragment.getLOC() + "\",\"");

					final GUICloneSet cloneSet = GUICloneManager.SINGLETON
							.getCloneSet(codeFragment);

					if (cloneSet.getRAD() == 0) {
						bw.write("\",dense,\"");
					} else if (cloneSet.getRAD() == 1) {
						bw.write("\",middle,\"");
					} else if (cloneSet.getRAD() > 1) {
						bw.write("\",scattered,\"");
					}

					bw.write(cloneSet.getPOP() + "\"\n");
				}

			}

			bw.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	void exportScatteredCloneCSVFormat() {

		try {

			String exportFilePath = this.getExportFilePath();
			if (exportFilePath == null)
				return;

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					exportFilePath));

			for (final GUIFile file : SelectedEntities.<GUIFile> getInstance(
					SELECTED_FILE).get()) {

				final String filePath = file.path;
				bw.write("File Path :" + filePath + "\n");
				bw.write("Location, Length, Despersivity, Equivalence\n");

				for (GUIClone codeFragment : file.getClones()) {

					final GUICloneSet cloneSet = GUICloneManager.SINGLETON
							.getCloneSet(codeFragment);
					if (cloneSet.getRAD() > 1) {
						bw.write("\""
								+ GUICloneLabelManager.SINGLETON
										.getLocationLabel(codeFragment)
								+ "\",\"" + codeFragment.getLOC()
								+ "\", scattered,\"" + cloneSet.getPOP() + "\n");
					}
				}
			}

			bw.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	void exportMiddleCloneCSVFormat() {

		try {

			String exportFilePath = this.getExportFilePath();
			if (exportFilePath == null)
				return;

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					exportFilePath));

			for (final GUIFile file : SelectedEntities.<GUIFile> getInstance(
					SELECTED_FILE).get()) {

				final String filePath = file.path;
				bw.write("File Path :" + filePath + "\n");
				bw.write("Location, Length, Despersivity, Equivalence\n");

				for (GUIClone codeFragment : file.getClones()) {

					final GUICloneSet cloneSet = GUICloneManager.SINGLETON
							.getCloneSet(codeFragment);
					if (cloneSet.getRAD() == 1) {
						bw.write("\""
								+ GUICloneLabelManager.SINGLETON
										.getLocationLabel(codeFragment)
								+ "\",\"" + codeFragment.getLOC()
								+ "\", middle,\"" + cloneSet.getPOP() + "\"\n");
					}
				}
			}

			bw.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	void exportDenseCloneCSVFormat() {

		try {

			String exportFilePath = this.getExportFilePath();
			if (exportFilePath == null)
				return;

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					exportFilePath));

			for (final GUIFile file : SelectedEntities.<GUIFile> getInstance(
					SELECTED_FILE).get()) {

				final String filePath = file.path;
				bw.write("File Path :" + filePath + "\n");
				bw.write("Location, Length, Despersivity, Equivalence\n");

				for (GUIClone codeFragment : file.getClones()) {

					final GUICloneSet cloneSet = GUICloneManager.SINGLETON
							.getCloneSet(codeFragment);

					if (cloneSet.getRAD() == 0) {
						bw.write("\""
								+ GUICloneLabelManager.SINGLETON
										.getLocationLabel(codeFragment)
								+ "\",\"" + codeFragment.getLOC()
								+ "\", dense,\"" + cloneSet.getPOP() + "\"\n");
					}
				}
			}

			bw.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private String getExportFilePath() {

		String home = (new File(System.getProperty("user.home")))
				.getAbsolutePath();
		JFileChooser fileChooser = new JFileChooser(home);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle("Save in CSV Format");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new CSVFileFilter());

		// show dialog
		int returnValue = fileChooser.showSaveDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {

			return fileChooser.getSelectedFile().getAbsolutePath();

		} else if (returnValue == JFileChooser.CANCEL_OPTION) {

			return null;

		} else if (returnValue == JFileChooser.ERROR_OPTION) {

			return null;

		} else {

			return null;
		}
	}

	private final void setTable(final List<GUIClone> clones) {

		final CloneListViewModel model = new CloneListViewModel(clones);
		this.setModel(model);
		final RowSorter<CloneListViewModel> sorter = new TableRowSorter<>(model);
		this.setRowSorter(sorter);

		final CloneListViewRenderer renderer = new CloneListViewRenderer();

		final TableColumnModel fragmentViewColumnModel = this.getColumnModel();
		final TableColumn[] fragmentViewTableColumn = new TableColumn[model
				.getColumnCount()];
		for (int i = 0; i < fragmentViewTableColumn.length; i++) {
			fragmentViewTableColumn[i] = fragmentViewColumnModel.getColumn(i);
			fragmentViewTableColumn[i].setCellRenderer(renderer);
		}

		fragmentViewTableColumn[0]
				.setMinWidth(QUANTITATIVE_CLONELISTVIEW_LOCATION_COL_MIN_WIDTH);
		fragmentViewTableColumn[0]
				.setMaxWidth(QUANTITATIVE_CLONELISTVIEW_LOCATION_COL_MAX_WIDTH);
		fragmentViewTableColumn[1]
				.setMinWidth(QUANTITATIVE_CLONELISTVIEW_LENGTH_COL_MIN_WIDTH);
		fragmentViewTableColumn[1]
				.setMaxWidth(QUANTITATIVE_CLONELISTVIEW_LENGTH_COL_MAX_WIDTH);
		fragmentViewTableColumn[2]
				.setMinWidth(QUANTITATIVE_CLONELISTVIEW_DISPERSIVITY_COL_MIN_WIDTH);
		fragmentViewTableColumn[2]
				.setMaxWidth(QUANTITATIVE_CLONELISTVIEW_DISPERSIVITY_COL_MAX_WIDTH);
		fragmentViewTableColumn[3]
				.setMinWidth(QUANTITATIVE_CLONELISTVIEW_EQUIVALENCE_COL_MIN_WIDTH);
		fragmentViewTableColumn[3]
				.setMaxWidth(QUANTITATIVE_CLONELISTVIEW_EQUIVALENCE_COL_MAX_WIDTH);
	}
}
