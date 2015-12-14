package yoshikihigo.clonegear.gui.view.visual.scatterplot;


import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * @author y-higo 2005-11-08 : add
 */
public class FileNameDisplayPanel extends JPanel {

    final private javax.swing.JTextField horizontalFileName;

    final private javax.swing.JTextField verticalFileName;

    public FileNameDisplayPanel(final ScatterPlotView parentContainer) {

        super();

        final JLabel horizontalLabel = new JLabel("Horizontal :");
        final JLabel verticalLabel = new JLabel("Vertical   :");

        this.horizontalFileName = new javax.swing.JTextField("");
        this.verticalFileName = new javax.swing.JTextField("");

        final JPanel horizontalPanel = new JPanel();
        final JPanel verticalPanel = new JPanel();

        horizontalPanel.setLayout(new BorderLayout());
        verticalPanel.setLayout(new BorderLayout());

        horizontalPanel.add(this.horizontalFileName, BorderLayout.CENTER);
        verticalPanel.add(this.verticalFileName, BorderLayout.CENTER);

        horizontalPanel.add(horizontalLabel, BorderLayout.WEST);
        verticalPanel.add(verticalLabel, BorderLayout.WEST);

        this.setLayout(new GridLayout(2, 1));
        this.add(horizontalPanel);
        this.add(verticalPanel);

        this.horizontalFileName.setEditable(false);
        this.verticalFileName.setEditable(false);
    }

    public void update(final String horizontalFileName, final String verticalFileName) {
        this.horizontalFileName.setText(horizontalFileName);
        this.verticalFileName.setText(verticalFileName);
    }
}
