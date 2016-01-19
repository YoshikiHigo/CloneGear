package yoshikihigo.clonegear.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import yoshikihigo.clonegear.CGConfig;
import yoshikihigo.clonegear.FileUtility;
import yoshikihigo.clonegear.StringUtility;

public class Wizard extends JFrame {

	static private String lastDirectory = null;

	private boolean finished;
	final private JTextField tokenField;
	final private JTextField gapField;
	final private JTextField outputField;
	final private FileTable fileTable;
	final private JCheckBox crossGroupCheckBox;
	final private JCheckBox crossFileCheckBox;
	final private JCheckBox withinFileChechBox;
	final private JTextField threadField;
	final private JCheckBox verboseCheckBox;
	final private JCheckBox geminiCheckBox;
	final private JCheckBox onlyGeminiCheckBox;
	final private JButton detectButton;
	final private JButton quitButton;

	public Wizard() {

		super("CloneGear --setting for clone detection--");

		this.finished = false;
		this.setSize(800, 700);

		this.tokenField = new JTextField(Integer.toString(CGConfig
				.getInstance().getTHRESHOLD()), 3);
		final JLabel tokenLabel = new JLabel(
				"the number of tokens for minimal clones");
		final JPanel tokenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tokenPanel.add(this.tokenField);
		tokenPanel.add(tokenLabel);
		tokenPanel.setBorder(new LineBorder(Color.black));

		this.gapField = new JTextField(Integer.toString(CGConfig.getInstance()
				.getGAP()), 3);
		final JLabel gapLabel = new JLabel(
				"the size of maximal gap for Type-3 clones");
		final JPanel gapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		gapPanel.add(this.gapField);
		gapPanel.add(gapLabel);
		gapPanel.setBorder(new LineBorder(Color.black));

		final JLabel outputLabel = new JLabel("detection results: ");
		this.outputField = new JTextField(
				CGConfig.getInstance().hasRESULT() ? CGConfig.getInstance()
						.getRESULT() : "");
		final JButton referButton = new JButton("refer");
		final JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.add(outputLabel, BorderLayout.WEST);
		outputPanel.add(this.outputField, BorderLayout.CENTER);
		outputPanel.add(referButton, BorderLayout.EAST);
		outputPanel.setBorder(new LineBorder(Color.black));

		final JPanel topPanel = new JPanel(new GridLayout(3, 1, 2, 2));
		topPanel.add(tokenPanel);
		topPanel.add(gapPanel);
		topPanel.add(outputPanel);

		final JLabel targetFileLabel = new JLabel("target files: ");
		final JButton directoryButton = new JButton(
				"directory including target files");
		final JButton listButton = new JButton("file listing target files");
		final JPanel targetFilePanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		targetFilePanel.add(targetFileLabel);
		targetFilePanel.add(directoryButton);
		targetFilePanel.add(listButton);

		this.fileTable = new FileTable();
		final JPanel middlePanel = new JPanel(new BorderLayout());
		middlePanel.add(targetFilePanel, BorderLayout.NORTH);
		middlePanel.add(this.fileTable.scrollPane, BorderLayout.CENTER);
		middlePanel.setBorder(new LineBorder(Color.black));

		final JLabel crossWithinLabel = new JLabel("detection categories: ");
		this.crossGroupCheckBox = new JCheckBox("cross groups", CGConfig
				.getInstance().isCrossGroupDetection());
		this.crossFileCheckBox = new JCheckBox("cross files", CGConfig
				.getInstance().isCrossFileDetection());
		this.withinFileChechBox = new JCheckBox("within each file", CGConfig
				.getInstance().isWithinFileDetection());
		final JPanel crossWithinPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		crossWithinPanel.add(crossWithinLabel);
		crossWithinPanel.add(this.crossGroupCheckBox);
		crossWithinPanel.add(this.crossFileCheckBox);
		crossWithinPanel.add(this.withinFileChechBox);
		crossWithinPanel.setBorder(new LineBorder(Color.black));

		this.threadField = new JTextField(Integer.toString(CGConfig
				.getInstance().getTHREAD()), 3);
		final JLabel threadLabel = new JLabel(
				"the number of threads to user for clone detection");
		final JPanel threadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		threadPanel.add(this.threadField);
		threadPanel.add(threadLabel);
		threadPanel.setBorder(new LineBorder(Color.black));

		this.verboseCheckBox = new JCheckBox("verbose output", CGConfig
				.getInstance().isVERBOSE());
		final JPanel verbosePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		verbosePanel.add(this.verboseCheckBox);
		verbosePanel.setBorder(new LineBorder(Color.black));

		this.geminiCheckBox = new JCheckBox(
				"launch Gemini after finishing clone detection", CGConfig
						.getInstance().isGEMINI());
		this.onlyGeminiCheckBox = new JCheckBox(
				"use existing detection results", false);
		final JPanel geminiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		geminiPanel.add(this.geminiCheckBox);
		geminiPanel.add(new JLabel("("));
		geminiPanel.add(this.onlyGeminiCheckBox);
		geminiPanel.add(new JLabel(")"));
		geminiPanel.setBorder(new LineBorder(Color.black));

		this.detectButton = new JButton("Go");
		final JLabel detectLabel = new JLabel(
				"detect clones with the above settings");
		final JPanel detectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		detectPanel.add(this.detectButton);
		detectPanel.add(detectLabel);
		detectPanel.setBorder(new LineBorder(Color.black));

		this.quitButton = new JButton("Quit");
		final JLabel quitLabel = new JLabel("quit this program");
		final JPanel quitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		quitPanel.add(this.quitButton);
		quitPanel.add(quitLabel);
		quitPanel.setBorder(new LineBorder(Color.black));

		final JPanel bottomPanel = new JPanel(new GridLayout(6, 1, 2, 2));
		bottomPanel.add(crossWithinPanel);
		bottomPanel.add(threadPanel);
		bottomPanel.add(verbosePanel);
		bottomPanel.add(geminiPanel);
		bottomPanel.add(detectPanel);
		bottomPanel.add(quitPanel);

		final JPanel basePanel = new JPanel(new BorderLayout(2, 2));
		basePanel.add(topPanel, BorderLayout.NORTH);
		basePanel.add(middlePanel, BorderLayout.CENTER);
		basePanel.add(bottomPanel, BorderLayout.SOUTH);

		this.getContentPane().add(basePanel);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		directoryButton
				.addActionListener(e -> {
					final JFileChooser chooser = null == lastDirectory ? new JFileChooser()
							: new JFileChooser(lastDirectory);
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setMultiSelectionEnabled(true);
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"All analyzable files", "java", "c", "cpp", "py",
							"html", "js", "php", "jsp"));
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"Java files (*.java)", "java"));
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"C/C++ files (*.c, *.cpp)", "c", "cpp"));
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"Python (*.py)", "py"));
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"Web related files (*.html, *.js, *.php, *.jsp)",
							"html", "js", "php", "jsp"));
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"HTML files (*.html)", "html"));
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"Javascript files (*.js)", "js"));
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"PHP files (*.php)", "php"));
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"JSP files (*.jsp)", "jsp"));
					chooser.setDialogType(JFileChooser.OPEN_DIALOG);
					chooser.setDialogTitle("Directory including tareget files");
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

					if (JFileChooser.APPROVE_OPTION == chooser
							.showOpenDialog(this)) {
						final File[] roots = chooser.getSelectedFiles();
						final FileFilter filter = chooser.getFileFilter();
						final List<File> files = new ArrayList<>();
						Arrays.asList(roots).forEach(
								root -> getFiles(root, files, filter));
						this.fileTable.addFiles(files);
					}
				});
		listButton
				.addActionListener(e -> {
					final JFileChooser chooser = null == lastDirectory ? new JFileChooser()
							: new JFileChooser(lastDirectory);
					chooser.addChoosableFileFilter(new FileNameExtensionFilter(
							"Text files (*.txt)", "txt"));

					if (JFileChooser.APPROVE_OPTION == chooser
							.showOpenDialog(this)) {

						final String path = chooser.getSelectedFile()
								.getAbsolutePath();
						final List<String> lines = FileUtility.readFile(path);
						if (null == lines) {
							System.err.println("cannot read " + path);
							return;
						}

						final List<File> files = new ArrayList<>();
						for (final String line : lines) {
							if (StringUtility.isBlankLine(line)) {
								files.add(new File("-ns"));
							} else {
								files.add(new File(line));
							}
						}
						this.fileTable.addFiles(files);
					}
				});
		referButton
				.addActionListener(e -> {
					final JFileChooser chooser = null == lastDirectory ? new JFileChooser()
							: new JFileChooser(lastDirectory);
					final FileNameExtensionFilter filter = new FileNameExtensionFilter(
							"text files", "txt");
					chooser.setFileFilter(filter);
					chooser.setDialogType(JFileChooser.SAVE_DIALOG);
					chooser.setDialogTitle("file to store detection results");
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

					final int returnValue = chooser.showSaveDialog(this);
					switch (returnValue) {
					case JFileChooser.APPROVE_OPTION: {
						final String path = chooser.getSelectedFile()
								.getAbsolutePath();
						this.outputField.setText(path);
						lastDirectory = chooser.getCurrentDirectory()
								.getAbsolutePath();
						break;
					}
					case JFileChooser.CANCEL_OPTION:
					case JFileChooser.ERROR_OPTION: {
						break;
					}
					}
				});
		this.geminiCheckBox.addActionListener(e -> this.onlyGeminiCheckBox
				.setEnabled(this.geminiCheckBox.isSelected()));
		this.detectButton.addActionListener(e -> this.finished = true);
		this.quitButton.addActionListener(e -> System.exit(0));
	}

	public boolean isFinished() {
		return this.finished;
	}

	public String[] getConfiguration() {
		final List<String> configs = new ArrayList<>();

		configs.add("-thrld");
		configs.add(this.tokenField.getText());

		configs.add("-gap");
		configs.add(this.gapField.getText());

		configs.add("-list");
		configs.add(makeListFile(this.fileTable.getFilesWithSeparators()));

		configs.add("-result");
		configs.add(this.outputField.getText());

		configs.add("-cg");
		configs.add(this.crossGroupCheckBox.isSelected() ? "YES" : "NO");

		configs.add("-cf");
		configs.add(this.crossFileCheckBox.isSelected() ? "YES" : "NO");

		configs.add("-wf");
		configs.add(this.withinFileChechBox.isSelected() ? "YES" : "NO");

		configs.add("-thd");
		configs.add(this.threadField.getText());

		if (this.verboseCheckBox.isSelected()) {
			configs.add("-v");
		}

		if (this.geminiCheckBox.isSelected()) {
			configs.add("-g");
		}

		return configs.toArray(new String[0]);
	}

	private String makeListFile(final List<File> files) {

		File file = null;
		try {
			file = File.createTempFile("CloneGear", "TargetFiles");
			file.deleteOnExit();
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

		try (final BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file),
						StandardCharsets.ISO_8859_1))) {
			for (final File f : files) {
				if (!f.getName().equals("-ns")) {
					writer.write(f.getAbsolutePath());
				}
				writer.newLine();
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

		return file.getAbsolutePath();
	}

	private void getFiles(final File file, final List<File> list,
			final FileFilter filter) {

		if (file.isFile()) {
			if (filter.accept(file)) {
				list.add(file);
			}
		}

		else if (file.isDirectory()) {
			final File[] children = file.listFiles();
			Arrays.asList(children).stream().filter(child -> child.isFile())
					.forEach(child -> getFiles(child, list, filter));
			Arrays.asList(children).stream()
					.filter(child -> child.isDirectory())
					.forEach(child -> getFiles(child, list, filter));
		}
	}
}
