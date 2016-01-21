package yoshikihigo.clonegear.gui.view.visual.sourcecode;

import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Element;

import yoshikihigo.clonegear.gui.MessagePrinter;
import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.visual.DIRECTION;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;

public class SourceCodeWindow extends JTextArea implements ViewColors,
		Observer, VisualViewInterface {

	private static final int TAB_SIZE = 4;

	private final SourceCodeUI sourceCodeUI;

	final public JScrollPane scrollPane;

	private final DIRECTION direction;

	public SourceCodeWindow(final DIRECTION direction) {

		super();

		this.direction = direction;

		this.setTabSize(TAB_SIZE);

		final Insets margin = new Insets(5, 50, 5, 5);
		this.setMargin(margin);
		this.sourceCodeUI = new SourceCodeUI(this, margin);

		this.setUI(null);
		this.setText("");

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);

		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	}

	private void readFile(final GUIFile file) {
		try {
			final List<String> lines = Files.readAllLines(Paths.get(file.path),
					StandardCharsets.UTF_8);
			lines.stream().forEach(line -> {
				this.append(line);
				this.append(System.lineSeparator());
			});
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void addHighLight(final List<GUIClone> codeFragments) {

		final int threshold = RNRValue.getInstance(RNR).get();
		final boolean uninterestingClonesDisplay = UninterestingClonesDisplay
				.getInstance(UNINTERESTING).isDisplay();

		final DefaultHighlightPainter uninterestingClonePainter = new DefaultHighlightPainter(
				VISUAL_SOURCECODEVIEW_UNINTERESTING_CODECLONE_COLOR);
		final DefaultHighlightPainter practicalClonePainter;
		switch (this.direction) {
		case HORIZONTAL:
			practicalClonePainter = new DefaultHighlightPainter(
					VISUAL_HORIZONTAL_COLOR);
			break;
		case VERTICAL:
			practicalClonePainter = new DefaultHighlightPainter(
					VISUAL_VERTICAL_COLOR);
			break;
		default:
			practicalClonePainter = null;
		}

		for (final GUIClone codeFragment : codeFragments) {

			final int fromLine = codeFragment.fromLine;
			final int toLine = codeFragment.toLine + 1;
			final int rnr = codeFragment.getRNR();

			try {

				int fromOffset = 0;
				int toOffset = 0;
				if (0 < fromLine) {
					fromOffset = super.getLineStartOffset(fromLine - 1);
				} else if (0 == fromLine) {
					fromOffset = 0;
				}

				if (0 < toLine) {
					toOffset = super.getLineStartOffset(toLine - 1);
				} else if (0 == toLine) {
					toOffset = 0;
				}

				if (rnr < threshold) {
					if (uninterestingClonesDisplay) {
						this.getHighlighter().addHighlight(fromOffset,
								toOffset, uninterestingClonePainter);
					}
				} else {
					this.getHighlighter().addHighlight(fromOffset, toOffset,
							practicalClonePainter);
				}

			} catch (BadLocationException e) {
				MessagePrinter.ERR.println(e.getMessage());
			}
		}
	}

	public void init() {
	}

	public void update(Observable o, Object arg) {

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(HORIZONTAL_FILE)) {

				switch (this.direction) {
				case VERTICAL:
					break;

				case HORIZONTAL:

					this.setUI(null);
					this.setText("");

					if (selectedEntities.isSet()) {
						this.setUI(this.sourceCodeUI);

						final GUIFile file = (GUIFile) selectedEntities.get()
								.get(0);
						this.readFile(file);

						final List<GUIClone> codeFragments = GUICloneManager
								.instance().getFileClones(file);
						this.addHighLight(codeFragments);

						this.setCaretPosition(0);
					}

					this.setEditable(false);
					break;
				}

			} else if (selectedEntities.getLabel().equals(VERTICAL_FILE)) {

				switch (this.direction) {
				case HORIZONTAL:
					break;

				case VERTICAL:

					this.setUI(null);
					this.setText("");

					if (selectedEntities.isSet()) {
						this.setUI(this.sourceCodeUI);

						final GUIFile file = (GUIFile) selectedEntities.get()
								.get(0);
						this.readFile(file);

						final List<GUIClone> codeFragments = GUICloneManager
								.instance().getFileClones(file);
						this.addHighLight(codeFragments);

						this.setCaretPosition(0);
					}

					this.setEditable(false);
					break;
				}

			} else if (selectedEntities.getLabel().equals(CLONEPAIR)) {

				if (selectedEntities.isSet()) {
					final GUIClonePair clonePair = (GUIClonePair) selectedEntities
							.get().get(0);
					switch (this.direction) {
					case HORIZONTAL:
						this.displayCodeFragment(clonePair.left);
						break;
					case VERTICAL:
						this.displayCodeFragment(clonePair.right);
						break;
					}
				}
			}

		} else if ((o instanceof RNRValue)
				|| (o instanceof UninterestingClonesDisplay)) {

			switch (this.direction) {
			case HORIZONTAL:

				final SelectedEntities<GUIFile> horizontalSelectedFiles = SelectedEntities
						.<GUIFile> getInstance(HORIZONTAL_FILE);
				if (horizontalSelectedFiles.isSet()) {

					this.getHighlighter().removeAllHighlights();

					final GUIFile file = horizontalSelectedFiles.get().get(0);
					final List<GUIClone> codeFragments = GUICloneManager
							.instance().getFileClones(file);
					this.addHighLight(codeFragments);
				}
				break;

			case VERTICAL:

				final SelectedEntities<GUIFile> verticalSelectedFiles = SelectedEntities
						.<GUIFile> getInstance(VERTICAL_FILE);
				if (verticalSelectedFiles.isSet()) {

					this.getHighlighter().removeAllHighlights();

					final GUIFile file = verticalSelectedFiles.get().get(0);
					final List<GUIClone> codeFragments = GUICloneManager
							.instance().getFileClones(file);
					this.addHighLight(codeFragments);
				}
				break;
			}

		}
	}

	private void displayCodeFragment(final GUIClone codeFragment) {

		final Document doc = this.getDocument();
		final Element root = doc.getDefaultRootElement();
		try {
			final Element elem = root.getElement(Math.max(1,
					codeFragment.fromLine - 2));
			if (null != elem) {
				final Rectangle rect = this.modelToView(elem.getStartOffset());
				final Rectangle vr = this.scrollPane.getViewport()
						.getViewRect();
				if ((null != rect) && (null != vr)) {
					rect.setSize(10, vr.height);
					this.scrollRectToVisible(rect);
				}
				this.setCaretPosition(elem.getStartOffset());
			}
		}

		catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}
}
