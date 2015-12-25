package yoshikihigo.clonegear.gui.view.metric.scatterplot;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileNameDisplayPanel extends JPanel {

	final private JTextField horizontalFileName;
	final private JTextField verticalCloneSet;

	public FileNameDisplayPanel(final ScatterPlotView parentContainer) {

		super();

		final JLabel horizontalLabel = new JLabel("Horizontal :");
		final JLabel verticalLabel = new JLabel("Vertical   :");

		this.horizontalFileName = new JTextField("");
		this.verticalCloneSet = new JTextField("");

		final JPanel horizontalPanel = new JPanel();
		final JPanel verticalPanel = new JPanel();

		horizontalPanel.setLayout(new BorderLayout());
		verticalPanel.setLayout(new BorderLayout());

		horizontalPanel.add(this.horizontalFileName, BorderLayout.CENTER);
		verticalPanel.add(this.verticalCloneSet, BorderLayout.CENTER);

		horizontalPanel.add(horizontalLabel, BorderLayout.WEST);
		verticalPanel.add(verticalLabel, BorderLayout.WEST);

		this.setLayout(new GridLayout(2, 1));
		this.add(horizontalPanel);
		this.add(verticalPanel);

		this.horizontalFileName.setEditable(false);
		this.verticalCloneSet.setEditable(false);
	}

	public void update(final String horizontalFileName,
			final String verticalCloneSet) {
		this.horizontalFileName.setText(horizontalFileName);
		this.verticalCloneSet.setText(verticalCloneSet);
	}
}
