package yoshikihigo.clonegear.gui.data.clone;

import java.util.Collection;

public class CloneMetricsMaxValues {

	public static final CloneMetricsMaxValues SINGLETON = new CloneMetricsMaxValues();

	public void initialize(final Collection<GUICloneSet> clonesets) {
		assert !this.initialized : "CloneMetricsMaxValues has already initialized.";

		for (final GUICloneSet cloneset : clonesets) {
			if (this.maxDFL < cloneset.getDFL()) {
				this.maxDFL = cloneset.getDFL();
			}
			if (this.maxLEN < cloneset.getLEN()) {
				this.maxLEN = cloneset.getLEN();
			}
			if (this.maxNIF < cloneset.getNIF()) {
				this.maxNIF = cloneset.getNIF();
			}
			if (this.maxPOP < cloneset.getPOP()) {
				this.maxPOP = cloneset.getPOP();
			}
			if (this.maxRAD < cloneset.getRAD()) {
				this.maxRAD = cloneset.getRAD();
			}
			if (this.maxRNR < cloneset.getRNR()) {
				this.maxRNR = cloneset.getRNR();
			}
		}

		this.initialized = true;
	}

	public int getMaxDFL() {
		assert this.initialized : "CloneMetricsMaxValues was not initialized.";
		return this.maxDFL;
	}

	public int getMaxLEN() {
		assert this.initialized : "CloneMetricsMaxValues was not initialized.";
		return this.maxLEN;
	}

	public int getMaxNIF() {
		assert this.initialized : "CloneMetricsMaxValues was not initialized.";
		return this.maxNIF;
	}

	public int getMaxPOP() {
		assert this.initialized : "CloneMetricsMaxValues was not initialized.";
		return this.maxPOP;
	}

	public int getMaxRAD() {
		assert this.initialized : "CloneMetricsMaxValues was not initialized.";
		return this.maxRAD;
	}

	public int getMaxRNR() {
		assert this.initialized : "CloneMetricsMaxValues was not initialized.";
		return this.maxRNR;
	}

	private CloneMetricsMaxValues() {
		this.maxDFL = 0;
		this.maxLEN = 0;
		this.maxNIF = 0;
		this.maxPOP = 0;
		this.maxRAD = 0;
		this.maxRNR = 0;
		this.initialized = false;
	}

	private int maxDFL;
	private int maxLEN;
	private int maxNIF;
	private int maxPOP;
	private int maxRAD;
	private int maxRNR;
	private boolean initialized;
}
