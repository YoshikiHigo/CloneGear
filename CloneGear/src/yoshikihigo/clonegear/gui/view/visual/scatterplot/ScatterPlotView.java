package yoshikihigo.clonegear.gui.view.visual.scatterplot;


import java.awt.BorderLayout;

import javax.swing.JPanel;

import yoshikihigo.clonegear.gui.data.clone.GUIClonePair;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;


/**
 * @author y-higo 2005-11-08 : modify
 */
public class ScatterPlotView extends JPanel implements VisualViewInterface {

    public ScatterPlotView() {

        this.setLayout(new BorderLayout());

        final FileNameDisplayPanel fileNameDisplayPanel = new FileNameDisplayPanel(this);
        this.add(fileNameDisplayPanel, java.awt.BorderLayout.NORTH);

        this.scatterPlotPanel = new ScatterPlotPanel(fileNameDisplayPanel);
        SelectedEntities.<GUIFile> getInstance(HORIZONTAL_FILE).addObserver(this.scatterPlotPanel);
        SelectedEntities.<GUIFile> getInstance(VERTICAL_FILE).addObserver(this.scatterPlotPanel);
        SelectedEntities.<GUIClonePair> getInstance(CLONEPAIR).addObserver(this.scatterPlotPanel);
        RNRValue.getInstance(RNR).addObserver(this.scatterPlotPanel);
        UninterestingClonesDisplay.getInstance(UNINTERESTING).addObserver(this.scatterPlotPanel);

        this.add(this.scatterPlotPanel, java.awt.BorderLayout.CENTER);
    }

    public void init() {
    }

    public void reset() {
        this.scatterPlotPanel.reset();
    }

    final private ScatterPlotPanel scatterPlotPanel;
}
