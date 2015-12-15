package yoshikihigo.clonegear.gui.data.clone;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import yoshikihigo.clonegear.gui.data.Entity;
import yoshikihigo.clonegear.gui.data.HavingClones;

public class GUICloneSet implements Comparable<GUICloneSet>, Entity,
		HavingClones {

	public GUICloneSet(final int id) {
		this.id = id;
		this.clonepairs = new ArrayList<>();
	}

	public void addClonepair(final GUIClonePair clonepair) {
		this.clonepairs.add(clonepair);
	}

	public List<GUIClonePair> getClonepairs() {
		return new ArrayList<GUIClonePair>(this.clonepairs);
	}

	@Override
	public List<GUIClone> getClones() {
		final SortedSet<GUIClone> clones = new TreeSet<>();
		this.clonepairs.stream().forEach(clonepair -> {
			clones.add(clonepair.left);
			clones.add(clonepair.right);
		});
		return new ArrayList<>(clones);
	}

	public final GUIClone first() {
		final List<GUIClone> clones = this.getClones();
		return clones.get(0);
	}

	public final GUIClone last() {
		final List<GUIClone> clones = this.getClones();
		return clones.get(clones.size() - 1);
	}

	public final int size() {
		return this.getClones().size();
	}

	public final int getDFL() {
		return GUICloneMetricsManager.SINGLETON.getDFL(this);
	}

	public final int getLEN() {
		return GUICloneMetricsManager.SINGLETON.getLEN(this);
	}

	public final int getNIF() {
		return GUICloneMetricsManager.SINGLETON.getNIF(this);
	}

	public final int getPOP() {
		return GUICloneMetricsManager.SINGLETON.getPOP(this);
	}

	public final int getRAD() {
		return GUICloneMetricsManager.SINGLETON.getRAD(this);
	}

	public final int getRNR() {
		return GUICloneMetricsManager.SINGLETON.getRNR(this);
	}

	@Override
	public int compareTo(final GUICloneSet target) {
		return Integer.compare(this.id, target.id);
	}

	@Override
	public boolean equals(final Object o) {

		if (null == o) {
			return false;
		}

		if (!(o instanceof GUICloneSet)) {
			return false;
		}

		final GUICloneSet target = (GUICloneSet) o;
		return this.clonepairs.equals(target.clonepairs);
	}

	@Override
	public int hashCode() {
		return this.clonepairs.hashCode();
	}

	public final int id;
	private final ArrayList<GUIClonePair> clonepairs;
}
