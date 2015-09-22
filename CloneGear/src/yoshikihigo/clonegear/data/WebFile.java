package yoshikihigo.clonegear.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

public abstract class WebFile extends SourceFile {

	protected WebFile(final String path) {
		super(path);
	}

	public List<String> extractJavascript() {

		final List<String> texts = new ArrayList<>();

		try {

			final Source src = new Source(new InputStreamReader(
					new FileInputStream(this.path), "JISAutoDetect"));
			final List<Element> elements = src.getAllElements("script");
			for (final Element e : elements) {
				if (this.isJavascript(e)) {
					final Segment content = e.getContent();
					final int startLine = content.getRowColumnVector().getRow();
					final int startColumn = content.getRowColumnVector()
							.getColumn();

					final StringBuilder builder = new StringBuilder();
					for (int line = 1; line < startLine; line++) {
						builder.append(System.lineSeparator());
					}
					for (int column = 1; column < startColumn; column++) {
						builder.append(' ');
					}
					builder.append(content.toString());
					texts.add(builder.toString());
				}
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

		return texts;
	}

	private boolean isJavascript(final Element e) {

		final String type = e.getAttributeValue("type");
		if ((null != type) && type.equals("text/javascript")) {
			return true;
		}

		final String language = e.getAttributeValue("language");
		if ((null != language) && language.equals("javascript")) {
			return true;
		}

		return false;
	}

	public List<String> getJSP() {

		final List<String> texts = new ArrayList<>();

		try {
			final Source src = new Source(new InputStreamReader(
					new FileInputStream(this.path), "JISAutoDetect"));
			final List<Element> elements = src.getAllElements();
			for (final Element e : elements) {
				final String text = e.toString();
				if (text.startsWith("<%") && !text.startsWith("<%@")
						&& text.endsWith("%>")) {

					final int startLine = e.getRowColumnVector().getRow();
					final int startColumn = e.getRowColumnVector().getColumn() + 2;

					final StringBuilder builder = new StringBuilder();
					for (int line = 1; line < startLine; line++) {
						builder.append(System.lineSeparator());
					}
					for (int column = 1; column < startColumn; column++) {
						builder.append(' ');
					}

					builder.append(text.substring(2, text.length() - 2));
					texts.add(builder.toString());
				}
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

		return texts;
	}
}
