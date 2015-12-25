package yoshikihigo.clonegear.gui.view.metric.graph;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JPanel;

import yoshikihigo.clonegear.gui.data.clone.CloneMetricsMaxValues;
import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneManager;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;

public class MetricView extends JPanel implements MetricViewConst, Observer,
		MetricViewInterface, ViewColors {

	class Filter implements MouseMotionListener, MouseListener {

		final private int[] x;
		final private int[] y;
		final private double[] yRate;

		private Point pressedPoint;

		private int draggingPolygonalIndex;

		Filter() {
			this.x = new int[2 * AXIS_TITLE.length];
			this.y = new int[2 * AXIS_TITLE.length];
			this.yRate = new double[2 * AXIS_TITLE.length];

			this.reset();
		}

		@Override
		public void mouseClicked(final MouseEvent evt) {
		}

		@Override
		public void mouseEntered(final MouseEvent evt) {
		}

		@Override
		public void mouseExited(final MouseEvent evt) {
		}

		@Override
		public void mousePressed(final MouseEvent evt) {
			if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				this.pressedPoint = evt.getPoint();
				this.draggingPolygonalIndex = this
						.getPolygonalIndex(this.pressedPoint);
				if (this.draggingPolygonalIndex != -1)
					MetricView.this.setCursor(HAND_CURSOR);
			}
		}

		@Override
		public void mouseReleased(final MouseEvent evt) {

			MetricView.this.setCursor(WAIT_CURSOR);

			if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {

				if (this.draggingPolygonalIndex != -1) {

					this.update();
					this.draggingPolygonalIndex = -1;

					SelectedEntities.<GUIClone> getInstance(CLONE).clear(
							MetricView.this);
					SelectedEntities.<GUICloneSet> getInstance(
							SELECTED_CLONESET).clear(MetricView.this);
				}
			}

			MetricView.this.setCursor(DEFAULT_CURSOR);
		}

		@Override
		public void mouseDragged(final MouseEvent evt) {

			if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {

				if (this.draggingPolygonalIndex != -1) {

					final int deltaY = this.pressedPoint.y - evt.getY();
					final double deltaRate = ((double) deltaY)
							/ ((double) MetricView.this.getYSpace());

					this.yRate[this.draggingPolygonalIndex] += deltaRate;

					if (this.yRate[this.draggingPolygonalIndex] < 0.0)
						this.yRate[this.draggingPolygonalIndex] = 0.0;
					if (this.yRate[this.draggingPolygonalIndex] > 1.0)
						this.yRate[this.draggingPolygonalIndex] = 1.0;

					final int otherSideIndex = 2 * AXIS_TITLE.length - 1
							- this.draggingPolygonalIndex;

					if (this.draggingPolygonalIndex < otherSideIndex) {
						if (this.yRate[this.draggingPolygonalIndex] < this.yRate[otherSideIndex]) {
							this.yRate[this.draggingPolygonalIndex] = this.yRate[otherSideIndex];
						}

					} else {
						if (this.yRate[this.draggingPolygonalIndex] > this.yRate[otherSideIndex]) {
							this.yRate[this.draggingPolygonalIndex] = this.yRate[otherSideIndex];
						}
					}

					this.pressedPoint = evt.getPoint();
					MetricView.this.repaint();
				}
			}
		}

		@Override
		public void mouseMoved(final MouseEvent evt) {
		}

		private void reset() {

			for (int i = 0; i < AXIS_TITLE.length; i++) {

				this.x[i] = X_MARGIN + i * MetricView.this.getXInterval();
				this.x[2 * AXIS_TITLE.length - 1 - i] = X_MARGIN + i
						* MetricView.this.getXInterval();

				this.y[i] = MetricView.this.getReversedY(Y_MARGIN);
				this.y[2 * AXIS_TITLE.length - 1 - i] = MetricView.this
						.getReversedY(MetricView.this.getHeight() - Y_MARGIN);

				this.yRate[i] = 1.0;
				this.yRate[2 * AXIS_TITLE.length - 1 - i] = 0.0;
			}

			this.update();
		}

		void update() {

			SelectedEntities.<GUIClone> getInstance(CLONE).clear(
					MetricView.this);
			SelectedEntities.<GUICloneSet> getInstance(SELECTED_CLONESET)
					.clear(MetricView.this);

			final Set<GUICloneSet> unfilteredCloneSets = new HashSet<>();
			final Set<GUICloneSet> filteredCloneSets = new HashSet<>();

			for (final GUICloneSet cloneset : GUICloneManager.SINGLETON
					.getCloneSets()) {

				if (cloneset.getRAD() > MetricView.this.maxRAD * this.yRate[0])
					unfilteredCloneSets.add(cloneset);
				else if (cloneset.getLEN() > MetricView.this.maxLEN
						* this.yRate[1])
					unfilteredCloneSets.add(cloneset);
				else if (cloneset.getRNR() > MetricView.this.maxRNR
						* this.yRate[2])
					unfilteredCloneSets.add(cloneset);
				else if (cloneset.getNIF() > MetricView.this.maxNIF
						* this.yRate[3])
					unfilteredCloneSets.add(cloneset);
				else if (cloneset.getPOP() > MetricView.this.maxPOP
						* this.yRate[4])
					unfilteredCloneSets.add(cloneset);
				else if (cloneset.getDFL() > MetricView.this.maxDFL
						* this.yRate[5])
					unfilteredCloneSets.add(cloneset);

				else if (MetricView.this.maxRAD * this.yRate[11] > cloneset
						.getRAD())
					unfilteredCloneSets.add(cloneset);
				else if (MetricView.this.maxLEN * this.yRate[10] > cloneset
						.getLEN())
					unfilteredCloneSets.add(cloneset);
				else if (MetricView.this.maxRNR * this.yRate[9] > cloneset
						.getRNR())
					unfilteredCloneSets.add(cloneset);
				else if (MetricView.this.maxNIF * this.yRate[8] > cloneset
						.getNIF())
					unfilteredCloneSets.add(cloneset);
				else if (MetricView.this.maxPOP * this.yRate[7] > cloneset
						.getPOP())
					unfilteredCloneSets.add(cloneset);
				else if (MetricView.this.maxDFL * this.yRate[6] > cloneset
						.getDFL())
					unfilteredCloneSets.add(cloneset);
				else
					filteredCloneSets.add(cloneset);
			}

			SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET)
					.setAll(filteredCloneSets, MetricView.this);
			MetricView.this.repaint();
		}

		void draw(final Graphics g) {

			for (int i = 0; i < AXIS_TITLE.length; i++) {

				this.x[i] = X_MARGIN + i * MetricView.this.getXInterval();
				this.x[2 * AXIS_TITLE.length - 1 - i] = X_MARGIN + i
						* MetricView.this.getXInterval();

				this.y[i] = MetricView.this
						.getReversedY((int) (Y_MARGIN + MetricView.this
								.getYSpace() * this.yRate[i]));
				this.y[2 * AXIS_TITLE.length - 1 - i] = MetricView.this
						.getReversedY((int) (Y_MARGIN + MetricView.this
								.getYSpace()
								* this.yRate[2 * AXIS_TITLE.length - 1 - i]));
			}

			g.setColor(METRICS_FILTER_AREA_ON_COLOR);
			g.fillPolygon(this.x, this.y, 2 * AXIS_TITLE.length);
		}

		private int getPolygonalIndex(final Point p) {

			final int validAreaWidth = MetricView.this.getWidth()
					/ (2 * AXIS_TITLE.length);
			final int validAreaHeight = (int) (MetricView.this.getYSpace() * 0.1);

			// get axis index
			final int axisIndex = getNearestAxisIndex(p.x);

			final int bottomY = Y_MARGIN
					+ (int) (MetricView.this.getYSpace() * this.yRate[axisIndex]);
			final int topY = Y_MARGIN
					+ (int) (MetricView.this.getYSpace() * this.yRate[2
							* AXIS_TITLE.length - 1 - axisIndex]);

			final Rectangle topValidArea = new Rectangle(this.x[axisIndex]
					- validAreaWidth / 2, MetricView.this.getReversedY(topY)
					- validAreaHeight / 2, validAreaWidth, validAreaHeight);
			final Rectangle bottomValidArea = new Rectangle(this.x[axisIndex]
					- validAreaWidth / 2, MetricView.this.getReversedY(bottomY)
					- validAreaHeight / 2, validAreaWidth, validAreaHeight);

			boolean topContains = topValidArea.contains(p);
			boolean bottomContains = bottomValidArea.contains(p);

			if (topContains && bottomContains) {

				int halfY = (topY + bottomY) / 2;
				if (halfY > p.y) {
					bottomContains = false;
				} else {
					topContains = false;
				}
			}

			if (bottomContains) {
				return axisIndex;
			} else if (topContains) {
				return 2 * AXIS_TITLE.length - 1 - axisIndex;
			} else {
				return -1;
			}
		}

		private int getNearestAxisIndex(final int x) {
			final double x_interval = ((double) MetricView.this.getWidth())
					/ ((double) AXIS_TITLE.length);
			final int index = (int) (x / x_interval);
			return index;
		}
	}

	final private Filter filter;
	final private int maxRAD;
	final private int maxLEN;
	final private int maxRNR;
	final private int maxNIF;
	final private int maxPOP;
	final private int maxDFL;

	public MetricView() {

		this.filter = new Filter();
		this.addMouseListener(this.filter);
		this.addMouseMotionListener(this.filter);

		this.maxRAD = CloneMetricsMaxValues.SINGLETON.getMaxRAD();
		this.maxLEN = this.getPropertyMaxNumber(CloneMetricsMaxValues.SINGLETON
				.getMaxLEN());
		this.maxRNR = CloneMetricsMaxValues.SINGLETON.getMaxRNR();
		this.maxNIF = this.getPropertyMaxNumber(CloneMetricsMaxValues.SINGLETON
				.getMaxNIF());
		this.maxPOP = this.getPropertyMaxNumber(CloneMetricsMaxValues.SINGLETON
				.getMaxPOP());
		this.maxDFL = this.getPropertyMaxNumber(CloneMetricsMaxValues.SINGLETON
				.getMaxDFL());
	}

	@Override
	public void paint(final Graphics g) {

		this.drawBackGround(g);
		this.drawStatus(g);
		this.drawAxis(g);
		this.drawCloneSets(g);
		this.drawLabel(g);
		this.filter.draw(g);
	}

	@Override
	public void update(final Observable o, final Object arg) {
	}

	public void init() {
		this.reset();
	}

	public void reset() {
		this.filter.reset();
	}

	private int getYSpace() {
		return this.getHeight() - 2 * Y_MARGIN;
	}

	private int getXInterval() {
		return (this.getWidth() - 2 * X_MARGIN) / (AXIS_TITLE.length - 1);
	}

	private void drawBackGround(final Graphics g) {

		final int width = this.getWidth();
		final int height = this.getHeight();

		g.setColor(METRIC_GRAPH_BACKGROUND_COLOR);
		g.fillRect(0, 0, width, height);
	}

	private void drawStatus(final Graphics g) {

		final List<GUICloneSet> filteredCloneSets = SelectedEntities
				.<GUICloneSet> getInstance(FILTERED_CLONESET).get();
		final int numberOfCloneSet = GUICloneManager.SINGLETON
				.getCloneSetCount();
		final int numberOfSelectedCloneSet = filteredCloneSets.size();

		g.setColor(METRIC_GRAPH_STATUS_COLOR);
		g.drawString(numberOfSelectedCloneSet + "/" + numberOfCloneSet
				+ " are selected", 20, 25);
	}

	private void drawAxis(final Graphics g) {

		final int width = this.getWidth();
		final int height = this.getHeight();
		final int y_title = TITLE_MARGIN;
		final int y_line_start = X_MARGIN;
		final int y_line_end = height - X_MARGIN;
		final double data_space = (width - (2 * X_MARGIN))
				/ (AXIS_TITLE.length - 1);

		g.setColor(METRICS_AXIS_TITLE_COLOR);

		for (int i = 0; i < AXIS_TITLE.length; i++) {
			final int x_location = Y_MARGIN + (int) (data_space * i);
			g.drawLine(x_location, this.getReversedY(y_line_start), x_location,
					this.getReversedY(y_line_end));
			g.drawString(AXIS_TITLE[i], x_location - 10,
					this.getReversedY(y_title));
		}
	}

	private void drawCloneSets(final Graphics g) {

		g.setColor(METRICS_UN_SELECTED_DATA_COLOR);
		GUICloneManager.SINGLETON.getCloneSets().stream()
				.forEach(cloneset -> this.drawCloneSet(g, cloneset));

		g.setColor(METRICS_SELECTED_DATA_COLOR);
		SelectedEntities.<GUICloneSet> getInstance(FILTERED_CLONESET).get()
				.stream().forEach(cloneset -> this.drawCloneSet(g, cloneset));
	}

	private void drawCloneSet(final Graphics g, final GUICloneSet cloneSet) {

		// get y axis plot location
		final int[] y = new int[AXIS_TITLE.length];
		y[0] = this.getReversedY((int) (Y_MARGIN + this.getYSpace()
				* (((double) cloneSet.getRAD()) / ((double) this.maxRAD))));
		y[1] = this.getReversedY((int) (Y_MARGIN + this.getYSpace()
				* (((double) cloneSet.getLEN()) / ((double) this.maxLEN))));
		y[2] = this.getReversedY((int) (Y_MARGIN + this.getYSpace()
				* (((double) cloneSet.getRNR()) / ((double) this.maxRNR))));
		y[3] = this.getReversedY((int) (Y_MARGIN + this.getYSpace()
				* (((double) cloneSet.getNIF()) / ((double) this.maxNIF))));
		y[4] = this.getReversedY((int) (Y_MARGIN + this.getYSpace()
				* (((double) cloneSet.getPOP()) / ((double) this.maxPOP))));
		y[5] = this.getReversedY((int) (Y_MARGIN + this.getYSpace()
				* (((double) cloneSet.getDFL()) / ((double) this.maxDFL))));

		// get x axis plot location
		final int[] x = new int[AXIS_TITLE.length];
		final int data_space = (this.getWidth() - 2 * X_MARGIN)
				/ (AXIS_TITLE.length - 1);
		for (int i = 0; i < AXIS_TITLE.length; i++) {
			x[i] = X_MARGIN + i * data_space;
		}

		g.drawPolyline(x, y, AXIS_TITLE.length);
	}

	private void drawLabel(final Graphics g) {

		// this value is space between metrics values
		// int data_space = this.getYInterval();
		final int data_space = this.getXInterval();

		g.setColor(METRICS_AXIS_LABEL_COLOR);
		this.drawRADLabel(g);
		this.drawLENLabel(g, data_space);
		this.drawRNRLabel(g, data_space);
		this.drawNIFLabel(g, data_space);
		this.drawPOPLabel(g, data_space);
		this.drawDFLLabel(g, data_space);
	}

	private void drawRADLabel(final Graphics g) {

		final int x_RAD = X_MARGIN + LABEL_MARGIN;
		final double incCoordinate = ((double) this.getYSpace()) / this.maxRAD;

		for (int i = 0; i <= this.maxRAD; i++) {
			g.drawString(Integer.toString(i), x_RAD,
					this.getReversedY(Y_MARGIN + ((int) (i * incCoordinate))));
		}
	}

	private void drawLENLabel(final Graphics g, final int data_space) {

		final int x_LEN = X_MARGIN + data_space + LABEL_MARGIN;
		final double incValue = ((double) this.maxLEN) / LABEL_NUMBER;
		final double incCoordinate = ((double) this.getYSpace()) / LABEL_NUMBER;

		for (int i = 0; i <= LABEL_NUMBER; i++) {
			g.drawString(Integer.toString(((int) (i * incValue))), x_LEN,
					this.getReversedY(Y_MARGIN + (int) (i * incCoordinate)));
		}
	}

	private void drawRNRLabel(final Graphics g, int data_space) {

		final int x_RNR = X_MARGIN + 2 * data_space + LABEL_MARGIN;
		final int incValue = this.maxRNR / LABEL_NUMBER;
		final double incCoordinate = ((double) this.getYSpace()) / LABEL_NUMBER;

		for (int i = 0; i <= LABEL_NUMBER; i++) {
			g.drawString(String.valueOf(i * incValue), x_RNR,
					this.getReversedY(Y_MARGIN + (int) (i * incCoordinate)));
		}
	}

	private void drawNIFLabel(final Graphics g, final int data_space) {

		final int x_NIF = X_MARGIN + 3 * data_space + LABEL_MARGIN;
		final double incValue = ((double) this.maxNIF) / LABEL_NUMBER;
		final double incCoordinate = ((double) this.getYSpace()) / LABEL_NUMBER;

		for (int i = 0; i <= LABEL_NUMBER; i++) {
			g.drawString(Integer.toString(((int) (i * incValue))), x_NIF,
					this.getReversedY(Y_MARGIN + (int) (i * incCoordinate)));
		}
	}

	private void drawPOPLabel(final Graphics g, final int data_space) {

		final int x_POP = X_MARGIN + 4 * data_space + LABEL_MARGIN;
		final double incValue = ((double) this.maxPOP) / LABEL_NUMBER;
		final double incCoordinate = ((double) this.getYSpace()) / LABEL_NUMBER;

		for (int i = 0; i <= LABEL_NUMBER; i++) {
			g.drawString(Integer.toString(((int) (i * incValue))), x_POP,
					this.getReversedY(Y_MARGIN + (int) (i * incCoordinate)));
		}
	}

	private void drawDFLLabel(final Graphics g, final int data_space) {

		final int x_DFL = X_MARGIN + 5 * data_space + LABEL_MARGIN;
		final double incValue = ((double) this.maxDFL) / LABEL_NUMBER;
		final double incCoordinate = ((double) this.getYSpace()) / LABEL_NUMBER;

		for (int i = 0; i <= LABEL_NUMBER; i++) {
			g.drawString(Integer.toString(((int) (i * incValue))), x_DFL,
					this.getReversedY(Y_MARGIN + (int) (i * incCoordinate)));
		}
	}

	private int getReversedY(final int y) {
		return this.getHeight() - y;
	}

	private int getPropertyMaxNumber(int i) {

		if (i <= 10)
			return 10;
		// else if( i <= 15 ) return 15;
		else if (i <= 20)
			return 20;
		// else if( i <= 25 ) return 25;
		else if (i <= 30)
			return 30;
		// else if( i <= 35 ) return 35;
		else if (i <= 40)
			return 40;
		// else if( i <= 45 ) return 45;
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
