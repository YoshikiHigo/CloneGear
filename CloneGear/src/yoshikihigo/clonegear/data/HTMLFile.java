package yoshikihigo.clonegear.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import yoshikihigo.clonegear.LANGUAGE;

public class HTMLFile extends SourceFile {

	public HTMLFile(final String path) {
		super(path);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.HTML;
	}

	public List<String> extractJavascript() {

		final List<String> texts = new ArrayList<>();

		try {

			final Source src = new Source(new InputStreamReader(
					new FileInputStream(this.path), "JISAutoDetect"));
			final List<Element> elements = src.getAllElements("script");
			for (final Element e : elements) {
				final String value = e.getAttributeValue("type");
				if ((null != value) && value.equals("text/javascript")) {
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

	private int[] getCurrentPosition(final StringBuilder builder) {
		final String newline = System.lineSeparator();
		final String text = builder.toString();

		int line = 1;
		int offset = 0;
		int lastMatchOffset = 0;
		while (true) {
			offset = text.indexOf(newline, offset);
			if (offset < 0) {
				break;
			}
			line++;
			lastMatchOffset = offset;
			offset++;
		}

		int lastLineOffset = 0 == lastMatchOffset ? 0 : lastMatchOffset
				+ newline.length();
		int column = text.substring(lastLineOffset).length();

		int[] position = new int[2];
		position[0] = line;
		position[1] = column;

		return position;
	}
}
