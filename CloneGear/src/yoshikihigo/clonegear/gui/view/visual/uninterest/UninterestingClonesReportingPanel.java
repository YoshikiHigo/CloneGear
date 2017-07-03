package yoshikihigo.clonegear.gui.view.visual.uninterest;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;

import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;

public class UninterestingClonesReportingPanel extends JPanel implements Observer, VisualViewInterface {

	final JButton reportingButton;
	final JButton cancelButton;
	GUIClonePair clonepair;

	public UninterestingClonesReportingPanel() {
		super(new GridLayout(1, 2));
		this.reportingButton = new JButton("Report as Uninteresting");
		this.cancelButton = new JButton("Cancel Reporting");
		this.clonepair = null;
		this.add(this.reportingButton);
		this.add(this.cancelButton);
		this.reportingButton.setEnabled(false);
		this.cancelButton.setEnabled(false);

		this.reportingButton.addActionListener(e -> {
			if (null == this.clonepair) {
				return;
			}
		});

		this.cancelButton.addActionListener(e -> {
			if (null == this.clonepair) {
				return;
			}
		});
	}

	@Override
	public void update(final Observable o, final Object arg) {

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;

			if (selectedEntities.getLabel().equals(CLONEPAIR)) {

				if (selectedEntities.isSet()) {
					this.clonepair = (GUIClonePair) selectedEntities.get().get(0);
					this.reportingButton.setEnabled(true);
					this.cancelButton.setEnabled(true);
				} else {
					clonepair = null;
					this.reportingButton.setEnabled(false);
					this.cancelButton.setEnabled(false);
				}
			}
		}
	}
}
