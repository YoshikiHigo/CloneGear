package yoshikihigo.clonegear.gui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import yoshikihigo.clonegear.gui.data.Entity;

public final class SelectedEntities<T extends Entity> extends Observable {

	public static final <S extends Entity> SelectedEntities<S> getInstance(
			final String label) {
		SelectedEntities<S> instance = (SelectedEntities<S>) INSTANCES
				.get(label);
		if (null == instance) {
			instance = new SelectedEntities<S>(label);
			INSTANCES.put(label, instance);
		}
		return instance;
	}

	public void add(final T entity, final Observer source) {

		this.selectedEntities.add(entity);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);
	}

	public void addAll(final Collection<T> entities, final Observer source) {

		this.selectedEntities.addAll(entities);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);
	}

	public void remove(final T entity, final Observer source) {

		this.selectedEntities.remove(entity);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);
	}

	public void removeAll(final Collection<T> entities, final Observer source) {

		this.selectedEntities.removeAll(entities);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);
	}

	public void set(final T entity, final Observer source) {

		this.selectedEntities.clear();
		this.selectedEntities.add(entity);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);
	}

	public void setAll(final Collection<T> entities, final Observer source) {

		this.selectedEntities.clear();
		this.selectedEntities.addAll(entities);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);
	}

	public boolean isSet() {
		return !this.selectedEntities.isEmpty();
	}

	public void clear(final Observer source) {

		this.selectedEntities.clear();
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);
	}

	public List<T> get() {
		return new ArrayList<T>(this.selectedEntities);
	}

	public Observer getSource() {
		return this.source;
	}

	public String getLabel() {
		return this.label;
	}

	private static final Map<String, SelectedEntities<? extends Entity>> INSTANCES = new HashMap<>();

	private SelectedEntities(final String label) {
		this.selectedEntities = new TreeSet<T>();
		this.source = null;
		this.label = label;
	}

	private final SortedSet<T> selectedEntities;
	private Observer source;
	private final String label;
}
