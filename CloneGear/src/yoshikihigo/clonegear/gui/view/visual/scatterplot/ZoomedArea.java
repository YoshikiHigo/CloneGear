package yoshikihigo.clonegear.gui.view.visual.scatterplot;


/**
 * @author y-higo 2005-11-08 : modify
 */
public class ZoomedArea {

    // these variables are for selected area
    private int startTokenX;

    private int startTokenY;

    private int endTokenX;

    private int endTokenY;

    private int startPositionX;

    private int startPositionY;

    private int endPositionX;

    private int endPositionY;

    private int prevStartTokenX;

    private int prevStartTokenY;

    private int prevEndTokenX;

    private int prevEndTokenY;

    private int prevStartPositionX;

    private int prevStartPositionY;

    private int prevEndPositionX;

    private int prevEndPositionY;

    // the variable are for temporary
    private int tempTokenX;

    private int tempTokenY;

    private int tempPositionX;

    private int tempPositionY;

    public ZoomedArea(int elementNum) {
        this.startTokenX = 0;
        this.startTokenY = 0;
        this.startPositionX = 0;
        this.startPositionY = 0;

        this.endTokenX = elementNum;
        this.endTokenY = elementNum;
        this.endPositionX = 0;
        this.endPositionY = 0;
    }

    public void setPressedToken(int x, int y) {
        this.tempTokenX = x;
        this.tempTokenY = y;
    }

    public void setPressedPosition(int x, int y) {
        this.tempPositionX = x;
        this.tempPositionY = y;
    }

    public void setReleasedToken(int x, int y) {

        // back up privios value
        this.prevStartTokenX = this.startTokenX;
        this.prevStartTokenY = this.startTokenY;
        this.prevEndTokenX = this.endTokenX;
        this.prevEndTokenY = this.endTokenY;

        // set x axe
        if (this.tempTokenX < x) {
            this.startTokenX = this.tempTokenX;
            this.endTokenX = x;
        } else {
            this.startTokenX = x;
            this.endTokenX = this.tempTokenX;
        }

        // set y axe
        if (this.tempTokenY < y) {
            this.startTokenY = this.tempTokenY;
            this.endTokenY = y;
        } else {
            this.startTokenY = y;
            this.endTokenY = this.tempTokenY;
        }
    }

    public void setReleasedPosition(int x, int y) {

        // back up privios value
        this.prevStartPositionX = this.startPositionX;
        this.prevStartPositionY = this.startPositionY;
        this.prevEndPositionX = this.endPositionX;
        this.prevEndPositionY = this.endTokenY;

        // set x axe
        if (this.tempPositionX < x) {
            this.startPositionX = this.tempPositionX;
            this.endPositionX = x;
        } else {
            this.startPositionX = x;
            this.endPositionX = this.tempPositionX;
        }

        // set y axe
        if (this.tempPositionY < y) {
            this.startPositionY = this.tempPositionY;
            this.endPositionY = y;
        } else {
            this.startPositionY = y;
            this.endPositionY = this.tempPositionY;
        }
    }

    public int getHorizontalStartToken() {
        return this.startTokenX;
    }

    public int getHorizontalEndToken() {
        return this.endTokenX;
    }

    public int getVerticalStartToken() {
        return this.startTokenY;
    }

    public int getVerticalEndToken() {
        return this.endTokenY;
    }

    public int getHorizontalStartPosition() {
        return this.startPositionX;
    }

    public int getHorizontalEndPosition() {
        return this.endPositionX;
    }

    public int getVerticalStartPosition() {
        return this.startPositionY;
    }

    public int getVerticalEndPosition() {
        return this.endPositionY;
    }

    public void rollBack() {

        this.startTokenX = this.prevStartTokenX;
        this.startTokenY = this.prevStartTokenY;
        this.endTokenX = this.prevEndTokenX;
        this.endTokenY = this.prevEndTokenY;

        this.startPositionX = this.prevStartPositionX;
        this.startPositionY = this.prevStartPositionY;
        this.endPositionX = this.prevEndPositionX;
        this.endPositionY = this.prevEndPositionY;
    }
}
