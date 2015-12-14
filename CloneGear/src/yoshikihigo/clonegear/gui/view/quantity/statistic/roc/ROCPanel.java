package yoshikihigo.clonegear.gui.view.quantity.statistic.roc;

import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class ROCPanel extends javax.swing.JPanel implements ROCPanelConst,
		Observer, QuantitativeViewInterface {

	final private int properMaxNumber;
	final private int allFileNumber;
	final int[] unfilteredNumber;
	final int[] filteredNumber;

	public ROCPanel() {

		final int maxNumber = SelectedEntities.<GUIFile> getInstance(GROUP)
				.get().size();
		this.properMaxNumber = this.getProperMaxNumber(maxNumber);

		this.unfilteredNumber = new int[RANGE_NUMBER];
		this.filteredNumber = new int[RANGE_NUMBER];
		for (int i = 0; i < RANGE_NUMBER; i++) {
			unfilteredNumber[i] = 0;
			filteredNumber[i] = 0;
		}

		this.allFileNumber = GUIFileManager.SINGLETON.getFileCount();
	}

	public void init() {
	}

	@Override
	public void update(final Observable o, final Object arg) {

		if (o instanceof SelectedEntities) {

			SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(SELECTED_FILE)) {

			} else if (selectedEntities.getLabel().equals(RELATED_FILE)) {

			} else if (selectedEntities.getLabel().equals(GROUP)) {
				this.update();
			}

		} else if (o instanceof RNRValue) {

			RNRValue rnrValue = (RNRValue) o;
			if (rnrValue.getLabel().equals(RNR)) {
				this.update();
			}
		}
	}

	private void update() {

		for (int i = 0; i < RANGE_NUMBER; i++) {
			unfilteredNumber[i] = 0;
			filteredNumber[i] = 0;
		}

		final int rnr = RNRValue.getInstance(RNR).get();
		for (final GUIFile file : SelectedEntities.<GUIFile> getInstance(GROUP)
				.get()) {

			final double unfilteredROC = file.getROC();
			if ((0 <= unfilteredROC) && (unfilteredROC <= 10))
				unfilteredNumber[0]++;
			else if (unfilteredROC <= 20)
				unfilteredNumber[1]++;
			else if (unfilteredROC <= 30)
				unfilteredNumber[2]++;
			else if (unfilteredROC <= 40)
				unfilteredNumber[3]++;
			else if (unfilteredROC <= 50)
				unfilteredNumber[4]++;
			else if (unfilteredROC <= 60)
				unfilteredNumber[5]++;
			else if (unfilteredROC <= 70)
				unfilteredNumber[6]++;
			else if (unfilteredROC <= 80)
				unfilteredNumber[7]++;
			else if (unfilteredROC <= 90)
				unfilteredNumber[8]++;
			else if (unfilteredROC <= 100)
				unfilteredNumber[9]++;
			else {
				System.err.println("Unproper ROC Value: " + unfilteredROC);
			}

			double filteredROC = file.getROC(rnr);
			if ((0 <= filteredROC) && (filteredROC <= 10))
				filteredNumber[0]++;
			else if (filteredROC <= 20)
				filteredNumber[1]++;
			else if (filteredROC <= 30)
				filteredNumber[2]++;
			else if (filteredROC <= 40)
				filteredNumber[3]++;
			else if (filteredROC <= 50)
				filteredNumber[4]++;
			else if (filteredROC <= 60)
				filteredNumber[5]++;
			else if (filteredROC <= 70)
				filteredNumber[6]++;
			else if (filteredROC <= 80)
				filteredNumber[7]++;
			else if (filteredROC <= 90)
				filteredNumber[8]++;
			else if (filteredROC <= 100)
				filteredNumber[9]++;
			else {
				System.err.println("Unproper ROC Value: " + filteredROC);
			}
		}

		this.repaint();
	}

	public void paint(final Graphics g) {
		this.drawBackGround(g);
		this.drawColorExplanation(g);
		this.drawStatus(g);
		this.drawFrame(g);
		this.drawLabel(g);
		this.drawBar(g);
	}

	private void drawBackGround(final Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, width, height);
	}

	private void drawColorExplanation(final Graphics g) {

		final int width = this.getWidth();
		final int height = this.getHeight();

		final int x_color = width - 100;
		final int x_explanation = width - 70;
		final int y_unfiltered = height - 5;
		final int y_filtered = height - 30;
		final int color_width = 20;
		final int color_height = 20;

		g.setColor(UNFILTERED_BAR_COLOR);
		g.fillRect(x_color, this.getReversedY(y_unfiltered), color_width,
				color_height);
		g.drawString("Unfiltered", x_explanation,
				this.getReversedY(y_unfiltered - 15));

		g.setColor(FILTERED_BAR_COLOR);
		g.fillRect(x_color, this.getReversedY(y_filtered), color_width,
				color_height);
		g.drawString("Filtered", x_explanation,
				this.getReversedY(y_filtered - 15));
	}

	private void drawStatus(final Graphics g) {

		final int width = this.getWidth();
		final int height = this.getHeight();

		final int rnr = RNRValue.getInstance(RNR).get();
		final int unfilteredCloneSetNumber = GUICloneManager.SINGLETON
				.getCloneSetCount();
		final int filteredCloneSetNumber = GUICloneManager.SINGLETON
				.getCloneSetCount(rnr);
		final int unfilteredClonePairNumber = GUICloneManager.SINGLETON
				.getClonePairCount();
		final int filteredClonePairNumber = GUICloneManager.SINGLETON
				.getClonePairCount(rnr);
		final int unfilteredFragmentNumber = GUICloneManager.SINGLETON
				.getClones().size();
		final int filteredFragmentNumber = GUICloneManager.SINGLETON.getClones(
				rnr).size();

		final int y1 = height - 15;
		final int y2 = height - 30;
		final int y3 = height - 45;
		final String averageUnfilteredROC = String
				.valueOf((int) GUIFileManager.SINGLETON.getTotalROC());
		final String averageFilteredROC = String
				.valueOf((int) GUIFileManager.SINGLETON.getTotalROC(rnr));

		g.setColor(STATUS_COLOR);

		StringBuffer rocBuffer = new StringBuffer();
		rocBuffer.append("Average of ROC(f) : ");
		rocBuffer.append(averageFilteredROC);
		rocBuffer.append("(");
		rocBuffer.append(averageUnfilteredROC);
		rocBuffer.append(")");

		g.drawString(rocBuffer.toString(), width - 640, this.getReversedY(y3));

		StringBuffer cloneSetBuffer = new StringBuffer();
		cloneSetBuffer.append("# of All Clone Sets : ");
		cloneSetBuffer.append(filteredCloneSetNumber);
		cloneSetBuffer.append("(");
		cloneSetBuffer.append(unfilteredCloneSetNumber);
		cloneSetBuffer.append(")");

		StringBuffer clonePairBuffer = new StringBuffer();
		clonePairBuffer.append("# of All Clone Pairs : ");
		clonePairBuffer.append(filteredClonePairNumber);
		clonePairBuffer.append("(");
		clonePairBuffer.append(unfilteredClonePairNumber);
		clonePairBuffer.append(")");

		StringBuffer fragmentBuffer = new StringBuffer();
		fragmentBuffer.append("# of All Fragments : ");
		fragmentBuffer.append(filteredFragmentNumber);
		fragmentBuffer.append("(");
		fragmentBuffer.append(unfilteredFragmentNumber);
		fragmentBuffer.append(")");

		g.drawString(cloneSetBuffer.toString(), width - 450,
				this.getReversedY(y1));
		g.drawString(clonePairBuffer.toString(), width - 450,
				this.getReversedY(y2));
		g.drawString(fragmentBuffer.toString(), width - 450,
				this.getReversedY(y3));

		StringBuffer allFileBuffer = new StringBuffer();
		allFileBuffer.append("# of All Files : ");
		allFileBuffer.append(this.allFileNumber);

		StringBuffer shownFileBuffer = new StringBuffer();
		shownFileBuffer.append("# of Shown Files : ");
		shownFileBuffer.append(SelectedEntities.<GUIFile> getInstance(GROUP)
				.get().size());

		g.drawString(allFileBuffer.toString(), width - 240,
				this.getReversedY(y1));
		g.drawString(shownFileBuffer.toString(), width - 240,
				this.getReversedY(y2));
	}

	private void drawFrame(final Graphics g) {

		final int width = this.getWidth();
		final int height = this.getHeight();

		final int horizontalLineXFrom = X_MARGIN;
		final int horizontalLineYFrom = Y_MARGIN;
		final int horizontalLineXTo = width - X_MARGIN;
		final int horizontalLineYTo = Y_MARGIN;

		final int verticalLineXFrom = X_MARGIN;
		final int verticalLineYFrom = Y_MARGIN;
		final int verticalLineXTo = X_MARGIN;
		final int verticalLineYTo = height - Y_MARGIN;

		g.setColor(FRAME_COLOR);
		g.drawLine(horizontalLineXFrom, this.getReversedY(horizontalLineYFrom),
				horizontalLineXTo, this.getReversedY(horizontalLineYTo));
		g.drawLine(horizontalLineXFrom,
				this.getReversedY(horizontalLineYFrom - 1), horizontalLineXTo,
				this.getReversedY(horizontalLineYTo - 1));
		g.drawLine(horizontalLineXFrom,
				this.getReversedY(horizontalLineYFrom - 2), horizontalLineXTo,
				this.getReversedY(horizontalLineYTo - 2));

		g.drawLine(verticalLineXFrom, this.getReversedY(verticalLineYFrom),
				verticalLineXTo, this.getReversedY(verticalLineYTo));
		g.drawLine(verticalLineXFrom - 1, this.getReversedY(verticalLineYFrom),
				verticalLineXTo - 1, this.getReversedY(verticalLineYTo));
		g.drawLine(verticalLineXFrom - 2, this.getReversedY(verticalLineYFrom),
				verticalLineXTo - 2, this.getReversedY(verticalLineYTo));

		final int value = this.properMaxNumber / LINE_NUMBER;
		final int blockHeight = (height - 2 * Y_MARGIN) / LINE_NUMBER;
		for (int i = 0; i <= LINE_NUMBER; i++) {
			g.drawString(Integer.toString(value * i), X_MARGIN - 30,
					this.getReversedY(Y_MARGIN + blockHeight * i));
			g.drawLine(horizontalLineXFrom,
					this.getReversedY(Y_MARGIN + blockHeight * i),
					horizontalLineXTo,
					this.getReversedY(Y_MARGIN + blockHeight * i));
		}
	}

	private void drawBar(final Graphics g) {

		final int width = this.getWidth();
		final int height = this.getHeight();

		final int totalBarMargin = BAR_MARGIN * 9;
		final int totalBarWidth = width - X_MARGIN * 2 - totalBarMargin;
		final int barWidth = totalBarWidth / 20;
		final int maxBarHeight = height - Y_MARGIN * 2;

		int xOrigin = X_MARGIN;

		for (int i = 0; i < RANGE_NUMBER; i++) {

			int unfilteredBarHeight = maxBarHeight * this.unfilteredNumber[i]
					/ this.properMaxNumber;
			int filteredBarHeight = maxBarHeight * this.filteredNumber[i]
					/ this.properMaxNumber;

			g.setColor(UNFILTERED_BAR_COLOR);
			g.fillRect(xOrigin,
					this.getReversedY(Y_MARGIN + unfilteredBarHeight),
					barWidth, unfilteredBarHeight);
			g.drawString(
					Integer.toString(this.unfilteredNumber[i]),
					xOrigin + NUMBER_LABEL_X_MARGIN,
					this.getReversedY(Y_MARGIN + unfilteredBarHeight
							+ NUMBER_LABEL_Y_MARGIN));
			xOrigin += barWidth;

			g.setColor(FILTERED_BAR_COLOR);
			g.fillRect(xOrigin,
					this.getReversedY(Y_MARGIN + filteredBarHeight), barWidth,
					filteredBarHeight);
			g.drawString(
					Integer.toString(this.filteredNumber[i]),
					xOrigin + NUMBER_LABEL_X_MARGIN,
					this.getReversedY(Y_MARGIN + filteredBarHeight
							+ NUMBER_LABEL_Y_MARGIN));
			xOrigin += barWidth + BAR_MARGIN;
		}
	}

	private void drawLabel(final Graphics g) {

		final int width = this.getWidth();
		final int height = this.getHeight();

		final int totalBarMargin = BAR_MARGIN * 9;
		final int totalBarWidth = width - X_MARGIN * 2 - totalBarMargin;
		final int rangeWidth = totalBarWidth / 10 + BAR_MARGIN;

		int xPoint = X_MARGIN + RANGE_LABEL_X_MARGIN;
		final int yPoint = this.getReversedY(RANGE_LABEL_Y_MARGIN);
		for (int i = 0; i < RANGE_NUMBER; i++) {
			g.drawString(RANGE_TITLE[i], xPoint, yPoint);
			xPoint += rangeWidth;
		}

		g.drawString(X_TITLE, width - 100, this.getReversedY(30));
		g.drawString(Y_TITLE, 5, this.getReversedY(height - 30));
	}

	private int getReversedY(final int y) {
		return this.getHeight() - y;
	}

	private int getProperMaxNumber(int i) {

		if (i <= 5)
			return 5;
		else if (i <= 10)
			return 10;
		else if (i <= 15)
			return 15;
		else if (i <= 20)
			return 20;
		else if (i <= 25)
			return 25;
		else if (i <= 30)
			return 30;
		else if (i <= 35)
			return 35;
		else if (i <= 40)
			return 40;
		else if (i <= 45)
			return 45;
		else if (i <= 50)
			return 50;
		else if (i <= 60)
			return 60;
		else if (i <= 70)
			return 70;
		else if (i <= 80)
			return 80;
		else if (i <= 90)
			return 90;
		else if (i <= 100)
			return 100;
		else if (i <= 120)
			return 120;
		else if (i <= 140)
			return 140;
		else if (i <= 160)
			return 160;
		else if (i <= 180)
			return 180;
		else if (i <= 200)
			return 200;
		else if (i <= 250)
			return 250;
		else if (i <= 300)
			return 300;
		else if (i <= 350)
			return 350;
		else if (i <= 400)
			return 400;
		else if (i <= 450)
			return 450;
		else if (i <= 500)
			return 500;
		else if (i <= 600)
			return 600;
		else if (i <= 700)
			return 700;
		else if (i <= 800)
			return 800;
		else if (i <= 900)
			return 900;
		else if (i <= 1000)
			return 1000;
		else if (i <= 1200)
			return 1200;
		else if (i <= 1400)
			return 1400;
		else if (i <= 1600)
			return 1600;
		else if (i <= 1800)
			return 1800;
		else if (i <= 2000)
			return 2000;
		else if (i <= 2500)
			return 2500;
		else if (i <= 3000)
			return 3000;
		else if (i <= 3500)
			return 3500;
		else if (i <= 4000)
			return 4000;
		else if (i <= 4500)
			return 4500;
		else if (i <= 5000)
			return 5000;
		else if (i <= 6000)
			return 6000;
		else if (i <= 7000)
			return 7000;
		else if (i <= 8000)
			return 8000;
		else if (i <= 9000)
			return 9000;
		else if (i <= 10000)
			return 10000;
		else if (i <= 12000)
			return 12000;
		else if (i <= 14000)
			return 14000;
		else if (i <= 16000)
			return 16000;
		else if (i <= 18000)
			return 18000;
		else if (i <= 20000)
			return 20000;
		else if (i <= 25000)
			return 25000;
		else if (i <= 30000)
			return 30000;
		else if (i <= 35000)
			return 35000;
		else if (i <= 40000)
			return 40000;
		else if (i <= 45000)
			return 45000;
		else if (i <= 50000)
			return 50000;
		else if (i <= 60000)
			return 60000;
		else if (i <= 70000)
			return 70000;
		else if (i <= 80000)
			return 80000;
		else if (i <= 90000)
			return 90000;
		else if (i <= 100000)
			return 100000;
		else if (i <= 120000)
			return 120000;
		else if (i <= 140000)
			return 140000;
		else if (i <= 160000)
			return 160000;
		else if (i <= 180000)
			return 180000;
		else if (i <= 200000)
			return 200000;
		else if (i <= 250000)
			return 250000;
		else if (i <= 300000)
			return 300000;
		else if (i <= 350000)
			return 350000;
		else if (i <= 400000)
			return 400000;
		else if (i <= 450000)
			return 450000;
		else if (i <= 500000)
			return 500000;
		else if (i <= 600000)
			return 600000;
		else if (i <= 700000)
			return 700000;
		else if (i <= 800000)
			return 800000;
		else if (i <= 900000)
			return 900000;
		else if (i <= 1000000)
			return 1000000;
		else
			return i;
	}
}
