package yoshikihigo.clonegear.gui.util;


import java.util.HashMap;
import java.util.Map;
import java.util.Observable;


public class UninterestingClonesDisplay extends Observable {

    public static UninterestingClonesDisplay getInstance(final String label) {

        if (null == label) {
            throw new NullPointerException();
        }

        UninterestingClonesDisplay instance = INSTANCES.get(label);
        if (null == instance) {
            instance = new UninterestingClonesDisplay(label);
            INSTANCES.put(label, instance);
        }

        return instance;
    }

    public void set(final boolean display) {

        this.display = display;

        this.setChanged();
        this.notifyObservers();
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isDisplay() {
        return this.display;
    }

    private UninterestingClonesDisplay(final String label) {
        this.display = true;
        this.label = label;
    }

    private boolean display;

    private final String label;

    private static final Map<String, UninterestingClonesDisplay> INSTANCES = new HashMap<String, UninterestingClonesDisplay>();
}
