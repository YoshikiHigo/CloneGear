package yoshikihigo.clonegear.wizard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class FileTable extends JTable {

	public JScrollPane scrollPane;
	private List<File> files;

	public FileTable() {

		this.files = new ArrayList<>();

		this.setColumnSelectionAllowed(false);
		this.setRowSelectionAllowed(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
					final FileTablePopupMenu popup = new FileTablePopupMenu();
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	public List<File> getFilesWithSeparators() {
		return new ArrayList<File>(this.files);
	}

	public void clear() {
		this.files.clear();
		this.renew();
	}

	public void addFiles(final List<File> files) {
		this.files.addAll(files);
		this.renew();
	}

	public void addSeparators(final int[] indexes) {
		Arrays.sort(indexes);
		for (int i = 0; i < indexes.length; i++) {
			this.files.add(indexes[i] + i, new File("-ns"));
		}
		this.renew();
	}

	public void addSeparatorsToEveryDirectory() {
		final List<Integer> list = new ArrayList<>();
		File prevFile = new File("-ns");
		for (int i = 0; i < this.files.size(); i++) {
			final File file = this.files.get(i);
			if (prevFile.getName().equals("-ns")
					|| file.getName().equals("-ns")
					|| prevFile.getParent().equals(file.getParent())) {
			}

			else {
				list.add(i);
			}

			prevFile = file;
		}

		final int[] indexes = new int[list.size()];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = list.get(i);
		}

		this.addSeparators(indexes);
	}

	public void removeFiles(final int[] indexes) {
		Arrays.sort(indexes);
		for (int i = 0; i < indexes.length; i++) {
			this.files.remove(indexes[i] - i);
		}
		this.renew();
	}

	public void removeAllSeparators() {
		final Iterator<File> iterator = this.files.iterator();
		while (iterator.hasNext()) {
			final File file = iterator.next();
			if (file.getAbsolutePath().isEmpty()) {
				iterator.remove();
			}
		}
		this.renew();
	}

	public void renew() {
		this.getSelectionModel().clearSelection();
		final FileTableModel model = new FileTableModel(this.files);
		this.setModel(model);

		final TableColumnModel columnModel = this.getColumnModel();
		final TableColumn[] columns = new TableColumn[model.getColumnCount()];
		columns[0] = columnModel.getColumn(0);
		columns[1] = columnModel.getColumn(1);
		columns[0].setMinWidth(50);
		columns[0].setMaxWidth(70);
		columns[0].setPreferredWidth(60);
		columns[1].setMinWidth(300);
	}

	public void exportFiles() {
		final JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this)) {
			final List<String> files = this.files.stream()
					.filter(file -> !file.getName().equals("-ns"))
					.map(file -> file.getAbsolutePath())
					.collect(Collectors.toList());
			try {
				Files.write(
						Paths.get(chooser.getSelectedFile().getAbsolutePath()),
						files, StandardCharsets.ISO_8859_1);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void exportFilesAndSeparators() {
		final JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this)) {
			try (final BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(
							chooser.getSelectedFile()),
							StandardCharsets.ISO_8859_1))) {
				for (final File file : this.files) {
					if (!file.getName().equals("-ns")) {
						writer.write(file.getAbsolutePath());
					}
					writer.newLine();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	static class FileTableModel extends AbstractTableModel {

		final String[] titles;
		final private List<File> files;

		public FileTableModel(List<File> files) {
			this.titles = new String[] { "ID", "File" };
			this.files = new ArrayList<>(files);
		}

		@Override
		public int getRowCount() {
			return this.files.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			switch (col) {
			case 0:
				return Integer.toString(row);
			case 1:
				final File file = this.files.get(row);
				if (file.getName().equals("-ns")) {
					return "---------- GROUP SEPARATOR ----------";
				} else {
					return file.getAbsolutePath();
				}
			}
			assert false : "here should not be reached.";
			return "";
		}

		@Override
		public String getColumnName(int col) {
			return this.titles[col];
		}
	}

	class FileTablePopupMenu extends JPopupMenu {

		FileTablePopupMenu() {

			super();

			final JMenu removeMenu = new JMenu("remove");
			final JMenuItem removeAllItemMenuItem = new JMenuItem("all items");
			final JMenuItem removeAllSeparatorMenuItem = new JMenuItem(
					"all separators");
			final JMenuItem removeSelectedItemMenuItem = new JMenuItem(
					"selected items");
			final JMenu addMenu = new JMenu("add");
			final JMenu addSeparatorMenu = new JMenu("separator");
			final JMenuItem addSeparatorSelectedItemMenuItem = new JMenuItem(
					"selected item");
			final JMenuItem addSeparatorEveryDirectoryMenuItem = new JMenuItem(
					"every directory");
			final JMenu exportMenu = new JMenu("export");
			final JMenuItem exportFileMenuItem = new JMenuItem("files");
			final JMenuItem exportAllMenuItem = new JMenuItem(
					"files with separators");

			this.add(removeMenu);
			removeMenu.add(removeAllItemMenuItem);
			removeMenu.add(removeAllSeparatorMenuItem);
			removeMenu.add(removeSelectedItemMenuItem);
			this.add(addMenu);
			addMenu.add(addSeparatorMenu);
			addSeparatorMenu.add(addSeparatorSelectedItemMenuItem);
			addSeparatorMenu.add(addSeparatorEveryDirectoryMenuItem);
			this.add(exportMenu);
			exportMenu.add(exportFileMenuItem);
			exportMenu.add(exportAllMenuItem);

			removeAllItemMenuItem.addActionListener(e -> {
				FileTable.this.clear();
				FileTable.this.renew();
			});

			removeAllSeparatorMenuItem.addActionListener(e -> {
				FileTable.this.removeAllSeparators();
				FileTable.this.renew();
			});

			removeSelectedItemMenuItem.addActionListener(e -> {
				final int[] indexes = FileTable.this.getSelectedRows();
				FileTable.this.removeFiles(indexes);
				FileTable.this.renew();
			});

			addSeparatorSelectedItemMenuItem.addActionListener(e -> {
				final int[] indexes = FileTable.this.getSelectedRows();
				FileTable.this.addSeparators(indexes);
				FileTable.this.renew();
			});

			addSeparatorEveryDirectoryMenuItem.addActionListener(e -> {
				FileTable.this.addSeparatorsToEveryDirectory();
				FileTable.this.renew();
			});

			exportFileMenuItem.addActionListener(e -> {
				FileTable.this.exportFiles();
			});

			exportAllMenuItem.addActionListener(e -> {
				FileTable.this.exportFilesAndSeparators();
			});
		}
	}
}
