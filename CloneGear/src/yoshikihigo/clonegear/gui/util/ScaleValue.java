package yoshikihigo.clonegear.gui.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class ScaleValue extends Observable {

	public static ScaleValue getInstance(final String label) {
		ScaleValue instance = INSTANCES.get(label);
		if (null == instance) {
			instance = new ScaleValue(label);
			INSTANCES.put(label, instance);
		}
		return instance;
	}

	public void set(final int scale) {
		this.scale = scale;
		this.setChanged();
		this.notifyObservers();
	}

	public String getLabel() {
		return this.label;
	}

	public int get() {
		return this.scale;
	}

	private ScaleValue(final String label) {
		this.scale = 1;
		this.label = label;
	}

	private int scale;
	private final String label;

	private static final Map<String, ScaleValue> INSTANCES = new HashMap<>();
}
