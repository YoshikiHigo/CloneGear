package yoshikihigo.clonegear.gui.view.metric.rnr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;

public class RNRSliderView extends JPanel implements Observer,
		MetricViewInterface {

	public static final int DEFAULT_VALUE = 50;

	final private JSlider slider;

	public RNRSliderView() {

		this.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Threshold of RNR"));

		this.setLayout(new BorderLayout());

		this.slider = new javax.swing.JSlider(0, 100, DEFAULT_VALUE);
		this.slider.setPaintLabels(true);
		this.slider.setPaintTicks(true);
		this.slider.setPaintTrack(true);
		this.slider.setMajorTickSpacing(10);
		this.slider.setMinorTickSpacing(1);
		this.slider.setSnapToTicks(true);
		this.add(this.slider, BorderLayout.CENTER);

		this.slider.addChangeListener(e -> {
			if (!RNRSliderView.this.slider.getValueIsAdjusting()) {
				RNRSliderView.this.slider.setEnabled(false);
				final int rnr = RNRSliderView.this.slider.getValue();
				RNRValue.getInstance(RNR).set(rnr);
				RNRSliderView.this.slider.setEnabled(true);
			}
		});
	}

	@Override
	public void update(final Observable o, final Object arg) {
	}

	public void reset() {
		this.slider.setValue(DEFAULT_VALUE);
	}
}
