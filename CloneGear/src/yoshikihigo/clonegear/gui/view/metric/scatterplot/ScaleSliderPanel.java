package yoshikihigo.clonegear.gui.view.metric.scatterplot;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import yoshikihigo.clonegear.gui.util.ScaleValue;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;

public class ScaleSliderPanel extends JSlider implements Observer,
		MetricViewInterface {

	public static final int DEFAULT_VALUE = 1;

	public ScaleSliderPanel() {

		super(1, 10, DEFAULT_VALUE);

		this.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Scale Ratio"));
		this.setPaintLabels(true);
		this.setPaintTicks(true);
		this.setPaintTrack(true);
		this.setMajorTickSpacing(1);
		this.setMinorTickSpacing(1);
		this.setSnapToTicks(true);

		this.addChangeListener(e -> {
			if (!ScaleSliderPanel.this.getValueIsAdjusting()) {
				ScaleSliderPanel.this.setEnabled(false);
				final int scale = ScaleSliderPanel.this.getValue();
				ScaleValue.getInstance(SCALE).set(scale);
				ScaleSliderPanel.this.setEnabled(true);
			}
		});
	}

	@Override
	public void update(final Observable o, final Object arg) {
		final int scale = ScaleValue.getInstance(SCALE).get();
		this.setValue(scale);
	}

	public void reset() {
		this.setValue(DEFAULT_VALUE);
	}
}
