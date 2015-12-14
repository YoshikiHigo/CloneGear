package yoshikihigo.clonegear.gui.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public final class RNRValue extends Observable {

	public static RNRValue getInstance(final String label) {
		RNRValue instance = INSTANCES.get(label);
		if (null == instance) {
			instance = new RNRValue(label);
			INSTANCES.put(label, instance);
		}
		return instance;
	}

	public void set(final int rnr) {
		this.rnr = rnr;
		this.setChanged();
		this.notifyObservers();
	}

	public String getLabel() {
		return this.label;
	}

	public int get() {
		return this.rnr;
	}

	private RNRValue(final String label) {
		this.rnr = 50;
		this.label = label;
	}

	private int rnr;
	private final String label;

	private static final Map<String, RNRValue> INSTANCES = new HashMap<>();
}
