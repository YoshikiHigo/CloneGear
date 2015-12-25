package yoshikihigo.clonegear.gui.view.quantity.filelist;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.CSVFileFilter;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectDirectory;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class FileListView extends JTable implements ViewScale, Observer,
		QuantitativeViewInterface {

	static class FileListViewPopupMenu extends JPopupMenu {

		public FileListViewPopupMenu(final FileListView parentContainer) {

			final JMenu exportMenu = new JMenu("export");
			final JMenu csvFormatMenu = new JMenu("CSV Format");
			final JMenuItem allFileItem = new JMenuItem("all files");
			final JMenuItem onlyClonedFileItem = new JMenuItem(
					"only files which include clones");

			this.add(exportMenu);
			exportMenu.add(csvFormatMenu);
			csvFormatMenu.add(allFileItem);
			csvFormatMenu.add(onlyClonedFileItem);

			allFileItem.addActionListener(e -> parentContainer
					.exportFileDataCVSFormat(false));
			onlyClonedFileItem.addActionListener(e -> parentContainer
					.exportFileDataCVSFormat(true));
		}
	}

	class SelectionEventHandler implements ListSelectionListener {

		@Override
		public void valueChanged(final ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			final int firstIndex = e.getFirstIndex();
			final int lastIndex = e.getLastIndex();

			for (int i = firstIndex; i <= lastIndex; i++) {

				final int modelIndex = FileListView.this
						.convertRowIndexToModel(i);
				final FileListViewModel model = (FileListViewModel) FileListView.this
						.getModel();
				final GUIFile file = model.getFiles().get(modelIndex);

				if (FileListView.this.getSelectionModel().isSelectedIndex(i)) {
					SelectedEntities.<GUIFile> getInstance(SELECTED_FILE).add(
							file, FileListView.this);
				} else {
					SelectedEntities.<GUIFile> getInstance(SELECTED_FILE)
							.remove(file, FileListView.this);
				}
			}
		}
	}

	class MouseEventHandler extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {

			final int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {
				FileListView.this.fileListViewPopupMenu.show(e.getComponent(),
						e.getX(), e.getY());
			}
		}
	}

	final public JScrollPane scrollPane;

	final private FileListViewPopupMenu fileListViewPopupMenu;

	private SelectionEventHandler selectionEventHandler;

	private MouseEventHandler mouseEventHandler;

	public FileListView() {

		super();

		this.fileListViewPopupMenu = new FileListViewPopupMenu(this);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				"File List"));

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		this.setTable(new ArrayList<GUIFile>());

		this.selectionEventHandler = new SelectionEventHandler();
		this.mouseEventHandler = new MouseEventHandler();

		this.addListeners();
	}

	public void init() {
	}

	@Override
	public void update(final Observable o, final Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(SELECTED_FILE)) {

			} else if (selectedEntities.getLabel().equals(RELATED_FILE)) {

			} else if (selectedEntities.getLabel().equals(GROUP)) {

				this.setTable(selectedEntities.get());
				this.repaint();
			}

		} else if (o instanceof RNRValue) {

			final RNRValue rnrValue = (RNRValue) o;
			if (rnrValue.getLabel().equals(RNR)) {
				this.repaint();
			}

		} else if (o instanceof UninterestingClonesDisplay) {

			final FileListViewRenderer renderer = new FileListViewRenderer();
			final TableColumnModel columnModel = this.getColumnModel();
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				columnModel.getColumn(i).setCellRenderer(renderer);
			}

			this.repaint();
		}

		this.addListeners();
	}

	@Override
	public String getToolTipText(final MouseEvent e) {

		final Point stopPoint = e.getPoint();
		final int stopRow = this.rowAtPoint(stopPoint);
		final int modelRow = this.convertRowIndexToModel(stopRow);

		final FileListViewModel model = (FileListViewModel) this.getModel();
		final GUIFile file = model.getFiles().get(modelRow);
		return file.path;
	}

	void exportFileDataCVSFormat(final boolean onlyClonedFile) {

		final String exportFile = this.getExportFilePath();
		if (exportFile == null) {
			return;
		}

		final List<String> lines = new ArrayList<>();
		lines.add("\"Group ID\",\"LOC\",\"NOC\",\"ROC\",\"NOF\",\"Path\"");

		final FileListViewModel model = (FileListViewModel) this.getModel();
		final List<GUIFile> files = model.getFiles();
		files.stream().forEach(
				file -> {
					final int groupID = file.groupID;
					final int line = file.loc;
					final int noc = file.getNOC();
					final double roc = file.getROC();
					final int nof = file.getNOF();
					final String path = file.path;
					if (!onlyClonedFile || (onlyClonedFile && (0 < noc))) {
						lines.add(groupID + "," + line + "," + noc + "," + roc
								+ "," + nof + ",\"" + path + "\"");
					}
				});

		try {
			Files.write(Paths.get(exportFile), lines, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private String getExportFilePath() {

		String directory = SelectDirectory.getLastDirectory();
		JFileChooser fileChooser = new JFileChooser(directory);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new CSVFileFilter());

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

	private final void setTable(final List<GUIFile> files) {

		final FileListViewModel model = new FileListViewModel(files);
		this.setModel(model);
		final RowSorter<FileListViewModel> sorter = new TableRowSorter<>(model);
		this.setRowSorter(sorter);

		FileListViewRenderer renderer = new FileListViewRenderer();

		TableColumnModel fileListViewColumnModel = this.getColumnModel();
		TableColumn[] fileListViewTableColumn = new TableColumn[model
				.getColumnCount()];
		for (int i = 0; i < fileListViewTableColumn.length; i++) {
			fileListViewTableColumn[i] = fileListViewColumnModel.getColumn(i);
			fileListViewTableColumn[i].setCellRenderer(renderer);
		}

		fileListViewTableColumn[0]
				.setMinWidth(QUANTITATIVE_FILELISTVIEW_FILENAME_COL_MIN_WIDTH);
		fileListViewTableColumn[1]
				.setMinWidth(QUANTITATIVE_FILELISTVIEW_LOC_COL_MIN_WIDTH);
		fileListViewTableColumn[1]
				.setMaxWidth(QUANTITATIVE_FILELISTVIEW_LOC_COL_MAX_WIDTH);
		fileListViewTableColumn[2]
				.setMinWidth(QUANTITATIVE_FILELISTVIEW_NOC_COL_MIN_WIDTH);
		fileListViewTableColumn[2]
				.setMaxWidth(QUANTITATIVE_FILELISTVIEW_NOC_COL_MAX_WIDTH);
		fileListViewTableColumn[3]
				.setMinWidth(QUANTITATIVE_FILELISTVIEW_ROC_COL_MIN_WIDTH);
		fileListViewTableColumn[3]
				.setMaxWidth(QUANTITATIVE_FILELISTVIEW_ROC_COL_MAX_WIDTH);
		fileListViewTableColumn[4]
				.setMinWidth(QUANTITATIVE_FILELISTVIEW_NOF_COL_MIN_WIDTH);
		fileListViewTableColumn[4]
				.setMaxWidth(QUANTITATIVE_FILELISTVIEW_NOF_COL_MAX_WIDTH);
	}
}
