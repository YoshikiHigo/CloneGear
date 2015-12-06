package yoshikihigo.clonegear.bellon;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Clone implements Comparable<Clone> {

	final public String path;
	final private SortedSet<Integer> lines;

	public Clone(final String path, final int startline, final int endline) {
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

	public List<Integer> getLines() {
		return new ArrayList<Integer>(this.lines);
	}

	@Override
	public int compareTo(final Clone o) {

		final int path = this.path.compareTo(o.path);
		if (0 != path) {
			return path;
		}

		final int fromline1 = this.lines.first();
		final int fromline2 = o.lines.first();
		final int toline1 = this.lines.last();
		final int toline2 = this.lines.last();
		if (fromline1 < fromline2) {
			return -1;
		} else if (fromline1 > fromline2) {
			return 1;
		} else if (toline1 < toline2) {
			return -1;
		} else if (toline1 > toline2) {
			return 1;
		} else {
			return 0;
		}
	}
}
