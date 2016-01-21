package yoshikihigo.clonegear.gui.view.visual.scatterplot;

class Area {

	final public int startIndex;
	final public int endIndex;
	final public int restartIndex;
	final public int interruptIndex;

	Area(final int startIndex, final int interruptIndex,
			final int restartIndex, final int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.restartIndex = restartIndex;
		this.interruptIndex = interruptIndex;
	}
}
