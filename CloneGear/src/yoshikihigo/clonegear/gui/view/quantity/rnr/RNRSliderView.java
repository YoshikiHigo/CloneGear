package yoshikihigo.clonegear.gui.view.quantity.rnr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class RNRSliderView extends JPanel implements Observer,
		QuantitativeViewInterface {

	public static final int DEFAULT_VALUE = 50;

	public RNRSliderView() {

		this.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Threshold of RNR"));

		this.setLayout(new BorderLayout());

		this.slider = new JSlider(0, 100, DEFAULT_VALUE);
		this.slider.setPaintLabels(true);
		this.slider.setPaintTicks(true);
		this.slider.setPaintTrack(true);
		this.slider.setMajorTickSpacing(10);
		this.slider.setMinorTickSpacing(1);
		this.slider.setSnapToTicks(true);
		this.add(this.slider, BorderLayout.CENTER);

		this.slider.addChangeListener(e -> {
			if (!this.slider.getValueIsAdjusting()) {
				this.slider.setEnabled(false);
				final int rnr = this.slider.getValue();
				RNRValue.getInstance(RNR).set(rnr);
				slider.setEnabled(true);
			}
		});
	}

	public void update(Observable o, Object arg) {

	}

	public void reset() {
		this.slider.setValue(DEFAULT_VALUE);
	}

	final private JSlider slider;
}
