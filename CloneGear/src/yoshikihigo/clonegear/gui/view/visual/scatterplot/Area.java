package yoshikihigo.clonegear.gui.view.visual.scatterplot;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author y-higo 2005-11-08 : add
 */
class Area {

    final private static Logger logger = Logger.getLogger("ScatterPlotPanel");

    final private int startIndex;

    final private int endIndex;

    final private int restartIndex;

    final private int interruptIndex;

    Area(final int startIndex, final int interruptIndex, final int restartIndex, final int endIndex) {

        logger.log(Level.INFO, "start");

        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.restartIndex = restartIndex;
        this.interruptIndex = interruptIndex;

        logger.log(Level.INFO, "end");
    }

    int getStartIndex() {
        return this.startIndex;
    }

    int getEndIndex() {
        return this.endIndex;
    }

    int getRestartIndex() {
        return this.restartIndex;
    }

    int getInterruptIndex() {
        return this.interruptIndex;
    }
}
