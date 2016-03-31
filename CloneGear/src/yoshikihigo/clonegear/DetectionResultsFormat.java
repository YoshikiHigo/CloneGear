package yoshikihigo.clonegear;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import yoshikihigo.clonegear.data.ClonePair;
import yoshikihigo.clonegear.data.CloneSet;
import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.data.file.GUIFile;

public class DetectionResultsFormat {

	static public void writer(final List<SourceFile> files,
			final List<CloneSet> clonesets, final String path) {

		try (final PrintWriter writer = null != path ? new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(path), "UTF-8"))
				: new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"))) {

			final Set<String> checked = new HashSet<>();
			for (final SourceFile file : files) {
				if (checked.contains(file.path)) {
					continue;
				}
				final String line = makeFileLine(file);
				writer.println(line);
				checked.add(file.path);
			}

			clonesets.stream().sorted().forEach(cloneset -> {
				cloneset.getClonepairs().stream().forEach(clonepair -> {
					final String line = makeCloneLine(cloneset.id, clonepair);
					writer.println(line);
				});
			});
		}

		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	static private String makeFileLine(final SourceFile file) {
		final StringBuilder text = new StringBuilder();
		text.append("FILE\t");
		text.append(file.groupID);
		text.append("\t");
		text.append(file.path);
		text.append("\t");
		text.append(file.getLOC());
		return text.toString();
	}

	static private String makeCloneLine(final int clonesetID,
			final ClonePair clonepair) {
		final StringBuilder text = new StringBuilder();
		text.append("CLONE\t");
		text.append(clonesetID);
		text.append("\t");
		text.append(makeCloneLine(clonepair.left));
		text.append("\t");
		text.append(makeCloneLine(clonepair.right));
		return text.toString();
	}

	static private String makeCloneLine(final ClonedFragment clone) {
		final StringBuilder text = new StringBuilder();
		text.append(clone.file.path);
		text.append("\t");
		text.append(clone.getFromLine());
		text.append("\t");
		text.append(clone.getToLine());
		text.append("\t");
		text.append(round(clone.getRNR()));
		return text.toString();
	}

	static private float round(final float value) {
		final BigDecimal d = new BigDecimal(value);
		return d.setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	static public void read(final String path, final List<GUIFile> files,
			final List<GUIClonePair> clonepairs) {

		try {
			final List<String> lines = Files.readAllLines(Paths.get(path),
					StandardCharsets.UTF_8);

			lines.stream().filter(line -> line.startsWith("FILE"))
					.forEach(line -> {
						final GUIFile file = makeFile(line);
						files.add(file);
					});

			lines.stream().filter(line -> line.startsWith("CLONE"))
					.forEach(line -> {
						final GUIClonePair pair = makeClonepair(line);
						clonepairs.add(pair);
					});

		} catch (final IOException e) {
			throw new IllegalStateException("unable to read input file.");
		}
	}

	static private GUIFile makeFile(final String line) {
		final String[] tokens = line.split("\t");
		if (4 != tokens.length) {
			throw new IllegalStateException("input file has invalid format.");
		}
		final int groupID = Integer.parseInt(tokens[1]);
		final String path = tokens[2];
		final int loc = Integer.parseInt(tokens[3]);
		return GUIFile.getGUIFile(groupID, path, loc);
	}

	static private GUIClonePair makeClonepair(final String line) {
		final String[] tokens = line.split("\t");
		if (10 != tokens.length) {
			throw new IllegalStateException("input file has invalid format.");
		}
		final int clonesetID = Integer.parseInt(tokens[1]);
		final String leftPath = tokens[2];
		final int leftFromLine = Integer.parseInt(tokens[3]);
		final int leftToLine = Integer.parseInt(tokens[4]);
		final float leftCloneRNR = Float.parseFloat(tokens[5]);
		final String rightPath = tokens[6];
		final int rightFromLine = Integer.parseInt(tokens[7]);
		final int rightToLine = Integer.parseInt(tokens[8]);
		final float rightCloneRNR = Float.parseFloat(tokens[9]);

		final GUIFile leftFile = GUIFile.getGUIFile(leftPath);
		final GUIClone leftClone = new GUIClone(clonesetID, leftFile,
				leftFromLine, leftToLine, leftCloneRNR);
		final GUIFile rightFile = GUIFile.getGUIFile(rightPath);
		final GUIClone rightClone = new GUIClone(clonesetID, rightFile,
				rightFromLine, rightToLine, rightCloneRNR);

		final GUIClonePair clonepair = new GUIClonePair(clonesetID, leftClone,
				rightClone);
		return clonepair;
	}
}
