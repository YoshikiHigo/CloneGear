package yoshikihigo.clonegear.gui.view.quantity.statistic.roc;


import java.awt.Color;


public interface ROCPanelConst {

    int RANGE_NUMBER = 10;

    int LINE_NUMBER = 5;

    int X_MARGIN = 50;

    int Y_MARGIN = 50;

    int BAR_MARGIN = 5;

    int RANGE_LABEL_X_MARGIN = 1;

    int RANGE_LABEL_Y_MARGIN = 30;

    int NUMBER_LABEL_X_MARGIN = 1;

    int NUMBER_LABEL_Y_MARGIN = 10;

    Color BG_COLOR = Color.white;

    Color STATUS_COLOR = Color.black;

    Color FRAME_COLOR = Color.black;

    Color UNFILTERED_BAR_COLOR = new Color(0, 0, 0, 200);

    Color FILTERED_BAR_COLOR = new Color(100, 100, 255, 200);

    String[] RANGE_TITLE = { "0 - 10%", "11 - 20%", "21 - 30%", "31 - 40%", "41 - 50%", "51 - 60%",
            "61 - 70%", "71 - 80", "81 - 90", "91 -100%" };

    String X_TITLE = "Range of ROC";

    String Y_TITLE = "# of Files";
}
