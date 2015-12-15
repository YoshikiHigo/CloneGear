package yoshikihigo.clonegear.gui.view.metric.cloneset;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.util.CLSFileFilter;
import yoshikihigo.clonegear.gui.util.CSVFileFilter;
import yoshikihigo.clonegear.gui.util.SelectDirectory;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;

public class CloneSetListView extends JTable implements ViewScale, Observer,
		MetricViewInterface {

	class SelectionEventHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}
			SelectedEntities.<GUIClone> getInstance(CLONE).clear(
					CloneSetListView.this);

			final int firstIndex = e.getFirstIndex();
			final int lastIndex = e.getLastIndex();
			final CloneSetListViewModel model = (CloneSetListViewModel) CloneSetListView.this
					.getModel();

			for (int i = firstIndex; i <= lastIndex; i++) {

				if ((i < 0) || (model.getRowCount() <= i)) {
					break;
				}

				final int modelIndex = CloneSetListView.this
						.convertRowIndexToModel(i);
				if (CloneSetListView.this.isRowSelected(i)) {
					SelectedEntities.getInstance(SELECTED_CLONESET).add(
							model.getCloneSet(modelIndex),
							CloneSetListView.this);
				} else {
					SelectedEntities.getInstance(SELECTED_CLONESET).remove(
							model.getCloneSet(modelIndex),
							CloneSetListView.this);
				}
			}
		}
	}

	class MouseEventHandler extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {

			int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {

			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {

			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {

				CloneSetListView.this.cloneSetListViewPopupMenu.show(
						e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private static final int CSV_MODE = 0;
	private static final int CLS_MODE = 1;

	final private CloneSetListViewPopupMenu cloneSetListViewPopupMenu;
	final private JScrollPane scrollPane;
	final private SelectionEventHandler selectionEventHandler;
	final private MouseEventHandler mouseEventHandler;

	public CloneSetListView() {

		super();

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.scrollPane.setBorder(new TitledBorder(new LineBorder(
				java.awt.Color.black), "Clone Set List"));

		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		this.cloneSetListViewPopupMenu = new CloneSetListViewPopupMenu(this);

		this.selectionEventHandler = new SelectionEventHandler();
		this.mouseEventHandler = new MouseEventHandler();
	}

	public void init() {
	}

	public void update(Observable o, Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(FILTERED_CLONESET)) {

				SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
						.clear(this);
				SelectedEntities.<GUIClone> getInstance(CLONE).clear(this);

				final CloneSetListViewModel model = new CloneSetListViewModel(
						SelectedEntities.<GUICloneSet> getInstance(
								FILTERED_CLONESET).get());
				this.setModel(model);
				final RowSorter<CloneSetListViewModel> sorter = new TableRowSorter<>(
						model);
				this.setRowSorter(sorter);

				CloneSetListViewRenderer renderer = new CloneSetListViewRenderer();

				TableColumnModel fileListViewColumnModel = this
						.getColumnModel();
				TableColumn[] fileListViewTableColumn = new TableColumn[model
						.getColumnCount()];
				for (int i = 0; i < fileListViewTableColumn.length; i++) {
					fileListViewTableColumn[i] = fileListViewColumnModel
							.getColumn(i);
					fileListViewTableColumn[i].setCellRenderer(renderer);
				}

				fileListViewTableColumn[0]
						.setMinWidth(METRIC_CLONESETLISTVIEW_ID_COL_MIN_WIDTH);
				fileListViewTableColumn[0]
						.setMaxWidth(METRIC_CLONESETLISTVIEW_ID_COL_MAX_WIDTH);
				fileListViewTableColumn[1]
						.setMinWidth(METRIC_CLONESETLISTVIEW_RAD_COL_MIN_WIDTH);
				fileListViewTableColumn[1]
						.setMaxWidth(METRIC_CLONESETLISTVIEW_RAD_COL_MAX_WIDTH);
				fileListViewTableColumn[2]
						.setMinWidth(METRIC_CLONESETLISTVIEW_LEN_COL_MIN_WIDTH);
				fileListViewTableColumn[2]
						.setMaxWidth(METRIC_CLONESETLISTVIEW_LEN_COL_MAX_WIDTH);
				fileListViewTableColumn[3]
						.setMinWidth(METRIC_CLONESETLISTVIEW_RNR_COL_MIN_WIDTH);
				fileListViewTableColumn[3]
						.setMaxWidth(METRIC_CLONESETLISTVIEW_RNR_COL_MAX_WIDTH);
				fileListViewTableColumn[4]
						.setMinWidth(METRIC_CLONESETLISTVIEW_NIF_COL_MIN_WIDTH);
				fileListViewTableColumn[4]
						.setMaxWidth(METRIC_CLONESETLISTVIEW_NIF_COL_MAX_WIDTH);
				fileListViewTableColumn[5]
						.setMinWidth(METRIC_CLONESETLISTVIEW_POP_COL_MIN_WIDTH);
				fileListViewTableColumn[5]
						.setMaxWidth(METRIC_CLONESETLISTVIEW_POP_COL_MAX_WIDTH);
				fileListViewTableColumn[6]
						.setMinWidth(METRIC_CLONESETLISTVIEW_DFL_COL_MIN_WIDTH);
				fileListViewTableColumn[6]
						.setMaxWidth(METRIC_CLONESETLISTVIEW_DFL_COL_MAX_WIDTH);

			} else if (selectedEntities.getLabel().equals(SELECTED_CLONESET)) {
			} else if (selectedEntities.getLabel().equals(CLONE)) {
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

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	void exportAllCloneSetCSVFormat() {

		try {

			String exportFilePath = this.getExportFilePath(CSV_MODE);

			if (exportFilePath == null)
				return;

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					exportFilePath));
			bufferedWriter
					.write("\"ID\",\"RAD\",\"LEN\",\"RNR\",\"POP\",\"DFL\"\n");

			for (final GUICloneSet cloneSet : SelectedEntities
					.<GUICloneSet> getInstance(FILTERED_CLONESET).get()) {

				final int id = cloneSet.id;
				final int rad = cloneSet.getRAD();
				final int len = cloneSet.getLEN();
				final int rnr = cloneSet.getRNR();
				final int pop = cloneSet.getPOP();
				final int dfl = cloneSet.getDFL();

				bufferedWriter.write("\"" + id + "\",\"" + rad + "\",\"" + len
						+ "\",\"" + rnr + "\",\"" + pop + "\",\"" + dfl
						+ "\"\n");
			}

			bufferedWriter.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	void exportSelectedCloneSetCSVFormat() {

		try {

			String exportFilePath = this.getExportFilePath(CSV_MODE);

			if (exportFilePath == null)
				return;

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					exportFilePath));
			bufferedWriter
					.write("\"ID\",\"RAD\",\"LEN\",\"RNR\",\"POP\",\"DFL\"\n");

			for (final GUICloneSet cloneSet : SelectedEntities
					.<GUICloneSet> getInstance(SELECTED_CLONESET).get()) {

				final int id = cloneSet.id;
				final int rad = cloneSet.getRAD();
				final int len = cloneSet.getLEN();
				final int rnr = cloneSet.getRNR();
				final int pop = cloneSet.getPOP();
				final int dfl = cloneSet.getDFL();

				bufferedWriter.write("\"" + id + "\",\"" + rad + "\",\"" + len
						+ "\",\"" + rnr + "\",\"" + pop + "\",\"" + dfl
						+ "\"\n");
			}

			bufferedWriter.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private String getExportFilePath(int mode) {

		String directory = SelectDirectory.getLastDirectory();
		JFileChooser fileChooser = new JFileChooser(directory);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle("Save in CSV Format");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		if (mode == CSV_MODE) {
			fileChooser.addChoosableFileFilter(new CSVFileFilter());
		} else if (mode == CLS_MODE) {
			fileChooser.addChoosableFileFilter(new CLSFileFilter());
		}

		// show dialog
		int returnValue = fileChooser.showSaveDialog(this);

		// if save button push ...
		if (returnValue == JFileChooser.APPROVE_OPTION) {

			directory = fileChooser.getCurrentDirectory().getAbsolutePath();
			SelectDirectory.setLastDirectory(directory);

			return fileChooser.getSelectedFile().getAbsolutePath();

			// if cancel button push ...
		} else if (returnValue == JFileChooser.CANCEL_OPTION) {

			return null;

			// if error happens ...
		} else if (returnValue == JFileChooser.ERROR_OPTION) {

			return null;

		} else {
			return null;
		}
	}
}
