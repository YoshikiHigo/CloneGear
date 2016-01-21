package yoshikihigo.clonegear.gui.view.quantity.sourcecode;

import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Element;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class SourceCodeWindow extends JTextArea implements ViewColors,
		Observer, QuantitativeViewInterface {

	private static final int TAB_SIZE = 4;

	final private SourceCodeUI sourceCodeUI;

	final private JScrollPane scrollPane;

	public SourceCodeWindow() {

		super();

		Insets margin = new Insets(5, 50, 5, 5);
		this.setMargin(margin);

		this.sourceCodeUI = new SourceCodeUI(this, margin);

		this.setUI(this.sourceCodeUI);
		this.setTabSize(TAB_SIZE);

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

	private void addHighlight(final Collection<GUIClone> clones) {

		try {

			final int threshold = RNRValue.getInstance(RNR).get();
			final boolean display = UninterestingClonesDisplay.getInstance(
					UNINTERESTING).isDisplay();

			final DefaultHighlightPainter densePainter = new DefaultHighlightPainter(
					QUANTITATIVE_SOURCECODEVIEW_DENSE_CLONE_COLOR);
			final DefaultHighlightPainter middlePainter = new DefaultHighlightPainter(
					QUANTITATIVE_SOURCECODEVIEW_MIDDLE_CLONE_COLOR);
			final DefaultHighlightPainter scatteredPainter = new DefaultHighlightPainter(
					QUANTITATIVE_SOURCECODEVIEW_SCATTERED_CLONE_COLOR);
			final DefaultHighlightPainter uninterestingPainter = new DefaultHighlightPainter(
					QUANTITATIVE_SOURCECODEVIEW_UNINTERESTING_CLONE_COLOR);

			for (final GUIClone clone : clones) {

				int fromOffset = 0;
				int toOffset = 0;

				final int fromLine = clone.fromLine;
				final int toLine = clone.toLine + 1;
				final int rnr = clone.getRNR();

				if (0 < fromLine) {
					fromOffset = super.getLineStartOffset(fromLine - 1);
				} else if (0 == fromLine) {
					fromOffset = 0;
				} else {
					System.err.println("Error Happened in SourceCodeWindow.");
				}

				if (0 < toLine) {
					toOffset = super.getLineStartOffset(toLine - 1);
				} else if (0 == toLine) {
					toOffset = 0;
				} else {
					System.err.println("Error Happened in SourceCodeWindow.");
				}

				if (rnr < threshold) {

					if (display) {
						this.getHighlighter().addHighlight(fromOffset,
								toOffset, uninterestingPainter);
					}

				} else {

					GUICloneSet cloneSet = GUICloneManager.instance()
							.getCloneSet(clone);
					switch (cloneSet.getRAD()) {
					case 0:
						this.getHighlighter().addHighlight(fromOffset,
								toOffset, densePainter);
						break;

					case 1:
						this.getHighlighter().addHighlight(fromOffset,
								toOffset, middlePainter);
						break;

					default:
						this.getHighlighter().addHighlight(fromOffset,
								toOffset, scatteredPainter);
						break;
					}
				}
			}

		} catch (BadLocationException e) {
			System.err.println(e.getMessage());
		}
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	public void update(Observable o, Object arg) {

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;

			if (selectedEntities.getLabel().equals(SELECTED_FILE)) {

				this.setText("");
				this.setUI(null);

				if (selectedEntities.isSet()) {
					this.setUI(this.sourceCodeUI);
					final GUIFile file = (GUIFile) selectedEntities.get()
							.get(0);
					this.readFile(file);
					this.setCaretPosition(0);

					final List<GUIClone> codeFragments = GUICloneManager.instance()
							.getFileClones(file);
					this.addHighlight(codeFragments);
				}

				this.repaint();

			} else if (selectedEntities.getLabel().equals(RELATED_FILE)) {

			} else if (selectedEntities.getLabel().equals(GROUP)) {
				this.setText("");
				this.setUI(null);
				this.repaint();

			} else if (selectedEntities.getLabel().equals(CODEFRAGMENT)) {

				if (selectedEntities.isSet()) {
					final GUIClone codeFragmnet = (GUIClone) selectedEntities
							.get().get(0);
					this.displayCodeFragment(codeFragmnet);
				}
			}

		} else if ((o instanceof RNRValue)
				|| (o instanceof UninterestingClonesDisplay)) {

			if (SelectedEntities.getInstance(SELECTED_FILE).isSet()) {
				this.getHighlighter().removeAllHighlights();
				final GUIFile file = SelectedEntities
						.<GUIFile> getInstance(SELECTED_FILE).get().get(0);
				final List<GUIClone> codeFragments = GUICloneManager.instance()
						.getFileClones(file);
				this.addHighlight(codeFragments);
			}

			this.repaint();
		}
	}

	public void displayCodeFragment(final GUIClone codeFragment) {

		final Document doc = this.getDocument();
		final Element root = doc.getDefaultRootElement();
		try {
			Element elem = root.getElement(Math.max(1,
					codeFragment.fromLine - 2));
			Rectangle rect = this.modelToView(elem.getStartOffset());
			Rectangle vr = this.getScrollPane().getViewport().getViewRect();
			rect.setSize(10, vr.height);
			this.scrollRectToVisible(rect);
			this.setCaretPosition(elem.getStartOffset());
		} catch (BadLocationException e) {
			System.err.println(e.getMessage());
		}
	}
}
