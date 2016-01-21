package yoshikihigo.clonegear.gui.view.visual.scatterplot;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import yoshikihigo.clonegear.gui.MessagePrinter;
import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager.ClonePairData.FilePair;
import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.data.file.FileOffsetData;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.data.file.IDIndexMap;
import yoshikihigo.clonegear.gui.util.JPanelLightBuffered;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectDirectory;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;

class ScatterPlotPanel extends JPanelLightBuffered implements ViewColors,
		Observer, VisualViewInterface {

	public ScatterPlotPanel(final FileNameDisplayPanel fileNameDisplayPanel) {

		this.fileNameDisplayPanel = fileNameDisplayPanel;

		this.historyList = new LinkedList<Area>();
		this.scatterPlotPopupMenu = new ScatterPlotPopupMenu(this,
				this.historyList);

		this.startIndex = 0;
		this.endIndex = GUIFileManager.instance().getFileCount() - 1;
		this.restartIndex = -1;
		this.interruptIndex = -1;

		this.zoomMode = false;
		this.splitDrawing = false;

		this.zoomedArea = new ZoomedArea(GUIFileManager.instance()
				.getFileCount() - 1);
		this.draggedArea = new DraggedArea();

		this.setDoubleBuffered(false);

		this.mouseEventHandler = new MouseEventHandler();
		this.mouseMotionEventHandler = new MouseMotionEventHandler();
		this.addMouseListener(this.mouseEventHandler);
		this.addMouseMotionListener(this.mouseMotionEventHandler);
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		final boolean isGroupSeparetorDraw = this.scatterPlotPopupMenu
				.getStateGroupSeparetor();
		final boolean isFileSeparetorDraw = this.scatterPlotPopupMenu
				.getStateFileSeparetor();
		final boolean isDiagonalLineDraw = this.scatterPlotPopupMenu
				.getStateDiagonalLine();
		final boolean isNumericalInformationDraw = this.scatterPlotPopupMenu
				.getStateNumericalInformation();

		this.drawBackGround(g);
		if (isDiagonalLineDraw) {
			this.drawDiagonalLine(g);
		}
		if (isFileSeparetorDraw) {
			this.drawFileSeparators(g);
		}
		if (isGroupSeparetorDraw) {
			this.drawGroupSeparators(g);
		}
		this.drawCodeClones(g);
		if (this.splitDrawing) {
			this.drawSplitLine(g);
		}
		this.drawSelectedFile(g);
		this.drawSelectedClonePair(g);
		if (isNumericalInformationDraw) {
			this.drawNumericalInformation(g);
		}
		this.drawFrame(g);
	}

	@Override
	public void update(Observable o, Object arg) {
		this.repaint();
	}

	void setZoomMode(final boolean b) {
		this.zoomMode = b;
	}

	void zoomUp(final int startIndex, final int endIndex) {

		this.startIndex = startIndex;
		this.interruptIndex = -1;
		this.restartIndex = -1;
		this.endIndex = endIndex;

		final Area newArea = new Area(this.startIndex, this.interruptIndex,
				this.restartIndex, this.endIndex);
		this.historyList.addFirst(newArea);
		if (HISTORYSIZE < this.historyList.size()) {
			this.historyList.removeLast();
		}

		this.repaint();
	}

	void zoomUp(final int startIndex, final int interruptIndex,
			final int restartIndex, final int endIndex) {

		this.startIndex = startIndex;
		this.endIndex = endIndex;

		if (interruptIndex < restartIndex) {

			this.interruptIndex = interruptIndex;
			this.restartIndex = restartIndex;

		} else {

			this.interruptIndex = -1;
			this.restartIndex = -1;
		}

		Area newArea = new Area(this.startIndex, this.interruptIndex,
				this.restartIndex, this.endIndex);
		this.historyList.addFirst(newArea);
		if (HISTORYSIZE < this.historyList.size()) {
			this.historyList.removeLast();
		}

		this.repaint();
	}

	void reset() {

		this.startIndex = 0;
		this.endIndex = GUIFileManager.instance().getFileCount() - 1;
		this.restartIndex = -1;
		this.interruptIndex = -1;
		this.splitDrawing = false;

		this.repaint();
	}

	void outputScatterPlotImage() {

		String directory = SelectDirectory.getLastDirectory();

		final JFileChooser fileChooser = new JFileChooser(directory);
		fileChooser.setAcceptAllFileFilterUsed(false);

		final ScatterPlotFileFilter scatterPlotFileFilter = new ScatterPlotFileFilter(
				new String[] { "jpg", "png" }, "JPG or PNG Image");
		fileChooser.addChoosableFileFilter(scatterPlotFileFilter);

		fileChooser.setDialogTitle("Output Scatter Plot Image");
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

		switch (fileChooser.showSaveDialog(this)) {

		case JFileChooser.APPROVE_OPTION:

			try {

				final int panelLength = this.getLowerLength();
				final BufferedImage bufferedImage = new BufferedImage(
						panelLength, panelLength, BufferedImage.TYPE_INT_RGB);
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

		final int panelLength = this.getLowerLength();

		g.setColor(SCATTERPLOT_BACKGROUND_COLOR);
		g.fillRect(0, 0, panelLength, panelLength);

	}

	private final void drawFrame(Graphics g) {

		final int panelLength = this.getLowerLength();
		g.setColor(SCATTERPLOT_FRAME_COLOR);
		g.drawRect(0, 0, panelLength, panelLength);
		g.drawRect(1, 1, panelLength - 2, panelLength - 2);
		g.drawRect(2, 2, panelLength - 4, panelLength - 4);
	}

	private final void drawDiagonalLine(Graphics g) {

		final int length = this.getLowerLength() - 1;
		g.setColor(SCATTERPLOT_DIAGONAL_COLOR);
		g.drawLine(1, 1, length - 1, length - 1);
	}

	private final void drawGroupSeparators(Graphics g) {

		final double scaleRatio = this.getScaleRatio();
		final int panelLength = this.getLowerLength();
		final int groupNumber = GUIFileManager.instance().getGroupCount();

		final GUIFile startFile = IDIndexMap.instance().getFile(this.startIndex);
		final int startFileOffset = FileOffsetData.instance().get(startFile);

		if (this.splitDrawing) {

			final GUIFile interruptFile = IDIndexMap.instance()
					.getFile(this.interruptIndex);
			final int interruptFileOffset = FileOffsetData.instance().get(
					interruptFile);
			final double splitPosition = (interruptFileOffset
					+ interruptFile.loc - startFileOffset)
					/ scaleRatio;

			final GUIFile restartFile = IDIndexMap.instance()
					.getFile(this.restartIndex);
			final int restartFileOffset = FileOffsetData.instance().get(
					restartFile);

			for (int groupIndex = 0; groupIndex < groupNumber; groupIndex++) {

				final GUIFile lastFile = GUIFileManager.instance().getLastFile(
						groupIndex);
				final int fileIndex = IDIndexMap.instance().getIndex(
						lastFile.groupID, lastFile.fileID);
				if ((this.startIndex <= fileIndex)
						&& (fileIndex <= this.interruptIndex)) {

					final int lastFileOffset = FileOffsetData.instance().get(
							lastFile);
					final double lastFileEndPosition = (lastFileOffset
							+ lastFile.loc - startFileOffset)
							/ scaleRatio;

					g.setColor(SCATTERPLOT_GROUP_SEPARATOR_COLOR);
					g.drawLine((int) lastFileEndPosition, 0,
							(int) lastFileEndPosition, panelLength);
					g.drawLine(0, (int) lastFileEndPosition, panelLength,
							(int) lastFileEndPosition);

				} else if ((this.restartIndex <= fileIndex)
						&& (fileIndex <= this.endIndex)) {

					final int lastFileOffset = FileOffsetData.instance().get(
							lastFile);
					final double lastFileEndPosition = (lastFileOffset
							+ lastFile.loc - restartFileOffset)
							/ scaleRatio + splitPosition;

					g.setColor(SCATTERPLOT_GROUP_SEPARATOR_COLOR);
					g.drawLine((int) lastFileEndPosition, 0,
							(int) lastFileEndPosition, panelLength);
					g.drawLine(0, (int) lastFileEndPosition, panelLength,
							(int) lastFileEndPosition);
				}
			}

		} else {

			for (int groupIndex = 0; groupIndex < groupNumber; groupIndex++) {

				final GUIFile lastFile = GUIFileManager.instance().getLastFile(
						groupIndex);
				final int fileIndex = IDIndexMap.instance().getIndex(
						lastFile.groupID, lastFile.fileID);
				if ((this.startIndex <= fileIndex)
						&& (fileIndex < this.endIndex)) {

					final int lastFileOffset = FileOffsetData.instance().get(
							lastFile);
					final double lastFileEndPosition = (lastFileOffset
							+ lastFile.loc - startFileOffset)
							/ scaleRatio;

					g.setColor(SCATTERPLOT_GROUP_SEPARATOR_COLOR);
					g.drawLine((int) lastFileEndPosition, 0,
							(int) lastFileEndPosition, panelLength);
					g.drawLine(0, (int) lastFileEndPosition, panelLength,
							(int) lastFileEndPosition);
				}
			}
		}
	}

	private final void drawFileSeparators(Graphics g) {

		final double scaleRatio = this.getScaleRatio();
		final int panelLength = this.getLowerLength();
		final int fileNumber = GUIFileManager.instance().getFileCount();

		final GUIFile startFile = IDIndexMap.instance().getFile(this.startIndex);
		final int startFileOffset = FileOffsetData.instance().get(startFile);

		if (this.splitDrawing) {

			final GUIFile interruptFile = IDIndexMap.instance()
					.getFile(this.interruptIndex);
			final int interruptFileOffset = FileOffsetData.instance().get(
					interruptFile);
			final double splitPosition = (interruptFileOffset
					+ interruptFile.loc - startFileOffset)
					/ scaleRatio;

			final GUIFile restartFile = IDIndexMap.instance()
					.getFile(this.restartIndex);
			final int restartFileOffset = FileOffsetData.instance().get(
					restartFile);

			for (int index = 0; index < fileNumber; index++) {

				if ((this.startIndex <= index)
						&& (index <= this.interruptIndex)) {

					final GUIFile currentFile = IDIndexMap.instance()
							.getFile(index);
					final int currentFileOffset = FileOffsetData.instance()
							.get(currentFile);
					final double currentFileEndPosition = (currentFileOffset
							+ currentFile.loc - startFileOffset)
							/ scaleRatio;

					g.setColor(SCATTERPLOT_FILE_SEPARATOR_COLOR);
					g.drawLine((int) currentFileEndPosition, 0,
							(int) currentFileEndPosition, panelLength);
					g.drawLine(0, (int) currentFileEndPosition, panelLength,
							(int) currentFileEndPosition);

				} else if ((this.restartIndex <= index)
						&& (index <= this.endIndex)) {

					final GUIFile currentFile = IDIndexMap.instance()
							.getFile(index);
					final int currentFileOffset = FileOffsetData.instance()
							.get(currentFile);
					final double currentFileEndPosition = (currentFileOffset
							+ currentFile.loc - restartFileOffset)
							/ scaleRatio + splitPosition;

					g.setColor(SCATTERPLOT_FILE_SEPARATOR_COLOR);
					g.drawLine((int) currentFileEndPosition, 0,
							(int) currentFileEndPosition, panelLength);
					g.drawLine(0, (int) currentFileEndPosition, panelLength,
							(int) currentFileEndPosition);
				}
			}

		} else {

			for (int index = 0; index < fileNumber; index++) {

				if ((this.startIndex <= index) && (index <= this.endIndex)) {

					final GUIFile currentFile = IDIndexMap.instance()
							.getFile(index);
					final int currentFileOffset = FileOffsetData.instance()
							.get(currentFile);
					final double currentFileEndPosition = (currentFileOffset
							+ currentFile.loc - startFileOffset)
							/ scaleRatio;

					g.setColor(SCATTERPLOT_FILE_SEPARATOR_COLOR);
					g.drawLine((int) currentFileEndPosition, 0,
							(int) currentFileEndPosition, panelLength);
					g.drawLine(0, (int) currentFileEndPosition, panelLength,
							(int) currentFileEndPosition);
				}
			}
		}
	}

	private final void drawSplitLine(Graphics g) {

		if (this.splitDrawing) {
			final double scaleRatio = this.getScaleRatio();
			final int panelLength = this.getLowerLength();
			final GUIFile startFile = IDIndexMap.instance()
					.getFile(this.startIndex);
			final int startFileOffset = FileOffsetData.instance()
					.get(startFile);
			final GUIFile interruptFile = IDIndexMap.instance()
					.getFile(this.interruptIndex);
			final int interruptFileOffset = FileOffsetData.instance().get(
					interruptFile);
			final double splitPosition = (interruptFileOffset
					+ interruptFile.loc - startFileOffset)
					/ scaleRatio;

			g.setColor(SCATTERPLOT_SPLIT_LINE_COLOR);
			g.fillRect((int) Math.rint(splitPosition) - 1, 1, 3,
					panelLength - 2);
			g.fillRect(1, (int) Math.rint(splitPosition) - 1, panelLength - 2,
					3);
		}
	}

	private final void drawCodeClones(Graphics g) {

		if (this.scatterPlotPopupMenu.isFineGranularity()) {

			this.drawCodeClonesFinely(g);

		} else if (this.scatterPlotPopupMenu.isRoughGranularity()) {

			this.drawCodeClonesRoughly(g);

		} else if (this.scatterPlotPopupMenu.isAutoGranularity()) {

			final int drawnFileNumber;
			if (this.splitDrawing) {
				drawnFileNumber = (this.interruptIndex - this.startIndex + 1)
						+ (this.endIndex - this.restartIndex + 1);
			} else {
				drawnFileNumber = this.endIndex - this.startIndex + 1;
			}
			final int panelLength = this.getLowerLength();
			if (this.scatterPlotPopupMenu.getScaleFactor() * panelLength < drawnFileNumber) {

				this.drawCodeClonesRoughly(g);

			} else {

				this.drawCodeClonesFinely(g);
			}
		}
	}

	private final void drawCodeClonesFinely(Graphics g) {

		final boolean uninterestingClonesDisplay = UninterestingClonesDisplay
				.getInstance(UNINTERESTING).isDisplay();
		final int rnrThreshold = RNRValue.getInstance(RNR).get();
		final double scaleRatio = this.getScaleRatio();

		final GUIFile startFile = IDIndexMap.instance().getFile(this.startIndex);
		final int startFileOffset = FileOffsetData.instance().get(startFile);

		final Set<FilePair> fileKeys = GUICloneManager.instance()
				.getClonePairFileKeys();

		if (this.splitDrawing) {

			final GUIFile interruptFile = IDIndexMap.instance()
					.getFile(this.interruptIndex);
			final int interruptFileOffset = FileOffsetData.instance().get(
					interruptFile);
			final double splitPosition = (interruptFileOffset
					+ interruptFile.loc - startFileOffset)
					/ scaleRatio;

			final GUIFile restartFile = IDIndexMap.instance()
					.getFile(this.restartIndex);
			final int restartFileOffset = FileOffsetData.instance().get(
					restartFile);

			for (FilePair fileKey : fileKeys) {
				final GUIFile file1 = fileKey.left;
				final GUIFile file2 = fileKey.right;
				final int file1Index = IDIndexMap.instance().getIndex(
						file1.groupID, file1.fileID);
				final int file2Index = IDIndexMap.instance().getIndex(
						file2.groupID, file2.fileID);

				if (((this.startIndex <= file1Index) && (file1Index <= this.interruptIndex))
						&& ((this.startIndex <= file2Index) && (file2Index <= this.interruptIndex))) {

					final int file1Offset = FileOffsetData.instance()
							.get(file1);
					final int file2Offset = FileOffsetData.instance()
							.get(file2);

					final List<GUIClonePair> clonePairs = GUICloneManager
							.instance().getClonePairs(fileKey);

					for (final GUIClonePair clonePair : clonePairs) {
						final GUIClone clone1 = clonePair.left;
						final GUIClone clone2 = clonePair.right;

						final double startX = (file1Offset + clone1.fromLine - startFileOffset)
								/ scaleRatio;
						final double endX = (file1Offset + clone1.toLine - startFileOffset)
								/ scaleRatio;
						final double startY = (file2Offset + clone2.fromLine - startFileOffset)
								/ scaleRatio;
						final double endY = (file2Offset + clone2.toLine - startFileOffset)
								/ scaleRatio;

						if (rnrThreshold <= clonePair.getRNR()) {
							g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
							g.drawLine((int) startX, (int) startY, (int) endX,
									(int) endY);
						} else {
							if (uninterestingClonesDisplay) {
								g.setColor(SCATTERPLOT_FILTEROUT_CLONEPAIR_COLOR);
								g.drawLine((int) startX, (int) startY,
										(int) endX, (int) endY);
							}
						}

					}

				} else if (((this.startIndex <= file1Index) && (file1Index <= this.interruptIndex))
						&& ((this.restartIndex <= file2Index) && (file2Index <= this.endIndex))) {

					final int file1Offset = FileOffsetData.instance()
							.get(file1);
					final int file2Offset = FileOffsetData.instance()
							.get(file2);

					final List<GUIClonePair> clonePairs = GUICloneManager
							.instance().getClonePairs(fileKey);
					for (final GUIClonePair clonePair : clonePairs) {
						final GUIClone clone1 = clonePair.left;
						final GUIClone clone2 = clonePair.right;
						final double startX = (file1Offset + clone1.fromLine - startFileOffset)
								/ scaleRatio;
						final double endX = (file1Offset + clone1.toLine - startFileOffset)
								/ scaleRatio;
						final double startY = (file2Offset + clone2.fromLine - restartFileOffset)
								/ scaleRatio + splitPosition;
						final double endY = (file2Offset + clone2.toLine - restartFileOffset)
								/ scaleRatio + splitPosition;

						if (rnrThreshold <= clonePair.getRNR()) {
							g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
							g.drawLine((int) startX, (int) startY, (int) endX,
									(int) endY);
						} else {
							if (uninterestingClonesDisplay) {
								g.setColor(SCATTERPLOT_FILTEROUT_CLONEPAIR_COLOR);
								g.drawLine((int) startX, (int) startY,
										(int) endX, (int) endY);
							}
						}
					}

				} else if (((this.restartIndex <= file1Index) && (file1Index <= this.endIndex))
						&& ((this.restartIndex <= file2Index) && (file2Index <= this.endIndex))) {

					final int file1Offset = FileOffsetData.instance()
							.get(file1);
					final int file2Offset = FileOffsetData.instance()
							.get(file2);

					final List<GUIClonePair> clonePairs = GUICloneManager
							.instance().getClonePairs(fileKey);

					for (final GUIClonePair clonePair : clonePairs) {
						final GUIClone clone1 = clonePair.left;
						final GUIClone clone2 = clonePair.right;

						final double startX = (file1Offset + clone1.fromLine - restartFileOffset)
								/ scaleRatio + splitPosition;
						final double endX = (file1Offset + clone1.toLine - restartFileOffset)
								/ scaleRatio + splitPosition;
						final double startY = (file2Offset + clone2.fromLine - restartFileOffset)
								/ scaleRatio + splitPosition;
						final double endY = (file2Offset + clone2.toLine - restartFileOffset)
								/ scaleRatio + splitPosition;

						if (rnrThreshold <= clonePair.getRNR()) {
							g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
							g.drawLine((int) startX, (int) startY, (int) endX,
									(int) endY);
						} else {
							if (uninterestingClonesDisplay) {
								g.setColor(SCATTERPLOT_FILTEROUT_CLONEPAIR_COLOR);
								g.drawLine((int) startX, (int) startY,
										(int) endX, (int) endY);
							}
						}
					}
				}
			}

		} else {

			for (FilePair fileKey : fileKeys) {
				final GUIFile file1 = fileKey.left;
				final GUIFile file2 = fileKey.right;
				final int file1Index = IDIndexMap.instance().getIndex(
						file1.groupID, file1.fileID);
				final int file2Index = IDIndexMap.instance().getIndex(
						file2.groupID, file2.fileID);

				if (((this.startIndex <= file1Index) && (file1Index <= this.endIndex))
						&& ((this.startIndex <= file2Index) && (file2Index <= this.endIndex))) {

					final int file1Offset = FileOffsetData.instance()
							.get(file1);
					final int file2Offset = FileOffsetData.instance()
							.get(file2);

					final List<GUIClonePair> clonePairs = GUICloneManager
							.instance().getClonePairs(fileKey);

					for (final GUIClonePair clonePair : clonePairs) {

						final GUIClone clone1 = clonePair.left;
						final GUIClone clone2 = clonePair.right;

						final double startX = (file1Offset + clone1.fromLine - startFileOffset)
								/ scaleRatio;
						final double endX = (file1Offset + clone1.toLine - startFileOffset)
								/ scaleRatio;
						final double startY = (file2Offset + clone2.fromLine - startFileOffset)
								/ scaleRatio;
						final double endY = (file2Offset + clone2.toLine - startFileOffset)
								/ scaleRatio;

						if (rnrThreshold <= clonePair.getRNR()) {
							g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
							g.drawLine((int) startX, (int) startY, (int) endX,
									(int) endY);
						} else {
							if (uninterestingClonesDisplay) {
								g.setColor(SCATTERPLOT_FILTEROUT_CLONEPAIR_COLOR);
								g.drawLine((int) startX, (int) startY,
										(int) endX, (int) endY);
							}
						}
					}
				}
			}
		}
	}

	private final void drawCodeClonesRoughly(Graphics g) {

		final double scaleRatio = this.getScaleRatio();

		final GUIFile startFile = IDIndexMap.instance().getFile(this.startIndex);
		final int startFileOffset = FileOffsetData.instance().get(startFile);

		final Set<FilePair> fileKeys = GUICloneManager.instance()
				.getClonePairFileKeys();

		if (this.splitDrawing) {

			final GUIFile interruptFile = IDIndexMap.instance()
					.getFile(this.interruptIndex);
			final int interruptFileOffset = FileOffsetData.instance().get(
					interruptFile);
			final double splitPosition = (interruptFileOffset
					+ interruptFile.loc - startFileOffset)
					/ scaleRatio;

			final GUIFile restartFile = IDIndexMap.instance()
					.getFile(this.restartIndex);
			final int restartFileOffset = FileOffsetData.instance().get(
					restartFile);

			for (final FilePair fileKey : fileKeys) {
				final GUIFile file1 = fileKey.left;
				final GUIFile file2 = fileKey.right;
				final int file1Index = IDIndexMap.instance().getIndex(
						file1.groupID, file1.fileID);
				final int file2Index = IDIndexMap.instance().getIndex(
						file2.groupID, file2.fileID);

				if (((this.startIndex <= file1Index) && (file1Index <= this.interruptIndex))
						&& ((this.startIndex <= file2Index) && (file2Index <= this.interruptIndex))) {

					final double file1StartX = (FileOffsetData.instance().get(
							file1) - startFileOffset)
							/ scaleRatio;
					final double file1Width = file1.loc / scaleRatio;
					final double file2StartY = (FileOffsetData.instance().get(
							file2) - startFileOffset)
							/ scaleRatio;
					final double file2Height = file2.loc / scaleRatio;

					g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
					g.fillRect((int) Math.rint(file1StartX),
							(int) Math.rint(file2StartY),
							(int) Math.ceil(file1Width),
							(int) Math.ceil(file2Height));

				} else if (((this.startIndex <= file1Index) && (file1Index <= this.interruptIndex))
						&& ((this.restartIndex <= file2Index) && (file2Index <= this.endIndex))) {

					final double file1StartX = (FileOffsetData.instance().get(
							file1) - startFileOffset)
							/ scaleRatio;
					final double file1Width = file1.loc / scaleRatio;
					final double file2StartY = (FileOffsetData.instance().get(
							file2) - restartFileOffset)
							/ scaleRatio + splitPosition;
					final double file2Height = file2.loc / scaleRatio;

					g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
					g.fillRect((int) Math.rint(file1StartX),
							(int) Math.rint(file2StartY),
							(int) Math.ceil(file1Width),
							(int) Math.ceil(file2Height));

				} else if (((this.restartIndex <= file1Index) && (file1Index <= this.endIndex))
						&& ((this.restartIndex <= file2Index) && (file2Index <= this.endIndex))) {

					final double file1StartX = (FileOffsetData.instance().get(
							file1) - restartFileOffset)
							/ scaleRatio + splitPosition;
					final double file1Width = file1.loc / scaleRatio;
					final double file2StartY = (FileOffsetData.instance().get(
							file2) - restartFileOffset)
							/ scaleRatio + splitPosition;
					final double file2Height = file2.loc / scaleRatio;

					g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
					g.fillRect((int) Math.rint(file1StartX),
							(int) Math.rint(file2StartY),
							(int) Math.ceil(file1Width),
							(int) Math.ceil(file2Height));
				}
			}

		} else {

			for (final FilePair fileKey : fileKeys) {
				final GUIFile file1 = fileKey.left;
				final GUIFile file2 = fileKey.right;
				final int file1Index = IDIndexMap.instance().getIndex(
						file1.groupID, file1.fileID);
				final int file2Index = IDIndexMap.instance().getIndex(
						file2.groupID, file2.fileID);

				if (((this.startIndex <= file1Index) && (file1Index <= this.endIndex))
						&& ((this.startIndex <= file2Index) && (file2Index <= this.endIndex))) {

					final double file1StartX = (FileOffsetData.instance().get(
							file1) - startFileOffset)
							/ scaleRatio;
					final double file1Width = file1.loc / scaleRatio;
					final double file2StartY = (FileOffsetData.instance().get(
							file2) - startFileOffset)
							/ scaleRatio;
					final double file2Height = file2.loc / scaleRatio;

					g.setColor(SCATTERPLOT_FILTERIN_CLONEPAIR_COLOR);
					g.fillRect((int) Math.rint(file1StartX),
							(int) Math.rint(file2StartY),
							(int) Math.ceil(file1Width),
							(int) Math.ceil(file2Height));
				}
			}
		}
	}

	private final void drawSelectedClonePair(Graphics g) {

		if (SelectedEntities.<GUIClonePair> getInstance(CLONEPAIR).isSet()) {
			final GUIClonePair clonePair = SelectedEntities
					.<GUIClonePair> getInstance(CLONEPAIR).get().get(0);

			final double scaleRatio = this.getScaleRatio();
			final GUIFile startFile = IDIndexMap.instance()
					.getFile(this.startIndex);
			final int startFileOffset = FileOffsetData.instance()
					.get(startFile);

			final GUIClone horizontalClone = clonePair.left;
			final GUIClone verticalClone = clonePair.right;
			final GUIFile horizontalFile = horizontalClone.file;
			final GUIFile verticalFile = verticalClone.file;
			final int horizontalFileIndex = IDIndexMap.instance().getIndex(
					horizontalFile.groupID, horizontalFile.fileID);
			final int verticalFileIndex = IDIndexMap.instance().getIndex(
					verticalFile.groupID, verticalFile.fileID);

			if (this.splitDrawing) {

				final GUIFile interruptFile = IDIndexMap.instance()
						.getFile(this.interruptIndex);
				final int interruptFileOffset = FileOffsetData.instance().get(
						interruptFile);
				final double splitPosition = (interruptFileOffset
						+ interruptFile.loc - startFileOffset)
						/ scaleRatio;

				final GUIFile restartFile = IDIndexMap.instance()
						.getFile(this.restartIndex);
				final int restartFileOffset = FileOffsetData.instance().get(
						restartFile);

				if (((this.startIndex <= horizontalFileIndex) && (horizontalFileIndex <= this.interruptIndex))
						&& ((this.startIndex <= verticalFileIndex) && (verticalFileIndex <= this.interruptIndex))) {

					final double horizontalCloneStart = (FileOffsetData
							.instance().get(horizontalFile)
							+ horizontalClone.fromLine - startFileOffset)
							/ scaleRatio;
					final double horizontalCloneEnd = (FileOffsetData
							.instance().get(horizontalFile)
							+ horizontalClone.toLine - startFileOffset)
							/ scaleRatio;
					final double verticalCloneStart = (FileOffsetData
							.instance().get(verticalFile)
							+ verticalClone.fromLine - startFileOffset)
							/ scaleRatio;
					final double verticalCloneEnd = (FileOffsetData.instance()
							.get(verticalFile) + verticalClone.toLine - startFileOffset)
							/ scaleRatio;

					g.setColor(SCATTERPLOT_SELECTED_CLONEPAIR_COLOR);
					g.drawLine((int) horizontalCloneStart,
							(int) verticalCloneStart, (int) horizontalCloneEnd,
							(int) verticalCloneEnd);

				} else if (((this.startIndex <= horizontalFileIndex) && (horizontalFileIndex <= this.interruptIndex))
						&& ((this.restartIndex <= verticalFileIndex) && (verticalFileIndex <= this.endIndex))) {

					final double horizontalCloneStart = (FileOffsetData
							.instance().get(horizontalFile)
							+ horizontalClone.fromLine - startFileOffset)
							/ scaleRatio;
					final double horizontalCloneEnd = (FileOffsetData
							.instance().get(horizontalFile)
							+ horizontalClone.toLine - startFileOffset)
							/ scaleRatio;
					final double verticalCloneStart = (FileOffsetData
							.instance().get(verticalFile)
							+ verticalClone.fromLine - restartFileOffset)
							/ scaleRatio + splitPosition;
					final double verticalCloneEnd = (FileOffsetData.instance()
							.get(verticalFile) + verticalClone.toLine - restartFileOffset)
							/ scaleRatio + splitPosition;

					g.setColor(SCATTERPLOT_SELECTED_CLONEPAIR_COLOR);
					g.drawLine((int) horizontalCloneStart,
							(int) verticalCloneStart, (int) horizontalCloneEnd,
							(int) verticalCloneEnd);

				} else if (((this.restartIndex <= horizontalFileIndex) && (horizontalFileIndex <= this.endIndex))
						&& ((this.restartIndex <= verticalFileIndex) && (verticalFileIndex <= this.endIndex))) {

					final double horizontalCloneStart = (FileOffsetData
							.instance().get(horizontalFile)
							+ horizontalClone.fromLine - restartFileOffset)
							/ scaleRatio + splitPosition;
					final double horizontalCloneEnd = (FileOffsetData
							.instance().get(horizontalFile)
							+ horizontalClone.toLine - restartFileOffset)
							/ scaleRatio + splitPosition;
					final double verticalCloneStart = (FileOffsetData
							.instance().get(verticalFile)
							+ verticalClone.fromLine - startFileOffset)
							/ scaleRatio;
					final double verticalCloneEnd = (FileOffsetData.instance()
							.get(verticalFile) + verticalClone.toLine - startFileOffset)
							/ scaleRatio;

					g.setColor(SCATTERPLOT_SELECTED_CLONEPAIR_COLOR);
					g.drawLine((int) horizontalCloneStart,
							(int) verticalCloneStart, (int) horizontalCloneEnd,
							(int) verticalCloneEnd);

				}

			} else {

				if (((this.startIndex <= horizontalFileIndex) && (horizontalFileIndex <= this.endIndex))
						&& ((this.startIndex <= verticalFileIndex) && (verticalFileIndex <= this.endIndex))) {

					final double horizontalCloneStart = (FileOffsetData
							.instance().get(horizontalFile)
							+ horizontalClone.fromLine - startFileOffset)
							/ scaleRatio;
					final double horizontalCloneEnd = (FileOffsetData
							.instance().get(horizontalFile)
							+ horizontalClone.toLine - startFileOffset)
							/ scaleRatio;
					final double verticalCloneStart = (FileOffsetData
							.instance().get(verticalFile)
							+ verticalClone.fromLine - startFileOffset)
							/ scaleRatio;
					final double verticalCloneEnd = (FileOffsetData.instance()
							.get(verticalFile) + verticalClone.toLine - startFileOffset)
							/ scaleRatio;

					g.setColor(SCATTERPLOT_SELECTED_CLONEPAIR_COLOR);
					g.drawLine((int) horizontalCloneStart,
							(int) verticalCloneStart, (int) horizontalCloneEnd,
							(int) verticalCloneEnd);
				}
			}
		}
	}

	private final void drawDraggedArea(Graphics g) {

		final double scaleRatio = this.getScaleRatio();

		final GUIFile startFile = IDIndexMap.instance().getFile(this.startIndex);
		final int startFileOffset = FileOffsetData.instance().get(startFile);

		if (this.splitDrawing) {

			final GUIFile interruptFile = IDIndexMap.instance()
					.getFile(this.interruptIndex);
			final int interruptFileOffset = FileOffsetData.instance().get(
					interruptFile);

			final GUIFile restartFile = IDIndexMap.instance()
					.getFile(this.restartIndex);
			final int restartFileOffset = FileOffsetData.instance().get(
					restartFile);

			final double splitPosition = (interruptFileOffset
					+ interruptFile.loc - startFileOffset)
					/ scaleRatio;

			{
				final double x = (this.draggedArea.getStartTokenX() - startFileOffset)
						/ scaleRatio;
				final double y = (this.draggedArea.getStartTokenY() - startFileOffset)
						/ scaleRatio;
				double width = (this.draggedArea.getEndTokenX() - this.draggedArea
						.getStartTokenX()) / scaleRatio;
				double height = (this.draggedArea.getEndTokenY() - this.draggedArea
						.getStartTokenY()) / scaleRatio;

				if (splitPosition < (x + width)) {
					width -= (x + width) - splitPosition;
				}
				if (splitPosition < (y + height)) {
					height -= (y + height) - splitPosition;
				}

				g.setColor(SCATTERPLOT_SELECTED_AREA_COLOR);
				g.drawRect((int) x, (int) y, (int) Math.ceil(width),
						(int) Math.ceil(height));
			}

			{
				double startX = (this.draggedArea.getStartTokenX() - restartFileOffset)
						/ scaleRatio + splitPosition;
				double startY = (this.draggedArea.getStartTokenY() - startFileOffset)
						/ scaleRatio;
				double endX = (this.draggedArea.getEndTokenX() - restartFileOffset)
						/ scaleRatio + splitPosition;
				double height = (this.draggedArea.getEndTokenY() - this.draggedArea
						.getStartTokenY()) / scaleRatio;

				if (startX < splitPosition) {
					startX = splitPosition;
				}
				if (endX < splitPosition) {
					endX = splitPosition;
				}
				final double width = endX - startX;
				if (splitPosition < (startY + height)) {
					height -= (startY + height) - splitPosition;
				}

				g.setColor(SCATTERPLOT_SELECTED_AREA_COLOR);
				g.drawRect((int) startX, (int) startY, (int) Math.ceil(width),
						(int) Math.ceil(height));
			}

			{
				double startX = (this.draggedArea.getStartTokenX() - startFileOffset)
						/ scaleRatio;
				double startY = (this.draggedArea.getStartTokenY() - restartFileOffset)
						/ scaleRatio + splitPosition;
				double width = (this.draggedArea.getEndTokenX() - this.draggedArea
						.getStartTokenX()) / scaleRatio;
				double endY = (this.draggedArea.getEndTokenY() - restartFileOffset)
						/ scaleRatio + splitPosition;

				if (startY < splitPosition) {
					startY = splitPosition;
				}
				if (endY < splitPosition) {
					endY = splitPosition;
				}
				if (splitPosition < (startX + width)) {
					width -= (startX + width) - splitPosition;
				}
				final double height = endY - startY;

				g.setColor(SCATTERPLOT_SELECTED_AREA_COLOR);
				g.drawRect((int) startX, (int) startY, (int) Math.ceil(width),
						(int) Math.ceil(height));
			}

			{
				double startX = (this.draggedArea.getStartTokenX() - restartFileOffset)
						/ scaleRatio + splitPosition;
				double startY = (this.draggedArea.getStartTokenY() - restartFileOffset)
						/ scaleRatio + splitPosition;
				double endX = (this.draggedArea.getEndTokenX() - restartFileOffset)
						/ scaleRatio + splitPosition;
				double endY = (this.draggedArea.getEndTokenY() - restartFileOffset)
						/ scaleRatio + splitPosition;

				if (startX < splitPosition) {
					startX = splitPosition;
				}
				if (endX < splitPosition) {
					endX = splitPosition;
				}
				if (startY < splitPosition) {
					startY = splitPosition;
				}
				if (endY < splitPosition) {
					endY = splitPosition;
				}
				final double width = endX - startX;
				final double height = endY - startY;

				g.setColor(SCATTERPLOT_SELECTED_AREA_COLOR);
				g.drawRect((int) startX, (int) startY, (int) Math.ceil(width),
						(int) Math.ceil(height));
			}

		} else {

			final double startX = (this.draggedArea.getStartTokenX() - startFileOffset)
					/ scaleRatio;
			final double startY = (this.draggedArea.getStartTokenY() - startFileOffset)
					/ scaleRatio;
			final double width = (this.draggedArea.getEndTokenX() - this.draggedArea
					.getStartTokenX()) / scaleRatio;
			final double height = (this.draggedArea.getEndTokenY() - this.draggedArea
					.getStartTokenY()) / scaleRatio;

			g.setColor(SCATTERPLOT_SELECTED_AREA_COLOR);
			g.drawRect((int) startX, (int) startY, (int) Math.ceil(width),
					(int) Math.ceil(height));
		}
	}

	private final void drawSelectedFile(Graphics g) {

		final double scaleRatio = this.getScaleRatio();
		final int panelLength = this.getLowerLength();

		final SelectedEntities<GUIFile> horizontalSelectedFiles = SelectedEntities
				.<GUIFile> getInstance(HORIZONTAL_FILE);
		final SelectedEntities<GUIFile> verticalSelectedFiles = SelectedEntities
				.<GUIFile> getInstance(VERTICAL_FILE);

		if (horizontalSelectedFiles.isSet()) {

			final GUIFile horizontalFile = horizontalSelectedFiles.get().get(0);
			final int horizontalFileIndex = IDIndexMap.instance().getIndex(
					horizontalFile.groupID, horizontalFile.fileID);
			final int horizontalFileOffset = FileOffsetData.instance().get(
					horizontalFile);

			final GUIFile startFile = IDIndexMap.instance()
					.getFile(this.startIndex);
			final int startFileOffset = FileOffsetData.instance()
					.get(startFile);

			if (this.splitDrawing) {

				if ((this.startIndex <= horizontalFileIndex)
						&& (horizontalFileIndex <= this.interruptIndex)) {

					final double startX = (horizontalFileOffset - startFileOffset)
							/ scaleRatio;
					final double delta = horizontalFile.loc / scaleRatio;

					g.setColor(VISUAL_HORIZONTAL_COLOR);
					g.fillRect((int) startX, 0, (int) Math.ceil(delta),
							panelLength);

				} else if ((this.restartIndex <= horizontalFileIndex)
						&& (horizontalFileIndex <= this.endIndex)) {

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(this.restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(this.interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);

					final double startX = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio
							+ (horizontalFileOffset - restartFileOffset)
							/ scaleRatio;
					final double delta = horizontalFile.loc / scaleRatio;

					g.setColor(VISUAL_HORIZONTAL_COLOR);
					g.fillRect((int) startX, 0, (int) Math.ceil(delta),
							panelLength);
				}
			} else {

				final double startX = (horizontalFileOffset - startFileOffset)
						/ scaleRatio;
				final double delta = horizontalFile.loc / scaleRatio;

				g.setColor(VISUAL_HORIZONTAL_COLOR);
				g.fillRect((int) startX, 0, (int) Math.ceil(delta), panelLength);

			}
		}

		if (verticalSelectedFiles.isSet()) {

			final GUIFile verticalFile = verticalSelectedFiles.get().get(0);
			g.setColor(VISUAL_VERTICAL_COLOR);

			final int verticalFileIndex = IDIndexMap.instance().getIndex(
					verticalFile.groupID, verticalFile.fileID);
			final int verticalFileOffset = FileOffsetData.instance().get(
					verticalFile);

			final GUIFile startFile = IDIndexMap.instance()
					.getFile(this.startIndex);
			final int startFileOffset = FileOffsetData.instance()
					.get(startFile);

			if (this.splitDrawing) {

				if ((this.startIndex <= verticalFileIndex)
						&& (verticalFileIndex <= this.interruptIndex)) {

					final double startY = (verticalFileOffset - startFileOffset)
							/ scaleRatio;
					final double delta = verticalFile.loc / scaleRatio;

					g.setColor(VISUAL_VERTICAL_COLOR);
					g.fillRect(0, (int) startY, panelLength,
							(int) Math.ceil(delta));

				} else if ((this.restartIndex <= verticalFileIndex)
						&& (verticalFileIndex <= this.endIndex)) {

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(this.restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(this.interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);

					final double startY = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio
							+ (verticalFileOffset - restartFileOffset)
							/ scaleRatio;
					final double delta = verticalFile.loc / scaleRatio;

					g.setColor(VISUAL_VERTICAL_COLOR);
					g.fillRect(0, (int) startY, panelLength,
							(int) Math.ceil(delta));
				}
			} else {

				final double startY = (verticalFileOffset - startFileOffset)
						/ scaleRatio;
				final double delta = verticalFile.loc / scaleRatio;

				g.setColor(VISUAL_VERTICAL_COLOR);
				g.fillRect(0, (int) startY, panelLength, (int) Math.ceil(delta));

			}
		}
	}

	private final void drawNumericalInformation(Graphics g) {

		int panelLength = this.getLowerLength();
		int fileNumber = 0;
		int loc = 0;

		if ((this.interruptIndex == -1) && (this.restartIndex == -1)) {

			fileNumber = this.endIndex - this.startIndex + 1;

			for (int i = this.startIndex; i <= this.endIndex; i++) {
				GUIFile element = IDIndexMap.instance().getFile(i);
				loc += element.loc;
			}

		} else {

			fileNumber = (this.interruptIndex - this.startIndex + 1)
					+ (this.endIndex - this.restartIndex + 1);

			for (int i = this.startIndex; i <= this.interruptIndex; i++) {

				GUIFile element = IDIndexMap.instance().getFile(i);
				loc += element.loc;
			}

			for (int i = this.restartIndex; i <= this.endIndex; i++) {

				GUIFile element = IDIndexMap.instance().getFile(i);
				loc += element.loc;
			}
		}

		g.setColor(SCATTERPLOT_BACKGROUND_COLOR);
		g.fillRoundRect(panelLength - 210, 15, 200, 65, 5, 5);

		int xDrewPoint = panelLength - 200;
		g.setColor(SCATTERPLOT_STRING_COLOR);
		java.text.NumberFormat numberFormat = java.text.NumberFormat
				.getInstance();
		g.drawString(
				"Number of drawn Files: " + numberFormat.format(fileNumber),
				xDrewPoint, 30);
		g.drawString("Lines of drawn Files: " + numberFormat.format(loc),
				xDrewPoint, 50);
	}

	private final boolean isValidZoomedArea() {

		if (this.splitDrawing) {

			final GUIFile startFile = IDIndexMap.instance()
					.getFile(this.startIndex);
			final int startFileOffset = FileOffsetData.instance()
					.get(startFile);
			final GUIFile interruptFile = IDIndexMap.instance()
					.getFile(this.interruptIndex);
			final int interruptFileOffset = FileOffsetData.instance().get(
					interruptFile);
			final double splitPosition = (interruptFileOffset
					+ interruptFile.loc - startFileOffset)
					/ this.getScaleRatio();

			if ((this.zoomedArea.getHorizontalStartPosition() < splitPosition)
					&& (splitPosition < this.zoomedArea
							.getHorizontalEndPosition())) {

				this.zoomedArea.rollBack();
				JOptionPane.showMessageDialog(this,
						"Cann't over Interrupted Line!", "Gemini",
						JOptionPane.WARNING_MESSAGE);
				return false;

			} else if ((this.zoomedArea.getVerticalStartPosition() < splitPosition)
					&& (splitPosition < this.zoomedArea
							.getVerticalEndPosition())) {

				this.zoomedArea.rollBack();
				JOptionPane.showMessageDialog(this,
						"Cann't over Interrupted Line.", "Gemini",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		return true;
	}

	private void zoomUp() {

		int low = this.startIndex;
		int high = this.endIndex;
		int horizontalStartFileID = IDIndexMap.instance().searchFileIndex(
				this.zoomedArea.getHorizontalStartToken(), low, high);
		int horizontalEndFileID = IDIndexMap.instance().searchFileIndex(
				this.zoomedArea.getHorizontalEndToken(), low, high);
		int verticalStartFileID = IDIndexMap.instance().searchFileIndex(
				this.zoomedArea.getVerticalStartToken(), low, high);
		int verticalEndFileID = IDIndexMap.instance().searchFileIndex(
				this.zoomedArea.getVerticalEndToken(), low, high);

		int startIndex = 0;
		int endIndex = 0;
		int interruptIndex = 0;
		int restartIndex = 0;

		if (horizontalStartFileID < verticalStartFileID) {
			startIndex = horizontalStartFileID;
		} else {
			startIndex = verticalStartFileID;
		}
		if (horizontalEndFileID < verticalEndFileID) {
			endIndex = verticalEndFileID;
		} else {
			endIndex = horizontalEndFileID;
		}

		if (horizontalStartFileID < verticalStartFileID) {

			if (1 < (verticalStartFileID - horizontalEndFileID)) {

				interruptIndex = horizontalEndFileID;
				restartIndex = verticalStartFileID;
				this.splitDrawing = true;

			} else {

				interruptIndex = -1;
				restartIndex = -1;
				this.splitDrawing = false;
			}

		} else if (verticalStartFileID < horizontalStartFileID) {

			if (1 < (horizontalStartFileID - verticalEndFileID)) {

				interruptIndex = verticalEndFileID;
				restartIndex = horizontalStartFileID;
				this.splitDrawing = true;

			} else {

				interruptIndex = -1;
				restartIndex = -1;
				this.splitDrawing = false;
			}

		} else {

			interruptIndex = -1;
			restartIndex = -1;
			this.splitDrawing = false;
		}

		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.interruptIndex = interruptIndex;
		this.restartIndex = restartIndex;

		final Area newArea = new Area(this.startIndex, this.interruptIndex,
				this.restartIndex, this.endIndex);
		this.historyList.addFirst(newArea);
		if (HISTORYSIZE < this.historyList.size()) {
			this.historyList.removeLast();
		}

		this.repaint();
	}

	private final int getLowerLength() {

		final int height = this.getHeight();
		final int width = this.getWidth();

		if (height < width) {
			return height - 1;
		} else {
			return width - 1;
		}
	}

	private final double getScaleRatio() {

		if (this.splitDrawing) {

			final GUIFile startFile = IDIndexMap.instance()
					.getFile(this.startIndex);
			final GUIFile interruptFile = IDIndexMap.instance()
					.getFile(this.interruptIndex);
			final GUIFile restartFile = IDIndexMap.instance()
					.getFile(this.restartIndex);
			final GUIFile endFile = IDIndexMap.instance().getFile(this.endIndex);
			final int startToken = FileOffsetData.instance().get(startFile);
			final int interruptToken = FileOffsetData.instance().get(
					interruptFile)
					+ interruptFile.loc;
			final int restartToken = FileOffsetData.instance().get(restartFile);
			final int endToken = FileOffsetData.instance().get(endFile)
					+ endFile.loc;
			final int displayToken = (endToken - restartToken)
					+ (interruptToken - startToken);

			return (double) displayToken / (double) this.getLowerLength();

		} else {

			final GUIFile startFile = IDIndexMap.instance()
					.getFile(this.startIndex);
			final GUIFile endFile = IDIndexMap.instance().getFile(this.endIndex);
			final int startToken = FileOffsetData.instance().get(startFile);
			final int endToken = FileOffsetData.instance().get(endFile)
					+ endFile.loc;
			final int displayToken = endToken - startToken;

			return (double) displayToken / (double) this.getLowerLength();
		}
	}

	synchronized void sleep(long time) {

		try {
			this.wait(time);
		} catch (InterruptedException e) {
			MessagePrinter.ERR.println("Interrupt time was wrong :");
		}
	}

	final private static int HISTORYSIZE = 20;

	final private FileNameDisplayPanel fileNameDisplayPanel;

	final private ScatterPlotPopupMenu scatterPlotPopupMenu;

	final private LinkedList<Area> historyList;

	private int startIndex;

	private int endIndex;

	private int restartIndex;

	private int interruptIndex;

	final private ZoomedArea zoomedArea;

	final private DraggedArea draggedArea;

	private boolean zoomMode;

	private boolean splitDrawing;

	final private MouseEventHandler mouseEventHandler;

	final private MouseMotionEventHandler mouseMotionEventHandler;

	class MouseEventHandler implements MouseListener {

		public void mousePressed(MouseEvent e) {

			int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {

				final GUIFile startFile = IDIndexMap.instance()
						.getFile(startIndex);
				final int startFileOffset = FileOffsetData.instance().get(
						startFile);

				final double scaleRatio = getScaleRatio();

				double x = 0;
				double y = 0;

				if (splitDrawing) {

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);
					final double splitPosition = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio;

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					if (e.getX() < splitPosition) {

						x = e.getX() * scaleRatio + startFileOffset;

					} else if (splitPosition < e.getX()) {

						x = (e.getX() - splitPosition) * scaleRatio
								+ restartFileOffset;
					}

				} else {

					x = e.getX() * scaleRatio + startFileOffset;
				}

				if (splitDrawing) {

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);
					final double splitPosition = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio;

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					if (e.getY() < splitPosition) {

						y = e.getY() * scaleRatio + startFileOffset;

					} else if (splitPosition < e.getY()) {

						y = (e.getY() - splitPosition) * scaleRatio
								+ restartFileOffset;
					}

				} else {

					y = e.getY() * scaleRatio + startFileOffset;
				}

				if (ScatterPlotPanel.this.zoomMode) {
					ScatterPlotPanel.this.zoomedArea.setPressedToken(
							(int) Math.rint(x), (int) Math.rint(y));
					ScatterPlotPanel.this.zoomedArea.setPressedPosition(
							e.getX(), e.getY());
				}

				ScatterPlotPanel.this.draggedArea.setPressedToken(
						(int) Math.rint(x), (int) Math.rint(y));
				ScatterPlotPanel.this.draggedArea.setPressedPosition(e.getX(),
						e.getY());

			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {

			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {
			}
		}

		public void mouseReleased(MouseEvent e) {

			int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {

				final GUIFile startFile = IDIndexMap.instance()
						.getFile(startIndex);
				final int startFileOffset = FileOffsetData.instance().get(
						startFile);

				final double scaleRatio = getScaleRatio();

				double x = 0;
				double y = 0;

				if (splitDrawing) {

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);
					final double splitPosition = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio;

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					if (e.getX() < splitPosition) {

						x = e.getX() * scaleRatio + startFileOffset;

					} else if (splitPosition < e.getX()) {

						x = (e.getX() - splitPosition) * scaleRatio
								+ restartFileOffset;
					}

				} else {

					x = e.getX() * scaleRatio + startFileOffset;
				}

				if (splitDrawing) {

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);
					final double splitPosition = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio;

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					if (e.getY() < splitPosition) {

						y = e.getY() * scaleRatio + startFileOffset;

					} else if (splitPosition < e.getY()) {

						y = (e.getY() - splitPosition) * scaleRatio
								+ restartFileOffset;
					}

				} else {

					y = e.getY() * scaleRatio + startFileOffset;
				}

				if (ScatterPlotPanel.this.zoomMode) {

					ScatterPlotPanel.this.zoomedArea.setReleasedToken(
							(int) Math.rint(x), (int) Math.rint(y));
					ScatterPlotPanel.this.zoomedArea.setReleasedPosition(
							e.getX(), e.getY());
					if (ScatterPlotPanel.this.isValidZoomedArea()) {
						ScatterPlotPanel.this.zoomUp();
					}
					ScatterPlotPanel.this.zoomMode = false;
				}

				ScatterPlotPanel.this.draggedArea.clear();

			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {
			}
		}

		public void mouseClicked(MouseEvent e) {

			int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {

				final GUIFile startFile = IDIndexMap.instance()
						.getFile(startIndex);
				final int startFileOffset = FileOffsetData.instance().get(
						startFile);

				final double scaleRatio = getScaleRatio();

				double x = 0;
				double y = 0;

				if (splitDrawing) {

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);
					final double splitPosition = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio;

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					if (e.getX() < splitPosition) {

						x = e.getX() * scaleRatio + startFileOffset;

					} else if (splitPosition < e.getX()) {

						x = (e.getX() - splitPosition) * scaleRatio
								+ restartFileOffset;
					}

				} else {

					x = e.getX() * scaleRatio + startFileOffset;
				}

				if (splitDrawing) {

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);
					final double splitPosition = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio;

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					if (e.getY() < splitPosition) {

						y = e.getY() * scaleRatio + startFileOffset;

					} else if (splitPosition < e.getY()) {

						y = (e.getY() - splitPosition) * scaleRatio
								+ restartFileOffset;
					}

				} else {

					y = e.getY() * scaleRatio + startFileOffset;
				}

				int low = 0;
				int high = GUIFileManager.instance().getFileCount();
				int horizontalElementIndex = IDIndexMap.instance()
						.searchFileIndex((int) Math.rint(x), low, high);
				int verticalElementIndex = IDIndexMap.instance()
						.searchFileIndex((int) Math.rint(y), low, high);
				final GUIFile horizontalFile = IDIndexMap.instance()
						.getFile(horizontalElementIndex);
				final GUIFile verticalFile = IDIndexMap.instance()
						.getFile(verticalElementIndex);

				SelectedEntities.<GUIFile> getInstance(HORIZONTAL_FILE).set(
						horizontalFile, ScatterPlotPanel.this);
				SelectedEntities.<GUIFile> getInstance(VERTICAL_FILE).set(
						verticalFile, ScatterPlotPanel.this);

			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {
			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {

				ScatterPlotPanel.this.scatterPlotPopupMenu.show(
						e.getComponent(), e.getX(), e.getY());
			}
		}

		public void mouseExited(MouseEvent e) {
			ScatterPlotPanel.this.fileNameDisplayPanel.update("", "");
		}

		public void mouseEntered(MouseEvent e) {
		}
	}

	class MouseMotionEventHandler implements MouseMotionListener {

		public void mouseDragged(MouseEvent e) {

			int modifier = e.getModifiers();

			if ((modifier & MouseEvent.BUTTON1_MASK) != 0) {

				final int length = ScatterPlotPanel.this.getLowerLength() - 1;
				if ((length <= e.getX()) || (length <= e.getY())) {
					return;
				}

				final GUIFile startFile = IDIndexMap.instance()
						.getFile(startIndex);
				final int startFileOffset = FileOffsetData.instance().get(
						startFile);

				final double scaleRatio = getScaleRatio();

				double x = 0;
				double y = 0;

				if (splitDrawing) {

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);
					final double splitPosition = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio;

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					if (e.getX() < splitPosition) {

						x = e.getX() * scaleRatio + startFileOffset;

					} else if (splitPosition < e.getX()) {

						x = (e.getX() - splitPosition) * scaleRatio
								+ restartFileOffset;
					}

				} else {

					x = e.getX() * scaleRatio + startFileOffset;
				}

				if (splitDrawing) {

					final GUIFile interruptFile = IDIndexMap.instance()
							.getFile(interruptIndex);
					final int interruptFileOffset = FileOffsetData.instance()
							.get(interruptFile);
					final double splitPosition = (interruptFileOffset
							+ interruptFile.loc - startFileOffset)
							/ scaleRatio;

					final GUIFile restartFile = IDIndexMap.instance()
							.getFile(restartIndex);
					final int restartFileOffset = FileOffsetData.instance()
							.get(restartFile);

					if (e.getY() < splitPosition) {

						y = e.getY() * scaleRatio + startFileOffset;

					} else if (splitPosition < e.getY()) {

						y = (e.getY() - splitPosition) * scaleRatio
								+ restartFileOffset;
					}

				} else {

					y = e.getY() * scaleRatio + startFileOffset;
				}

				int fileNumber = GUIFileManager.instance().getFileCount();
				int low = 0;
				int high = fileNumber - 1;
				int horizontalElementIndex = IDIndexMap.instance()
						.searchFileIndex((int) Math.rint(x), low, high);
				int verticalElementIndex = IDIndexMap.instance()
						.searchFileIndex((int) Math.rint(y), low, high);

				if ((0 <= horizontalElementIndex)
						&& (horizontalElementIndex < fileNumber)
						&& (0 <= verticalElementIndex)
						&& (verticalElementIndex < fileNumber)) {

					GUIFile horizontalElement = IDIndexMap.instance()
							.getFile(horizontalElementIndex);
					GUIFile verticalElement = IDIndexMap.instance()
							.getFile(verticalElementIndex);
					String horizontalFileName = horizontalElement.path + "("
							+ horizontalElement.groupID + "."
							+ horizontalElement.fileID + ")";
					String verticalFileName = verticalElement.path + "("
							+ verticalElement.groupID + "."
							+ verticalElement.fileID + ")";

					ScatterPlotPanel.this.fileNameDisplayPanel.update(
							horizontalFileName, verticalFileName);

				} else {
					ScatterPlotPanel.this.fileNameDisplayPanel.update("", "");
				}

				ScatterPlotPanel.this.draggedArea.setDraggedToken(
						(int) Math.rint(x), (int) Math.rint(y));
				ScatterPlotPanel.this.draggedArea.setDraggedPosition(e.getX(),
						e.getY());
				ScatterPlotPanel.this.repaint();

			} else if ((modifier & MouseEvent.BUTTON2_MASK) != 0) {

			} else if ((modifier & MouseEvent.BUTTON3_MASK) != 0) {
			}
		}

		public void mouseMoved(MouseEvent e) {

			int length = ScatterPlotPanel.this.getLowerLength();
			if ((length <= e.getX()) || (length <= e.getY())) {
				return;
			}

			final GUIFile startFile = IDIndexMap.instance().getFile(startIndex);
			final int startFileOffset = FileOffsetData.instance()
					.get(startFile);

			final double scaleRatio = getScaleRatio();

			double x = 0;
			double y = 0;

			if (splitDrawing) {

				final GUIFile interruptFile = IDIndexMap.instance()
						.getFile(interruptIndex);
				final int interruptFileOffset = FileOffsetData.instance().get(
						interruptFile);
				final double splitPosition = (interruptFileOffset
						+ interruptFile.loc - startFileOffset)
						/ scaleRatio;

				final GUIFile restartFile = IDIndexMap.instance()
						.getFile(restartIndex);
				final int restartFileOffset = FileOffsetData.instance().get(
						restartFile);

				if (e.getX() < splitPosition) {

					x = e.getX() * scaleRatio + startFileOffset;

				} else if (splitPosition < e.getX()) {

					x = (e.getX() - splitPosition) * scaleRatio
							+ restartFileOffset;
				}

			} else {

				x = e.getX() * scaleRatio + startFileOffset;
			}

			if (splitDrawing) {

				final GUIFile interruptFile = IDIndexMap.instance()
						.getFile(interruptIndex);
				final int interruptFileOffset = FileOffsetData.instance().get(
						interruptFile);
				final double splitPosition = (interruptFileOffset
						+ interruptFile.loc - startFileOffset)
						/ scaleRatio;

				final GUIFile restartFile = IDIndexMap.instance()
						.getFile(restartIndex);
				final int restartFileOffset = FileOffsetData.instance().get(
						restartFile);

				if (e.getY() < splitPosition) {

					y = e.getY() * scaleRatio + startFileOffset;

				} else if (splitPosition < e.getY()) {

					y = (e.getY() - splitPosition) * scaleRatio
							+ restartFileOffset;
				}

			} else {

				y = e.getY() * scaleRatio + startFileOffset;
			}

			int fileNumber = GUIFileManager.instance().getFileCount();
			int low = 0;
			int high = fileNumber - 1;
			int horizontalElementIndex = IDIndexMap.instance().searchFileIndex(
					(int) Math.rint(x), low, high);
			int verticalElementIndex = IDIndexMap.instance().searchFileIndex(
					(int) Math.rint(y), low, high);

			if ((0 <= horizontalElementIndex)
					&& (horizontalElementIndex < fileNumber)
					&& (0 <= verticalElementIndex)
					&& (verticalElementIndex < fileNumber)) {

				GUIFile horizontalElement = IDIndexMap.instance()
						.getFile(horizontalElementIndex);
				GUIFile verticalElement = IDIndexMap.instance()
						.getFile(verticalElementIndex);
				String horizontalFileName = horizontalElement.path + "("
						+ horizontalElement.groupID + "."
						+ horizontalElement.fileID + ")";
				String verticalFileName = verticalElement.path + "("
						+ verticalElement.groupID + "."
						+ verticalElement.fileID + ")";

				ScatterPlotPanel.this.fileNameDisplayPanel.update(
						horizontalFileName, verticalFileName);

			} else {
				ScatterPlotPanel.this.fileNameDisplayPanel.update("", "");
			}
		}
	}
}
