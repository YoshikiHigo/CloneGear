package yoshikihigo.clonegear.gui.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;

public class StatisticalView extends JInternalFrame {

	private static final Logger logger = Logger.getLogger("StatisticalView");

	public StatisticalView(final javax.swing.JDesktopPane parentContainer) {

		super("Statistical View", true, false, true, true);

		logger.log(Level.INFO, "begin");

		// setting this comnponent size
		// set operation when this window is closed
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		parentContainer.add(this);
		this.setBounds(0, 0, 300, 500);

		logger.log(Level.INFO, "end");
	}

	public void reset() {

		logger.log(Level.INFO, "begin");

		logger.log(Level.INFO, "end");
	}

	public void init() {

		logger.log(Level.INFO, "begin");

		final int numberOfCloneClasses = GUICloneManager.instance()
				.getCloneSetCount();
		final int numberOfClonePairs = GUICloneManager.instance()
				.getClonePairCount();
		final int numberOfClonedFragments = GUICloneManager.instance()
				.getClones().size();

		final int numberOfFiles = GUIFileManager.instance().getFileCount();
		final int numberOfGroups = GUIFileManager.instance().getGroupCount();
		final int totalLinesOfCode = GUIFileManager.instance().getTotalLOC();
		final double totalClonedRatio = GUIFileManager.instance().getTotalROC();

		final int[][] clonedRatioDistribution = this
				.getClonedRatioDistribution(GUIFileManager.instance());

		final JTextArea statisticalTextArea = new JTextArea();
		statisticalTextArea.setEditable(false);
		statisticalTextArea.append("\n");
		statisticalTextArea.append("Number of Target Files: " + numberOfFiles
				+ "\n");
		statisticalTextArea.append("Number of Target Groups: " + numberOfGroups
				+ "\n");
		statisticalTextArea.append("Total Lines of Code: " + totalLinesOfCode
				+ "\n");
		statisticalTextArea.append("\n");
		statisticalTextArea.append("Number of Clone Sets: "
				+ numberOfCloneClasses + "\n");
		statisticalTextArea.append("Number of Clone Pairs: "
				+ numberOfClonePairs + "\n");
		statisticalTextArea.append("Number of Cloned Fragment: "
				+ numberOfClonedFragments + "\n");
		statisticalTextArea.append("\n");
		statisticalTextArea.append("Cloned Ratio: " + totalClonedRatio + "%\n");
		statisticalTextArea.append("\n");
		statisticalTextArea.append("Duplicated rate : # of files\n");
		statisticalTextArea.append(" 0%  -  10%: "
				+ clonedRatioDistribution[0][1] + "%("
				+ clonedRatioDistribution[0][0] + ")\n");
		statisticalTextArea.append("11%  -  20%: "
				+ clonedRatioDistribution[1][1] + "%("
				+ clonedRatioDistribution[1][0] + ")\n");
		statisticalTextArea.append("21%  -  30%: "
				+ clonedRatioDistribution[2][1] + "%("
				+ clonedRatioDistribution[2][0] + ")\n");
		statisticalTextArea.append("31%  -  40%: "
				+ clonedRatioDistribution[3][1] + "%("
				+ clonedRatioDistribution[3][0] + ")\n");
		statisticalTextArea.append("41%  -  50%: "
				+ clonedRatioDistribution[4][1] + "%("
				+ clonedRatioDistribution[4][0] + ")\n");
		statisticalTextArea.append("51%  -  60%: "
				+ clonedRatioDistribution[5][1] + "%("
				+ clonedRatioDistribution[5][0] + ")\n");
		statisticalTextArea.append("61%  -  70%: "
				+ clonedRatioDistribution[6][1] + "%("
				+ clonedRatioDistribution[6][0] + ")\n");
		statisticalTextArea.append("71%  -  80%: "
				+ clonedRatioDistribution[7][1] + "%("
				+ clonedRatioDistribution[7][0] + ")\n");
		statisticalTextArea.append("81%  -  90%: "
				+ clonedRatioDistribution[8][1] + "%("
				+ clonedRatioDistribution[8][0] + ")\n");
		statisticalTextArea.append("91%  - 100%: "
				+ clonedRatioDistribution[9][1] + "%("
				+ clonedRatioDistribution[9][0] + ")\n");

		this.getContentPane().add(statisticalTextArea);

		logger.log(Level.INFO, "end");
	}

	private int[][] getClonedRatioDistribution(final GUIFileManager manager) {

		int[][] clonedRatioDistribution = new int[10][2];
		for (final GUIFile file : manager.getFiles()) {
			int roc = (int) file.getROC();
			if (roc <= 10)
				clonedRatioDistribution[0][0]++;
			else if (roc <= 20)
				clonedRatioDistribution[1][0]++;
			else if (roc <= 30)
				clonedRatioDistribution[2][0]++;
			else if (roc <= 40)
				clonedRatioDistribution[3][0]++;
			else if (roc <= 50)
				clonedRatioDistribution[4][0]++;
			else if (roc <= 60)
				clonedRatioDistribution[5][0]++;
			else if (roc <= 70)
				clonedRatioDistribution[6][0]++;
			else if (roc <= 80)
				clonedRatioDistribution[7][0]++;
			else if (roc <= 90)
				clonedRatioDistribution[8][0]++;
			else
				clonedRatioDistribution[9][0]++;
		}

		for (int i = 0; i < 10; i++) {
			int ratio = StrictMath.round(100.0f
					* ((float) clonedRatioDistribution[i][0])
					/ (float) manager.getFileCount());
			clonedRatioDistribution[i][1] = ratio;
		}

		return clonedRatioDistribution;
	}
}
