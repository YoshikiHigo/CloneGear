package yoshikihigo.clonegear.gui.data.clone;

import java.util.Collection;

public class CloneMetricsMaxValues {

	private static CloneMetricsMaxValues SINGLETON = null;

	public static CloneMetricsMaxValues instance(){
		assert null != SINGLETON : "SINGLETON is not initialized.";
		return SINGLETON;
	}
	
	public static void initialize(final Collection<GUICloneSet> clonesets) {
		SINGLETON = new CloneMetricsMaxValues();
		for (final GUICloneSet cloneset : clonesets) {
			if (SINGLETON.maxDFL < cloneset.getDFL()) {
				SINGLETON.maxDFL = cloneset.getDFL();
			}
			if (SINGLETON.maxLEN < cloneset.getLEN()) {
				SINGLETON.maxLEN = cloneset.getLEN();
			}
			if (SINGLETON.maxNIF < cloneset.getNIF()) {
				SINGLETON.maxNIF = cloneset.getNIF();
			}
			if (SINGLETON.maxPOP < cloneset.getPOP()) {
				SINGLETON.maxPOP = cloneset.getPOP();
			}
			if (SINGLETON.maxRAD < cloneset.getRAD()) {
				SINGLETON.maxRAD = cloneset.getRAD();
			}
			if (SINGLETON.maxRNR < cloneset.getRNR()) {
				SINGLETON.maxRNR = cloneset.getRNR();
			}
		}
	}

	public int getMaxDFL() {
		return this.maxDFL;
	}

	public int getMaxLEN() {
		return this.maxLEN;
	}

	public int getMaxNIF() {
		return this.maxNIF;
	}

	public int getMaxPOP() {
		return this.maxPOP;
	}

	public int getMaxRAD() {
		return this.maxRAD;
	}

	public int getMaxRNR() {
		return this.maxRNR;
	}

	private CloneMetricsMaxValues() {
		this.maxDFL = 0;
		this.maxLEN = 0;
		this.maxNIF = 0;
		this.maxPOP = 0;
		this.maxRAD = 0;
		this.maxRNR = 0;
	}

	private int maxDFL;
	private int maxLEN;
	private int maxNIF;
	private int maxPOP;
	private int maxRAD;
	private int maxRNR;
}
