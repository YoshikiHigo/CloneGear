package yoshikihigo.clonegear.gui.view.visual.scatterplot;


/**
 * @author y-higo 2005-11-08 : add
 */
public class DraggedArea {

    // these variables are for dragged area
    private int pressedTokenX;

    private int pressedTokenY;

    private int draggedTokenX;

    private int draggedTokenY;

    private int pressedPositionX;

    private int pressedPositionY;

    private int draggedPositionX;

    private int draggedPositionY;

    public DraggedArea() {
        this.pressedTokenX = 0;
        this.pressedTokenY = 0;
        this.pressedPositionX = 0;
        this.pressedPositionY = 0;

        this.draggedTokenX = 0;
        this.draggedTokenY = 0;
        this.draggedPositionX = 0;
        this.draggedPositionY = 0;
    }

    public void setPressedToken(int x, int y) {
        this.pressedTokenX = x;
        this.pressedTokenY = y;
    }

    public void setPressedPosition(int x, int y) {
        this.pressedPositionX = x;
        this.pressedPositionY = y;
    }

    public void setDraggedToken(int x, int y) {
        this.draggedTokenX = x;
        this.draggedTokenY = y;
    }

    public void setDraggedPosition(int x, int y) {
        this.draggedPositionX = x;
        this.draggedPositionY = y;
    }

    public int getStartTokenX() {
        if (this.getPressedTokenX() < this.getDraggedTokenX())
            return this.getPressedTokenX();
        else
            return this.getDraggedTokenX();
    }

    public int getStartTokenY() {
        if (this.getPressedTokenY() < this.getDraggedTokenY())
            return this.getPressedTokenY();
        else
            return this.getDraggedTokenY();
    }

    public int getEndTokenX() {
        if (this.getPressedTokenX() < this.getDraggedTokenX())
            return this.getDraggedTokenX();
        else
            return this.getPressedTokenX();
    }

    public int getEndTokenY() {
        if (this.getPressedTokenY() < this.getDraggedTokenY())
            return this.getDraggedTokenY();
        else
            return this.getPressedTokenY();
    }

    public int getStartPositionX() {
        if (this.getPressedPositionX() < this.getDraggedPositionX())
            return this.getPressedPositionX();
        else
            return this.getDraggedPositionX();
    }

    public int getStartPositionY() {
        if (this.getPressedPositionY() < this.getDraggedPositionY())
            return this.getPressedPositionY();
        else
            return this.getDraggedPositionY();
    }

    public int getEndPositionX() {
        if (this.getPressedPositionX() < this.getDraggedPositionX())
            return this.getDraggedPositionX();
        else
            return this.getPressedPositionX();
    }

    public int getEndPositionY() {
        if (this.getPressedPositionY() < this.getDraggedPositionY())
            return this.getDraggedPositionY();
        else
            return this.getPressedPositionY();
    }

    public int getPressedTokenX() {
        return this.pressedTokenX;
    }

    public int getPressedTokenY() {
        return this.pressedTokenY;
    }

    public int getDraggedTokenX() {
        return this.draggedTokenX;
    }

    public int getDraggedTokenY() {
        return this.draggedTokenY;
    }

    public int getPressedPositionX() {
        return this.pressedPositionX;
    }

    public int getPressedPositionY() {
        return this.pressedPositionY;
    }

    public int getDraggedPositionX() {
        return this.draggedPositionX;
    }

    public int getDraggedPositionY() {
        return this.draggedPositionY;
    }

    public void clear() {

        this.pressedTokenX = 0;
        this.pressedTokenY = 0;
        this.pressedPositionX = 0;
        this.pressedPositionY = 0;

        this.draggedTokenX = 0;
        this.draggedTokenY = 0;
        this.draggedPositionX = 0;
        this.draggedPositionY = 0;
    }
}
