package yoshikihigo.clonegear.gui.view.metric.source.code;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Element;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;

public class SourceCodeWindow extends javax.swing.JTextArea implements
		CloneColor, CodeColor {

	public SourceCodeWindow(final GUIClone clone) {

		super();

		final Insets margin = new Insets(5, 50, 5, 5);
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

		this.readFile(clone);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				final int button = e.getButton();
				final int clickCount = e.getClickCount();

				switch (button) {
				case MouseEvent.BUTTON1:
					switch (clickCount) {
					case 1:
						break;
					case 2:
						displayCodeFragment(clone);
						break;
					default:
					}
					break;
				case MouseEvent.BUTTON2:
					break;
				case MouseEvent.BUTTON3:
					break;
				default:
				}
			}
		});
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	private void readFile(final GUIClone clone) {

		try {

			{
				final String path = clone.file.path;
				final StringBuffer buffer = new StringBuffer();
				final InputStreamReader reader = new InputStreamReader(
						new FileInputStream(path), "JISAutoDetect");
				for (int c = reader.read(); c != -1; c = reader.read()) {
					buffer.append((char) c);
				}
				reader.close();
				this.append(buffer.toString());
				reader.close();
			}

			int fromOffset = 0;
			int toOffset = 0;
			final int fromLine = clone.fromLine;
			final int toLine = clone.toLine;

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

			final GUICloneSet cloneSet = GUICloneManager.SINGLETON
					.getCloneSet(clone);
			switch (cloneSet.getRAD()) {
			case 0:
				DefaultHighlightPainter densePainter = new DefaultHighlightPainter(
						DENSE_CODE_CLONE_COLOR);
				this.getHighlighter().addHighlight(fromOffset, toOffset,
						densePainter);
				break;

			case 1:
				DefaultHighlightPainter middlePainter = new DefaultHighlightPainter(
						MIDDLE_CODE_CLONE_COLOR);
				this.getHighlighter().addHighlight(fromOffset, toOffset,
						middlePainter);
				break;

			default:
				DefaultHighlightPainter scatteredPainter = new DefaultHighlightPainter(
						SCATTERED_CODE_CLONE_COLOR);
				this.getHighlighter().addHighlight(fromOffset, toOffset,
						scatteredPainter);
				break;
			}

		} catch (BadLocationException e) {
			System.err.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void displayCodeFragment(final GUIClone codeFragment) {

		final Document doc = this.getDocument();
		final Element root = doc.getDefaultRootElement();
		try {
			final Element elem = root.getElement(Math.max(1,
					codeFragment.fromLine - 2));
			final Rectangle rect = this.modelToView(elem.getStartOffset());
			final Rectangle vr = this.getScrollPane().getViewport()
					.getViewRect();
			if ((null != rect) && (null != vr)) {
				rect.setSize(10, vr.height);
				this.scrollRectToVisible(rect);
				this.setCaretPosition(elem.getStartOffset());
			}
		} catch (BadLocationException e) {
			System.err.println(e.getMessage());
		}
	}

	final static private int TAB_SIZE = 4;

	final private SourceCodeUI sourceCodeUI;

	final private JScrollPane scrollPane;
}
