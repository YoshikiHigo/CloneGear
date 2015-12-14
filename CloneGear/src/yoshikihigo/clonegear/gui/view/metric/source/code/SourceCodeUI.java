package yoshikihigo.clonegear.gui.view.metric.source.code;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.View;

class SourceCodeUI extends BasicTextAreaUI {

	class SourceCodePlainView extends PlainView implements CodeColor {

		final private JTextArea textArea;
		final private Insets margin;

		public SourceCodePlainView(final Element elem,
				final JTextArea textArea, final Insets margin) {
			super(elem);
			this.textArea = textArea;
			this.margin = margin;
		}

		@Override
		protected int drawUnselectedText(final Graphics g, final int x,
				final int y, final int p0, final int p1)
				throws BadLocationException {

			final int endColumn = super.drawUnselectedText(g, x, y, p0, p1);

			int start = p0;
			final String buffer = this.textArea.getDocument().getText(p0,
					p1 - p0 + 1);
			final StringTokenizer stringTokenizer = new StringTokenizer(buffer,
					" ,;=+*-/()[]{}\n\t", true);

			while (stringTokenizer.hasMoreTokens()) {

				final String token = stringTokenizer.nextToken();

				if (RESERVED_WORD.contains(token)) {
					this.drawLangToken(g, getDrawX(start), y, token);
				}
				start += token.length();
			}

			return endColumn;
		}

		private void drawLangToken(final Graphics g, final int x, final int y,
				final String token) {

			g.setPaintMode();
			Color backupColor = g.getColor();
			g.setColor(LANG_TOKEN_COLOR);
			g.drawString(token, x, y);
			g.setColor(backupColor);
		}

		private int getDrawX(final int pos) {

			int drawX = 0;

			try {
				drawX = this.textArea.modelToView(pos).x;
			} catch (BadLocationException e) {
				System.err.println(e.getMessage());
			}

			return drawX;
		}

		@Override
		protected void drawLine(final int lineIndex, final Graphics g,
				final int x, final int y) {

			super.drawLine(lineIndex, g, x, y);
			this.drawLineNumber(g, lineIndex, y);
		}

		private void drawLineNumber(final Graphics g, final int lineIndex,
				final int y) {

			// set overwrite mode
			g.setPaintMode();

			// backup now color
			Color backupColor = g.getColor();

			// set line number color
			g.setColor(LINENUM_COLOR);

			// draw separator between line number and text area
			g.drawLine(this.margin.left - 5,
					y - g.getFontMetrics().getHeight(), this.margin.left - 5, y);

			String lineString = String.valueOf(lineIndex + 1);
			int x_location = this.margin.left - 8
					- g.getFontMetrics().stringWidth(lineString);

			g.drawString(lineString, x_location, y);

			g.setColor(backupColor);
		}
	}

	final private JTextArea textArea;

	final Insets margin;

	SourceCodeUI(final JTextArea textArea, final Insets margin) {

		super();
		this.textArea = textArea;
		this.margin = margin;
	}

	@Override
	public View create(final Element elem) {
		return new SourceCodePlainView(elem, this.textArea, this.margin);
	}

	private static final Set<String> RESERVED_WORD = new HashSet<>();

	static {
		RESERVED_WORD.add("abstract");
		RESERVED_WORD.add("boolean");
		RESERVED_WORD.add("break");
		RESERVED_WORD.add("byte");
		RESERVED_WORD.add("case");
		RESERVED_WORD.add("catch");
		RESERVED_WORD.add("char");
		RESERVED_WORD.add("class");
		RESERVED_WORD.add("const");
		RESERVED_WORD.add("continue");
		RESERVED_WORD.add("default");
		RESERVED_WORD.add("do");
		RESERVED_WORD.add("double");
		RESERVED_WORD.add("else");
		RESERVED_WORD.add("extends");
		RESERVED_WORD.add("final");
		RESERVED_WORD.add("finally");
		RESERVED_WORD.add("float");
		RESERVED_WORD.add("for");
		RESERVED_WORD.add("goto");
		RESERVED_WORD.add("if");
		RESERVED_WORD.add("implements");
		RESERVED_WORD.add("import");
		RESERVED_WORD.add("instanceof");
		RESERVED_WORD.add("int");
		RESERVED_WORD.add("interface");
		RESERVED_WORD.add("long");
		RESERVED_WORD.add("native");
		RESERVED_WORD.add("new");
		RESERVED_WORD.add("package");
		RESERVED_WORD.add("private");
		RESERVED_WORD.add("protected");
		RESERVED_WORD.add("public");
		RESERVED_WORD.add("return");
		RESERVED_WORD.add("short");
		RESERVED_WORD.add("static");
		RESERVED_WORD.add("strictfp");
		RESERVED_WORD.add("super");
		RESERVED_WORD.add("switch");
		RESERVED_WORD.add("synchronized");
		RESERVED_WORD.add("this");
		RESERVED_WORD.add("throw");
		RESERVED_WORD.add("throws");
		RESERVED_WORD.add("transient");
		RESERVED_WORD.add("try");
		RESERVED_WORD.add("void");
		RESERVED_WORD.add("volatile");
		RESERVED_WORD.add("while");
	}
}
