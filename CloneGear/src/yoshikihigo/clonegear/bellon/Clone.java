package yoshikihigo.clonegear.bellon;

import java.util.SortedSet;
import java.util.TreeSet;

public class Clone {

	final public String path;
	final private SortedSet<Integer> lines;

	public Clone(final String path, final int startline, final int endline){
		this(path, startline, endline, null);
	}
	
	public Clone(final String path, final int startline, final int endline,
			final SortedSet<Integer> gaps) {
		this.path = path;
		this.lines = new TreeSet<>();
		for (int index = startline; index <= endline; index++) {
			this.lines.add(index);
		}
		if (null != gaps) {
			this.lines.removeAll(gaps);
		}
	}

	public SortedSet<Integer> getLines() {
		final SortedSet<Integer> lines = new TreeSet<>();
		lines.addAll(this.lines);
		return lines;
	}
}
