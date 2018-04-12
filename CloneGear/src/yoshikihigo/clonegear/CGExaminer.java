package yoshikihigo.clonegear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.data.file.GUIFile;

public class CGExaminer {

	public static void main(final String[] args) {

		CGConfig.initialize(args);
		CGConfig config = CGConfig.getInstance();

		final String results = config.getRESULT();
		final String database = config.getDATABASE();

		System.out.println("Result: " + results);
		System.out.println("Database: " + database);

		final List<GUIFile> files = new ArrayList<>();
		final List<GUIClonePair> clonepairs = new ArrayList<>();
		DetectionResultsFormat.read(results, files, clonepairs);

		clonepairs.stream().filter(c -> null == c.code).forEach(c -> {
			System.err.println("Tokenized code of clones are required to use CGExaminer.");
			System.exit(0);
		});

		// clonepairs.stream().forEach(c -> System.out.println(c.code));

		final GUICloneSet[] clonesets = GUICloneSet.convertPairsToSets(clonepairs);
		Arrays.stream(clonesets).forEach(c -> {
			System.out.println(c.id + " : " + c.getCode());
		});

	}

}
