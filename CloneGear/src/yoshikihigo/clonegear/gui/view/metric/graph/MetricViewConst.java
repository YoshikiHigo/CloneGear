package yoshikihigo.clonegear.gui.view.metric.graph;


import java.awt.Cursor;
import java.awt.Font;


public interface MetricViewConst {

    int DATA_NUM = 4;

    int X_MARGIN = 50;

    int Y_MARGIN = 50;

    int TITLE_MARGIN = 20;

    int LABEL_MARGIN = 5;

    int LABEL_NUMBER = 10;

    Font AXIS_SCALE_FONT = new Font("Metric View", Font.BOLD, 12);

    Font AXIS_TITLE_FONT = new Font("Metric View", Font.BOLD, 12);

    String[] AXIS_TITLE = { "RAD", "LEN", "RNR", "NIF", "POP", "DFL" };

    Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
}
