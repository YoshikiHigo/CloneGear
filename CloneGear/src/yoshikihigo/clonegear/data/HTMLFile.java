package yoshikihigo.clonegear.data;

import yoshikihigo.clonegear.LANGUAGE;

public class HTMLFile extends WebFile {

	public HTMLFile(final String path, final int groupID) {
		super(path, groupID);
	}

	@Override
	public LANGUAGE getLanguage() {
		return LANGUAGE.HTML;
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
