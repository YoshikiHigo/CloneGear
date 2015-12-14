package yoshikihigo.clonegear.gui.view.visual.directorytree;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.data.file.IDIndexMap;
import yoshikihigo.clonegear.gui.data.file.IndexedFile;
import yoshikihigo.clonegear.gui.data.file.IndexedFileData;
import yoshikihigo.clonegear.gui.util.PathCompare;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.visual.DIRECTION;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;

public class DirectoryTreeView extends JTree implements Observer,
		VisualViewInterface {

	class TreeSelectionEventHandler implements
			javax.swing.event.TreeSelectionListener {

		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {

			final TreePath[] changedPath = e.getPaths();

			for (int i = 0; i < changedPath.length; i++) {

				final DefaultMutableTreeNode node = (DefaultMutableTreeNode) changedPath[i]
						.getLastPathComponent();
				final IndexedFile indexedFile = (IndexedFile) node
						.getUserObject();

				if (indexedFile.isFile()) {

					final GUIFile file = IDIndexMap.SINGLETON
							.getFile(indexedFile.getStartIndex());

					switch (direction) {
					case HORIZONTAL:
						if (DirectoryTreeView.this
								.isPathSelected(changedPath[i])) {
							SelectedEntities.<GUIFile> getInstance(
									HORIZONTAL_FILE).add(file,
									DirectoryTreeView.this);
						} else {
							SelectedEntities.<GUIFile> getInstance(
									HORIZONTAL_FILE).remove(file,
									DirectoryTreeView.this);
						}
						break;
					case VERTICAL:
						if (DirectoryTreeView.this
								.isPathSelected(changedPath[i])) {
							SelectedEntities.<GUIFile> getInstance(
									VERTICAL_FILE).add(file,
									DirectoryTreeView.this);
						} else {
							SelectedEntities.<GUIFile> getInstance(
									VERTICAL_FILE).remove(file,
									DirectoryTreeView.this);
						}
						break;
					}

				} else if (indexedFile.isDirectory()) {

				}
			}
		}
	}

	final private DefaultMutableTreeNode rootNode;

	final private JScrollPane scrollPane;

	final private TreeSelectionEventHandler treeSelectionEventHandler;

	final private DIRECTION direction;

	public DirectoryTreeView(final DIRECTION direction) {

		this.direction = direction;

		String rootString = PathCompare
				.getRootFilePath(GUIFileManager.SINGLETON.getFiles());
		this.rootNode = new DefaultMutableTreeNode(new IndexedFile(rootString));
		this.makeTree(rootString, GUIFileManager.SINGLETON.getFiles());

		this.assignIndex(this.rootNode, 0);

		this.setModel(new DefaultTreeModel(this.rootNode));

		DirectoryTreeViewRenderer renderer = new DirectoryTreeViewRenderer(
				direction);
		this.setCellRenderer(renderer);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.scrollPane.setBorder(new javax.swing.border.LineBorder(
				java.awt.Color.black));

		this.treeSelectionEventHandler = new TreeSelectionEventHandler();
		this.addTreeSelectionListener(this.treeSelectionEventHandler);
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	public void init() {
		this.clearSelection();
	}

	public void update(Observable o, Object arg) {

		this.removeListeners();

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedFiles = (SelectedEntities) o;

			if ((selectedFiles.getLabel().equals(HORIZONTAL_FILE) && this.direction == DIRECTION.HORIZONTAL)
					|| (selectedFiles.getLabel().equals(VERTICAL_FILE) && this.direction == DIRECTION.VERTICAL)) {

				if (!arg.equals(this)) {

					this.clearSelection();

					for (final GUIFile file : (List<GUIFile>) selectedFiles
							.get()) {
						final IndexedFile indexedFile = IndexedFileData.SINGLETON
								.get(file);
						final TreePath currentPath = this
								.getTreePath(indexedFile);

						this.addSelectionPath(currentPath);
						this.scrollPathToVisible(currentPath);
					}
				}
			}

		} else if (o instanceof RNRValue) {

			final DirectoryTreeViewRenderer directoryTreeViewRenderer = new DirectoryTreeViewRenderer(
					this.direction);
			this.setCellRenderer(directoryTreeViewRenderer);
		}

		this.addListeners();

	}

	private void addListeners() {
		this.addTreeSelectionListener(this.treeSelectionEventHandler);
	}

	private void removeListeners() {
		this.removeTreeSelectionListener(this.treeSelectionEventHandler);
	}

	private void makeTree(final String rootDirectory,
			final Collection<GUIFile> files) {

		final char separator = File.separatorChar;

		for (GUIFile file : files) {

			DefaultMutableTreeNode currentNode = this.rootNode;
			String path = file.path;

			for (int startIndex = path.indexOf(separator,
					rootDirectory.length() + 1); startIndex != -1; startIndex = path
					.indexOf(separator, startIndex + 1)) {

				String currentPath = path.substring(0, startIndex);
				IndexedFile currentDirectory = new IndexedFile(currentPath);

				for (int k = 0;; k++) {

					if (k == currentNode.getChildCount()) {
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
								currentDirectory);
						currentNode.add(newNode);
						currentNode = newNode;
						break;
					}

					DefaultMutableTreeNode searchedChild = (DefaultMutableTreeNode) currentNode
							.getChildAt(k);
					IndexedFile searchedFile = (IndexedFile) searchedChild
							.getUserObject();

					if (currentDirectory.equals(searchedFile)) {
						currentNode = searchedChild;
						break;
					}
				}
			}

			IndexedFile currentFile = IndexedFileData.SINGLETON.get(file);
			DefaultMutableTreeNode newLeaf = new DefaultMutableTreeNode(
					currentFile);
			currentNode.add(newLeaf);
		}
	}

	private int assignIndex(final DefaultMutableTreeNode node,
			final int startIndex) {

		int currentIndex = startIndex;
		IndexedFile currentIndexedFile = (IndexedFile) node.getUserObject();

		if (currentIndexedFile.isFile()) {

			currentIndexedFile.setStartIndex(currentIndex);
			currentIndexedFile.setEndIndex(currentIndex);

			node.setUserObject(currentIndexedFile);

			return ++currentIndex;

		} else if (currentIndexedFile.isDirectory()) {

			int start = currentIndex;

			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node
						.getChildAt(i);
				currentIndex = this.assignIndex(childNode, currentIndex);
			}

			int end = currentIndex - 1;

			currentIndexedFile.setStartIndex(start);
			currentIndexedFile.setEndIndex(end);

			node.setUserObject(currentIndexedFile);

			return currentIndex;

		} else {
			return -1;
		}
	}

	private TreePath getTreePath(final IndexedFile searchFile) {
		return this.getTreePath(searchFile, this.rootNode);
	}

	private TreePath getTreePath(final IndexedFile searchFile,
			final DefaultMutableTreeNode currentNode) {

		IndexedFile currentFile = (IndexedFile) currentNode.getUserObject();

		if (currentFile.isFile()) {

			if (currentFile.equals(searchFile)) {
				return new TreePath(currentNode.getPath());
			}

		} else if (currentFile.isDirectory()) {

			for (int i = 0; i < currentNode.getChildCount(); i++) {

				DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) currentNode
						.getChildAt(i);
				IndexedFile childFile = (IndexedFile) currentChild
						.getUserObject();

				int searchStartIndex = searchFile.getStartIndex();
				int searchEndIndex = searchFile.getEndIndex();
				int childStartIndex = childFile.getStartIndex();
				int childEndIndex = childFile.getEndIndex();

				if (searchStartIndex < childStartIndex)
					continue;
				else if (childEndIndex < searchEndIndex)
					continue;

				return this.getTreePath(searchFile, currentChild);
			}
		}

		return null;
	}
}
