package yoshikihigo.clonegear.gui.view.metric.scatterplot;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import yoshikihigo.clonegear.gui.MessagePrinter;
import yoshikihigo.clonegear.gui.data.clone.CloneFirstPositionOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneIDOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneLastPositionOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneMiddlePositionOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneNIFOffsetData;
import yoshikihigo.clonegear.gui.data.clone.CloneRangeOffsetData;
import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.data.file.FileOffsetData;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.data.file.IDIndexMap;
import yoshikihigo.clonegear.gui.util.JPanelLightBuffered;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.ScaleValue;
import yoshikihigo.clonegear.gui.util.SelectDirectory;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;

class ScatterPlotPanel extends JPanelLightBuffered implements ViewColors,
		Observer, MetricViewInterface {

	public ScatterPlotPanel(final FileNameDisplayPanel fileNameDisplayPanel) {

		this.startIndex = 0;
		this.endIndex = GUIFileManager.SINGLETON.getFileCount() - 1;

		this.fileNameDisplayPanel = fileNameDisplayPanel;

		this.coordinate = new Coordinate();

		this.scatterPlotPopupMenu = new ScatterPlotPopupMenu(this);

		this.mouseEventHandler = new MouseEventHandler();
		this.mouseMotionEventHandler = new MouseMotionEventHandler();
		this.addMouseListener(this.mouseEventHandler);
		this.addMouseMotionListener(this.mouseMotionEventHandler);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		final boolean isGroupSeparetorDraw = this.scatterPlotPopupMenu
				.getStateGroupSeparetor();
		final boolean isFileSeparetorDraw = this.scatterPlotPopupMenu
				.getStateFileSeparetor();
		final boolean isCoorinateLineDraw = this.scatterPlotPopupMenu
				.getStateCoordinateLine();

		this.drawBackGround(g);
		if (isFileSeparetorDraw) {
			this.drawFileSeparators(g);
		}
		if (isGroupSeparetorDraw) {
			this.drawGroupSeparators(g);
		}
		this.drawCodeClones(g);
		if (isCoorinateLineDraw) {
			this.drawCoordinateLine(g);
		}
		this.drawSelectedArea(g);
		this.drawFrame(g);

		this.scrollPane.doLayout();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(this.getFrameWidth() + 1,
				this.getFrameHeight() + 1);
	}

	@Override
	public void update(Observable o, Object arg) {
		this.repaint();
	}

	void reset() {
		this.repaint();
	}

	void outputScatterPlotImage() {

		String directory = SelectDirectory.getLastDirectory();

		final JFileChooser fileChooser = new JFileChooser(directory);
		fileChooser.setAcceptAllFileFilterUsed(false);

		ScatterPlotFileFilter scatterPlotFileFilter = new ScatterPlotFileFilter(
				new String[] { "jpg", "png" }, "JPG or PNG Image");
		fileChooser.addChoosableFileFilter(scatterPlotFileFilter);

		fileChooser.setDialogTitle("Output Scatter Plot Image");
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

		switch (fileChooser.showSaveDialog(this)) {

		case JFileChooser.APPROVE_OPTION:

			try {

				final int width = this.getFrameWidth();
				final int height = this.getFrameHeight();

				final BufferedImage bufferedImage = new BufferedImage(width,
						height, BufferedImage.TYPE_INT_RGB);
				final Graphics graphics = bufferedImage.getGraphics();

				this.paint(graphics);

				final File outFile = fileChooser.getSelectedFile();
				final String extension = scatterPlotFileFilter
						.getExtension(outFile);

				if (scatterPlotFileFilter.isRegisteredExtension(extension) == true) {
					ImageIO.write(bufferedImage, extension, outFile);

					directory = fileChooser.getCurrentDirectory()
							.getAbsolutePath();
					SelectDirectory.setLastDirectory(directory);
				} else {
					JOptionPane.showMessageDialog(this,
							"Unsupported File Format", "Gemini",
							JOptionPane.WARNING_MESSAGE);
				}

			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

			break;

		case JFileChooser.CANCEL_OPTION:
			break;

		case JFileChooser.ERROR_OPTION:
			break;

		default:
			break;
		}
	}

	private final void drawBackGround(Graphics g) {

		final int panelWidth = this.getFrameWidth();
		final int panelHeight = this.getFrameHeight();

		g.setColor(SCATTERPLOT_BACKGROUND_COLOR);
		g.fillRect(0, 0, panelWidth, panelHeight);

	}

	private final void drawFrame(Graphics g) {

		final int panelWidth = this.getFrameWidth();
		final int panelHeight = this.getFrameHeight();

		g.setColor(SCATTERPLOT_FRAME_COLOR);
		g.drawRect(0, 0, panelWidth, panelHeight);
		g.drawRect(1, 1, panelWidth - 2, panelHeight - 2);
		g.drawRect(2, 2, panelWidth - 4, panelHeight - 4);
	}

	private final void drawGroupSeparators(Graphics g) {

		final double xScaleRatio = this.getXScaleRatio();
		final int panelHeight = this.getFrameHeight();

		final int groupNumber = GUIFileManager.SINGLETON.getGroupCount();

		final GUIFile startFile = IDIndexMap.SINGLETON.getFile(this.startIndex);
		final int startFileOffset = FileOffsetData.SINGLETON.get(startFile);

		for (int groupIndex = 0; groupIndex < groupNumber; groupIndex++) {

			final GUIFile lastFile = GUIFileManager.SINGLETON
					.getLastFile(groupIndex);
			final int fileIndex = IDIndexMap.SINGLETON.getIndex(
					lastFile.groupID, lastFile.fileID);
			if ((this.startIndex <= fileIndex) && (fileIndex < this.endIndex)) {

				final int lastFileOffset = FileOffsetData.SINGLETON
						.get(lastFile);
				final double lastFileEndPosition = (lastFileOffset
						+ lastFile.loc - startFileOffset)
						/ xScaleRatio;

				g.setColor(SCATTERPLOT_GROUP_SEPARATOR_COLOR);
				g.drawLine((int) lastFileEndPosition, 0,
						(int) lastFileEndPosition, panelHeight);
			}
		}
	}

	private final void drawFileSeparators(Graphics g) {

		final double xScaleRatio = this.getXScaleRatio();
		final int panelHeight = this.getFrameHeight();
		final int fileNumber = GUIFileManager.SINGLETON.getFileCount();

		final GUIFile startFile = IDIndexMap.SINGLETON.getFile(this.startIndex);
		final int startFileOffset = FileOffsetData.SINGLETON.get(startFile);

		for (int index = 0; index < fileNumber; index++) {

			if ((this.startIndex <= index) && (index <= this.endIndex)) {

				final GUIFile currentFile = IDIndexMap.SINGLETON.getFile(index);
				final int currentFileOffset = FileOffsetData.SINGLETON
						.get(currentFile);
				final double currentFileEndPosition = (currentFileOffset
						+ currentFile.loc - startFileOffset)
						/ xScaleRatio;

				g.setColor(SCATTERPLOT_FILE_SEPARATOR_COLOR);
				g.drawLine((int) currentFileEndPosition, 0,
						(int) currentFileEndPosition, panelHeight);
			}
		}
	}

	private final void drawCodeClones(Graphics g) {

		final int rnrThreshold = RNRValue.getInstance(RNR).get();
		final boolean uninterestingClonesDisplay = UninterestingClonesDisplay
				.getInstance(UNINTERESTING).isDisplay();

		final double xScaleRatio = this.getXScaleRatio();
		final double yScaleRatio = this.getYScaleRatio();
		for (final GUICloneSet cloneSet : GUICloneManager.SINGLETON
				.getCloneSets()) {

			final int index;
			if (this.scatterPlotPopupMenu.getStateIDSort()) {
				index = CloneIDOffsetData.SINGLETON.get(cloneSet);
			} else if (this.scatterPlotPopupMenu.getStateFirstPositionSort()) {
				index = CloneFirstPositionOffsetData.SINGLETON.get(cloneSet);
			} else if (this.scatterPlotPopupMenu.getStateLastPositionSort()) {
				index = CloneLastPositionOffsetData.SINGLETON.get(cloneSet);
			} else if (this.scatterPlotPopupMenu.getStateMiddlePositionSort()) {
				index = CloneMiddlePositionOffsetData.SINGLETON.get(cloneSet);
			} else if (this.scatterPlotPopupMenu.getStateRangeSort()) {
				index = CloneRangeOffsetData.SINGLETON.get(cloneSet);
			} else if (this.scatterPlotPopupMenu.getStateNIFSort()) {
				index = CloneNIFOffsetData.SINGLETON.get(cloneSet);
			} else {
				index = 0;
				assert false : "Here shouldn't be reached!";
			}
			final double cloneSetOffset = index / yScaleRatio;
			final double cloneSetHeight = 1 / yScaleRatio;

			{
				for (final GUIClone codeFragment : cloneSet.getClones()) {
					final GUIFile ownerFile = codeFragment.file;
					final int ownerFileOffset = FileOffsetData.SINGLETON
							.get(ownerFile);
					final double codeFragmentStartOffset = (ownerFileOffset + codeFragment.fromLine)
							/ xScaleRatio;
					final double codeFragmentWidth = codeFragment.getLOC()
							/ xScaleRatio;

					if (rnrThreshold <= cloneSet.getRNR()) {
						g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
						g.fillRect((int) Math.ceil(codeFragmentStartOffset),
								(int) Math.ceil(cloneSetOffset),
								(int) Math.ceil(codeFragmentWidth),
								(int) Math.ceil(cloneSetHeight));
					} else {

						if (uninterestingClonesDisplay) {
							g.setColor(SCATTERPLOT_FILTEROUT_CLONEPAIR_COLOR);
							g.fillRect(
									(int) Math.ceil(codeFragmentStartOffset),
									(int) Math.ceil(cloneSetOffset),
									(int) Math.ceil(codeFragmentWidth),
									(int) Math.ceil(cloneSetHeight));
						}
					}
				}
			}
		}
	}

	private final void drawCoordinateLine(Graphics g) {

		final int x = this.coordinate.getX();
		final int y = this.coordinate.getY();

		final int width = this.getFrameWidth();
		final int height = this.getFrameHeight();

		g.setColor(SCATTERPLOT_FILTEROUT_CLONEPAIR_COLOR);
		g.drawLine(0, y, width, y);
		g.drawLine(x, 0, x, height);
	}

	private final void drawSelectedArea(Graphics g) {

		if (null != this.selectedRows) {
			g.setColor(SCATTERPLOT_SELECTED_AREA_COLOR);
			final int width = this.getFrameWidth();
			int startY = this.selectedRows.getStartRow();
			int endY = this.selectedRows.getEndRow();
			g.fillRect(0, startY, width, endY - startY);
		}
	}

	private final int getFrameWidth() {

		final boolean isSquareFormat = this.scatterPlotPopupMenu
				.getStateSquare();
		final boolean isRectangleFormat = this.scatterPlotPopupMenu
				.getStateRectangle();

		final int scale = ScaleValue.getInstance(SCALE).get();

		if (isSquareFormat) {

			final int height = this.scrollPane.getHeight();
			final int width = this.scrollPane.getWidth();

			if (height < width) {
				return (height - 5) * scale;
			} else {
				return (width - 5) * scale;
			}

		} else if (isRectangleFormat) {

			final int width = this.getWidth();
			return (width - 5) * scale;
		}

		assert false : "Here shouldn't be reached!";
		return 0;
	}

	private final int getFrameHeight() {

		final boolean isSquareFormat = this.scatterPlotPopupMenu
				.getStateSquare();
		final boolean isRectangleFormat = this.scatterPlotPopupMenu
				.getStateRectangle();

		final int scale = ScaleValue.getInstance(SCALE).get();

		if (isSquareFormat) {

			final int height = this.scrollPane.getHeight();
			final int width = this.scrollPane.getWidth();

			if (height < width) {
				return (height - 5) * scale;
			} else {
				return (width - 5) * scale;
			}

		} else if (isRectangleFormat) {

			final int height = GUICloneManager.SINGLETON.getCloneSetCount();
			return height * scale;
		}

		assert false : "Here shouldn't be reached!";
		return 0;
	}

	private final double getXScaleRatio() {

		final GUIFile startFile = IDIndexMap.SINGLETON.getFile(this.startIndex);
		final GUIFile endFile = IDIndexMap.SINGLETON.getFile(this.endIndex);
		final int startToken = FileOffsetData.SINGLETON.get(startFile);
		final int endToken = FileOffsetData.SINGLETON.get(endFile)
				+ endFile.loc;
		final int displayToken = endToken - startToken;

		return (double) displayToken / (double) this.getFrameWidth();
	}

	private final double getYScaleRatio() {

		final int number = GUICloneManager.SINGLETON.getCloneSetCount();
		return (double) number / (double) this.getFrameHeight();
	}

	synchronized void sleep(long time) {

		try {
			this.wait(time);
		} catch (InterruptedException e) {
			MessagePrinter.ERR.println("Interrupt time was wrong :");
		}
	}

	final private ScatterPlotPopupMenu scatterPlotPopupMenu;

	final private int startIndex;

	final private int endIndex;

	final private FileNameDisplayPanel fileNameDisplayPanel;

	final private Coordinate coordinate;

	final public JScrollPane scrollPane;

	final private MouseEventHandler mouseEventHandler;

	final private MouseMotionEventHandler mouseMotionEventHandler;

	private SelectedRows selectedRows;

	class MouseEventHandler implements MouseListener {

		public void mousePressed(MouseEvent e) {

			final int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {

				SelectedEntities.<GUIClone> getInstance(CLONE).clear(
						ScatterPlotPanel.this);
				SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
						.clear(ScatterPlotPanel.this);

				ScatterPlotPanel.this.selectedRows = new SelectedRows(e.getY());

			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {
			}

		}

		public void mouseReleased(MouseEvent e) {

			final int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {

				SelectedEntities.<GUIClone> getInstance(CLONE).clear(
						ScatterPlotPanel.this);
				SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
						.clear(ScatterPlotPanel.this);
				SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
						.clear(ScatterPlotPanel.this);

				final int startY = ScatterPlotPanel.this.selectedRows
						.getStartRow();
				final int endY = ScatterPlotPanel.this.selectedRows.getEndRow();

				for (final GUICloneSet cloneSet : GUICloneManager.SINGLETON
						.getCloneSets()) {

					final int index;
					if (ScatterPlotPanel.this.scatterPlotPopupMenu
							.getStateIDSort()) {
						index = CloneIDOffsetData.SINGLETON.get(cloneSet);
					} else if (ScatterPlotPanel.this.scatterPlotPopupMenu
							.getStateFirstPositionSort()) {
						index = CloneFirstPositionOffsetData.SINGLETON
								.get(cloneSet);
					} else if (ScatterPlotPanel.this.scatterPlotPopupMenu
							.getStateLastPositionSort()) {
						index = CloneLastPositionOffsetData.SINGLETON
								.get(cloneSet);
					} else if (ScatterPlotPanel.this.scatterPlotPopupMenu
							.getStateMiddlePositionSort()) {
						index = CloneMiddlePositionOffsetData.SINGLETON
								.get(cloneSet);
					} else if (ScatterPlotPanel.this.scatterPlotPopupMenu
							.getStateRangeSort()) {
						index = CloneRangeOffsetData.SINGLETON.get(cloneSet);
					} else if (ScatterPlotPanel.this.scatterPlotPopupMenu
							.getStateNIFSort()) {
						index = CloneNIFOffsetData.SINGLETON.get(cloneSet);
					} else {
						index = 0;
						assert false : "Here shouldn't be reached!";
					}

					if ((startY <= index) && (index <= endY)) {
						SelectedEntities.<GUICloneSet> getInstance(
								FILTERED_CLONESET).add(cloneSet,
								ScatterPlotPanel.this);
					}
				}
				ScatterPlotPanel.this.selectedRows = null;

			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {
			}
		}

		public void mouseClicked(MouseEvent e) {

			final int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {

				ScatterPlotPanel.this.scatterPlotPopupMenu.show(
						e.getComponent(), e.getX(), e.getY());
			}
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}
	}

	class MouseMotionEventHandler implements MouseMotionListener {

		public void mouseDragged(MouseEvent e) {

			final int width = ScatterPlotPanel.this.getFrameWidth();
			final int height = ScatterPlotPanel.this.getFrameHeight();
			if ((width <= e.getX()) || (height <= e.getY())) {
				return;
			}

			{
				final GUIFile startFile = IDIndexMap.SINGLETON
						.getFile(ScatterPlotPanel.this.startIndex);
				final int startFileOffset = FileOffsetData.SINGLETON
						.get(startFile);

				final double xScaleRatio = getXScaleRatio();
				final double yScaleRatio = getYScaleRatio();

				final double x = e.getX() * xScaleRatio + startFileOffset;
				final double y = e.getY() * yScaleRatio;

				final int fileNumber = GUIFileManager.SINGLETON.getFileCount();
				final int cloneSetNumber = GUICloneManager.SINGLETON
						.getCloneSetCount();
				final int low = 0;
				final int high = fileNumber - 1;
				final int horizontalElementIndex = IDIndexMap.SINGLETON
						.searchFileIndex((int) Math.rint(x), low, high);
				final int verticalElementIndex = (int) y;

				if ((0 <= horizontalElementIndex)
						&& (horizontalElementIndex < fileNumber)
						&& (0 <= verticalElementIndex)
						&& (verticalElementIndex < cloneSetNumber)) {

					final GUIFile horizontalElement = IDIndexMap.SINGLETON
							.getFile(horizontalElementIndex);
					final StringBuilder horizontalString = new StringBuilder();
					horizontalString.append(horizontalElement.path);
					horizontalString.append("(");
					horizontalString.append(horizontalElement.groupID);
					horizontalString.append(".");
					horizontalString.append(horizontalElement.fileID);
					horizontalString.append(")");

					final GUICloneSet verticalElement = GUICloneManager.SINGLETON
							.getCloneSet(verticalElementIndex);
					final StringBuilder verticalString = new StringBuilder();
					verticalString.append(verticalElementIndex);
					verticalString.append(" (DFL:");
					verticalString.append(verticalElement.getDFL());
					verticalString.append(" ,LEN:");
					verticalString.append(verticalElement.getLEN());
					verticalString.append(" ,NIF:");
					verticalString.append(verticalElement.getNIF());
					verticalString.append(" ,POP:");
					verticalString.append(verticalElement.getPOP());
					verticalString.append(" ,RAD:");
					verticalString.append(verticalElement.getRAD());
					verticalString.append(" ,RNR:");
					verticalString.append(verticalElement.getRNR());
					verticalString.append(")");

					ScatterPlotPanel.this.fileNameDisplayPanel.update(
							horizontalString.toString(),
							verticalString.toString());

				} else {
					ScatterPlotPanel.this.fileNameDisplayPanel.update("", "");
				}
			}

			{
				ScatterPlotPanel.this.selectedRows.setEndRow(e.getY());
			}

			ScatterPlotPanel.this.repaint();
		}

		public void mouseMoved(MouseEvent e) {

			final int width = ScatterPlotPanel.this.getFrameWidth();
			final int height = ScatterPlotPanel.this.getFrameHeight();
			if ((width <= e.getX()) || (height <= e.getY())) {
				return;
			}

			final GUIFile startFile = IDIndexMap.SINGLETON
					.getFile(ScatterPlotPanel.this.startIndex);
			final int startFileOffset = FileOffsetData.SINGLETON.get(startFile);

			final double xScaleRatio = getXScaleRatio();
			final double yScaleRatio = getYScaleRatio();

			final double x = e.getX() * xScaleRatio + startFileOffset;
			final double y = e.getY() * yScaleRatio;

			final int fileNumber = GUIFileManager.SINGLETON.getFileCount();
			final int cloneSetNumber = GUICloneManager.SINGLETON
					.getCloneSetCount();
			final int low = 0;
			final int high = fileNumber - 1;
			final int horizontalElementIndex = IDIndexMap.SINGLETON
					.searchFileIndex((int) Math.rint(x), low, high);
			final int verticalElementIndex = (int) y;

			if ((0 <= horizontalElementIndex)
					&& (horizontalElementIndex < fileNumber)
					&& (0 <= verticalElementIndex)
					&& (verticalElementIndex < cloneSetNumber)) {

				final GUIFile horizontalElement = IDIndexMap.SINGLETON
						.getFile(horizontalElementIndex);
				final StringBuilder horizontalString = new StringBuilder();
				horizontalString.append(horizontalElement.path);
				horizontalString.append("(");
				horizontalString.append(horizontalElement.groupID);
				horizontalString.append(".");
				horizontalString.append(horizontalElement.fileID);
				horizontalString.append(")");

				final GUICloneSet verticalElement = GUICloneManager.SINGLETON
						.getCloneSet(verticalElementIndex);
				final StringBuilder verticalString = new StringBuilder();
				verticalString.append(verticalElementIndex);
				verticalString.append(" (DFL:");
				verticalString.append(verticalElement.getDFL());
				verticalString.append(" ,LEN:");
				verticalString.append(verticalElement.getLEN());
				verticalString.append(" ,NIF:");
				verticalString.append(verticalElement.getNIF());
				verticalString.append(" ,POP:");
				verticalString.append(verticalElement.getPOP());
				verticalString.append(" ,RAD:");
				verticalString.append(verticalElement.getRAD());
				verticalString.append(" ,RNR:");
				verticalString.append(verticalElement.getRNR());
				verticalString.append(")");

				ScatterPlotPanel.this.fileNameDisplayPanel.update(
						horizontalString.toString(), verticalString.toString());

				ScatterPlotPanel.this.coordinate.set(e.getX(), e.getY());
				ScatterPlotPanel.this.repaint();

			} else {
				ScatterPlotPanel.this.fileNameDisplayPanel.update("", "");
			}

		}
	}

	static private class Coordinate {

		Coordinate() {

			this.x = 0;
			this.y = 0;
		}

		void set(final int x, final int y) {
			this.x = x;
			this.y = y;
		}

		int getX() {
			return this.x;
		}

		int getY() {
			return this.y;
		}

		int x;

		int y;
	}

	static private class SelectedRows {

		SelectedRows(final int pressedRow) {

			this.startRow = pressedRow;
			this.endRow = pressedRow;
		}

		int startRow;

		int endRow;

		int getStartRow() {
			return this.startRow < this.endRow ? this.startRow : this.endRow;
		}

		int getEndRow() {
			return this.startRow < this.endRow ? this.endRow : this.startRow;
		}

		void setEndRow(final int endRow) {
			this.endRow = endRow;
		}
	}
}
