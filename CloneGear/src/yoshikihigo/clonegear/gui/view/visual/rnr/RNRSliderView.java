package yoshikihigo.clonegear.gui.view.visual.rnr;


import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;


/**
 * @author y-higo
 */
public class RNRSliderView extends JPanel implements Observer, VisualViewInterface {

    public static final int DEFAULT_VALUE = 50;

    final private JSlider slider;

    public RNRSliderView() {

        this.setBorder(new TitledBorder(new LineBorder(java.awt.Color.black), "Threshold of RNR"));

        this.setLayout(new java.awt.BorderLayout());

        this.slider = new javax.swing.JSlider(0, 100, DEFAULT_VALUE);
        this.slider.setPaintLabels(true);
        this.slider.setPaintTicks(true);
        this.slider.setPaintTrack(true);
        this.slider.setMajorTickSpacing(10);
        this.slider.setMinorTickSpacing(1);
        this.slider.setSnapToTicks(true);
        this.add(this.slider, java.awt.BorderLayout.CENTER);

        this.slider.addChangeListener(new ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                if (!RNRSliderView.this.slider.getValueIsAdjusting()) {
                    RNRSliderView.this.slider.setEnabled(false);
                    final int rnr = RNRSliderView.this.slider.getValue();
                    RNRValue.getInstance(RNR).set(rnr);
                    RNRSliderView.this.slider.setEnabled(true);
                }
            }
        });
    }

    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub

    }

    public void reset() {
        this.slider.setValue(DEFAULT_VALUE);
    }
}
