package yoshikihigo.clonegear.wizard;

import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class Progress extends JFrame {

	final JTextArea textArea;
	final JScrollPane scrollPane;

	public Progress() {

		super("Progress of clone detection");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			e.printStackTrace();
		}

		this.setSize(800, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.textArea = new JTextArea();
		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this.textArea);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.getContentPane().add(this.scrollPane);
	}

	static public class ProgressPrintStream extends PrintStream {

		final Progress progress;

		public ProgressPrintStream(final Progress progress) {
			super(System.out);
			this.progress = progress;
		}

		@Override
		public void print(final String str) {
			this.progress.textArea.append(str);
			final JScrollBar scrollBar = this.progress.scrollPane
					.getVerticalScrollBar();
			scrollBar.setValue(scrollBar.getMaximum());
		}

		@Override
		public void println(String str) {
			this.progress.textArea.append(str);
			this.progress.textArea.append(System.lineSeparator());
			final JScrollBar scrollBar = this.progress.scrollPane
					.getVerticalScrollBar();
			scrollBar.setValue(scrollBar.getMaximum());
		}
	}
}
