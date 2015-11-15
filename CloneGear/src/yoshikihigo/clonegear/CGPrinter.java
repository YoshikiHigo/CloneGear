package yoshikihigo.clonegear;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.StringTokenizer;

public class CGPrinter {

	private static int currentNumber = 0;

	public static void main(final String[] args) {

		CGConfig.initialize(args);

		final String result = CGConfig.getInstance().getRESULT();

		try (final BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(result),
						"JISAutoDetect"))) {

			while (true) {
				final String line = reader.readLine();
				if (null == line) {
					break;
				}
				final Clone clone = getClone(line);
				printClone(clone);
			}

		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static Clone getClone(final String line) {
		final StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		final int id = Integer.parseInt(tokenizer.nextToken());
		final String filepath = tokenizer.nextToken();
		final int fileLOC = Integer.parseInt(tokenizer.nextToken());
		final int startLine = Integer.parseInt(tokenizer.nextToken());
		final int endLine = Integer.parseInt(tokenizer.nextToken());
		return new Clone(id, filepath, fileLOC, startLine, endLine);
	}

	private static void printClone(final Clone clone) {
		try (final LineNumberReader reader = new LineNumberReader(
				new InputStreamReader(new FileInputStream(clone.filepath),
						"JISAutoDetect"))) {

			System.out
					.println("        ---------- start code fragment ----------");
			{
				final StringBuilder text = new StringBuilder();
				text.append("Clone ID: ");
				text.append(Integer.toString(clone.id));
				System.out.println(text.toString());
			}
			{
				final StringBuilder text = new StringBuilder();
				text.append("file: ");
				text.append(clone.filepath);

				final int base = 78;
				for (int i = 1; i * base < text.length(); i++) {
					text.insert(i * base, System.lineSeparator());
				}
				System.out.println(text.toString());
			}
			while (reader.ready()) {
				final String line = reader.readLine();
				final int number = reader.getLineNumber();
				if ((clone.startLine <= number) && (number <= clone.endLine)) {
					final StringBuilder text = new StringBuilder();
					text.append("   ");
					text.append(Integer.toString(number));
					text.append("\t|");
					text.append(line);
					System.out.println(text.toString());
				}
			}
			System.out
					.println("        ----------- end code fragment -----------");

		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	static class Clone {
		final int id;
		final String filepath;
		final int fileLOC;
		final int startLine;
		final int endLine;

		Clone(final int id, final String filepath, final int fileLOC,
				final int startLine, final int endLine) {
			this.id = id;
			this.filepath = filepath;
			this.fileLOC = fileLOC;
			this.startLine = startLine;
			this.endLine = endLine;
		}
	}
}
