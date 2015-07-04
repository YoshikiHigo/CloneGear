package yoshikihigo.clonegear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.data.Statement;

public class SmithWaterman {

	final private static AtomicLong CREATE_MATRIX_TIME = new AtomicLong(0);
	final private static AtomicLong DETECT_CLONE_TIME = new AtomicLong(0);

	public static long getMatrixCreationTime() {
		return CREATE_MATRIX_TIME.get();
	}

	public static long getCloneDetectionTime() {
		return DETECT_CLONE_TIME.get();
	}

	final private String path1;
	final private String path2;
	final private List<Statement> statements1;
	final private List<Statement> statements2;

	public SmithWaterman(final SourceFile file1, final SourceFile file2) {
		this.path1 = file1.path;
		this.path2 = file2.path;
		this.statements1 = file1.getStatements();
		this.statements2 = file2.getStatements();
	}

	public List<ClonedFragment> getClonedFragments() {

		if (this.statements1.isEmpty() || this.statements2.isEmpty()) {
			return new ArrayList<ClonedFragment>();
		}

		final long startCreatingMatrix = System.nanoTime();

		final Cell[][] table = new Cell[this.statements1.size()][this.statements2
				.size()];

		if (this.statements1.get(0).hash == this.statements2.get(0).hash) {
			table[0][0] = new Cell(2, true, 0, 0, null);
		} else {
			table[0][0] = new Cell(0, false, 0, 0, null);
		}

		for (int x = 1; x < statements1.size(); x++) {
			final boolean match = Arrays.equals(this.statements1.get(x).hash,
					this.statements2.get(0).hash);
			if (table[x - 1][0].value > 2) {
				final Cell base = table[x - 1][0];
				table[x][0] = new Cell(base.value - 1, match, x, 0, base);
			} else {
				table[x][0] = new Cell(match ? 2 : 0, match, x, 0, null);
			}
		}

		for (int y = 1; y < this.statements2.size(); y++) {
			final boolean match = Arrays.equals(this.statements1.get(0).hash,
					this.statements2.get(y).hash);
			if (table[0][y - 1].value > 2) {
				final Cell base = table[0][y - 1];
				table[0][y] = new Cell(base.value - 1, match, 0, y, base);
			} else {
				table[0][y] = new Cell(match ? 2 : 0, match, 0, y, null);
			}
		}

		for (int x = 1; x < this.statements1.size(); x++) {
			for (int y = 1; y < this.statements2.size(); y++) {

				final boolean match = Arrays.equals(
						this.statements1.get(x).hash,
						this.statements2.get(y).hash);
				final Cell left = table[x - 1][y];
				final Cell up = table[x][y - 1];
				final Cell upleft = table[x - 1][y - 1];
				final int leftValue = left.value - 1;
				final int upValue = up.value - 1;
				final int upleftValue = upleft.value + (match ? 2 : -1);

				if ((leftValue <= upleftValue) && (upValue <= upleftValue)) {
					table[x][y] = new Cell((upleftValue > 0 ? upleftValue : 0),
							match, x, y, (upleftValue > 0 ? upleft : null));
				}

				else if ((leftValue <= upValue) && (upleftValue <= upValue)) {
					table[x][y] = new Cell((upValue > 0 ? upValue : 0), match,
							x, y, (upValue > 0 ? up : null));
				}

				else if ((upValue <= leftValue) && (upleftValue <= leftValue)) {
					table[x][y] = new Cell((leftValue > 0 ? leftValue : 0),
							match, x, y, (leftValue > 0 ? left : null));
				}
			}
		}

		final long endCreatingMatrix = System.nanoTime();
		CREATE_MATRIX_TIME.addAndGet(endCreatingMatrix - startCreatingMatrix);

		final long startDetectingClones = System.nanoTime();

		final int threshold = Config.getInstance().getTHRESHOLD();
		final List<ClonedFragment> clonedFragments = new ArrayList<>();
		for (final Cell maxCell : this.getLocalMaximumCells(table)) {
			if (maxCell.isChecked()) {
				continue;
			}
			final Cell minCell = getMinCell(maxCell);
			final byte[][] cloneHash = getCloneHash(minCell, maxCell);

			final ClonedFragment xClonedFragment = getClonedFragment(path1,
					this.statements1, minCell.x, maxCell.x, cloneHash);
			final ClonedFragment yClonedFragment = getClonedFragment(path2,
					this.statements2, minCell.y, maxCell.y, cloneHash);
			if ((threshold <= xClonedFragment.getNumberOfTokens())
					&& (threshold <= yClonedFragment.getNumberOfTokens())) {
				clonedFragments.add(xClonedFragment);
				clonedFragments.add(yClonedFragment);
				switchToChecked(table, minCell.x, maxCell.x, minCell.y,
						maxCell.y);
			}
		}

		final long endDetectingClones = System.nanoTime();
		DETECT_CLONE_TIME.addAndGet(endDetectingClones - startDetectingClones);

		return clonedFragments;
	}

	private List<Cell> getLocalMaximumCells(final Cell[][] table) {
		final List<Cell> cells = new ArrayList<>();
		int x = table.length - 1;
		int y = table[0].length - 1;
		while ((0 < x) || (0 < y)) {
			if (this.isLocalMaximum(table, x, y)) {
				cells.add(table[x][y]);
			}

			for (int index = x - 1; 0 <= index; index--) {
				if (this.isLocalMaximum(table, index, y)) {
					cells.add(table[index][y]);
				}
			}

			for (int index = y - 1; 0 <= index; index--) {
				if (this.isLocalMaximum(table, x, index)) {
					cells.add(table[x][index]);
				}
			}

			x = (x > 0) ? x - 1 : 0;
			y = (y > 0) ? y - 1 : 0;
		}

		return cells;
	}

	private boolean isLocalMaximum(final Cell[][] table, final int x,
			final int y) {
		final int value = table[x][y].value;
		final int maxX = table.length - 1;
		final int maxY = table[0].length - 1;

		if ((0 < x) && (0 < y) && (table[x - 1][y - 1].value > value)) {
			return false;
		}

		if ((0 < x) && (table[x - 1][y].value > value)) {
			return false;
		}

		if ((0 < y) && (table[x][y - 1].value > value)) {
			return false;
		}

		if ((0 < x) && (y < maxY) && (table[x - 1][y + 1].value > value)) {
			return false;
		}

		if ((x < maxX) && (0 < y) && (table[x + 1][y - 1].value > value)) {
			return false;
		}

		if ((y < maxY) && (table[x][y + 1].value > value)) {
			return false;
		}

		if ((x < maxX) && (table[x + 1][y].value > value)) {
			return false;
		}

		if ((x < maxX) && (y < maxY) && (table[x + 1][y + 1].value > value)) {
			return false;
		}

		return true;
	}

	private Cell getMinCell(final Cell maxCell) {
		Cell minCell = maxCell;
		while (true) {
			if (0 == minCell.value) {
				break;
			}
			if (minCell.base.isChecked()) {
				break;
			}
			minCell = minCell.base;
		}
		return minCell;
	}

	private byte[][] getCloneHash(final Cell minCell, final Cell maxCell) {
		Cell cell = maxCell;
		final List<byte[]> list = new ArrayList<>();
		do {
			if (cell.match) {
				list.add(this.statements1.get(cell.x).hash);
			}
			cell = cell.base;
		} while ((null != cell) && (minCell.x <= cell.x)
				&& (minCell.y <= cell.y));
		return (byte[][]) list.toArray(new byte[][] {});
	}

	public ClonedFragment getClonedFragment(final String path,
			final List<Statement> statements, final int fromIndex,
			final int toIndex, final byte[][] cloneHash) {
		final List<Statement> clonedStatements = new ArrayList<>();
		for (int index = fromIndex; index <= toIndex; index++) {
			clonedStatements.add(statements.get(index));
		}
		return new ClonedFragment(cloneHash, path, clonedStatements);
	}

	private void switchToChecked(final Cell[][] table, final int fromX,
			final int toX, final int fromY, final int toY) {
		for (int x = fromX; x <= toX; x++) {
			for (int y = fromY; y <= toY; y++) {
				// assert !table[x][y].isChecked() :
				// "this cell must not be a checked-state.";
				table[x][y].switchToChecked();
			}
		}
	}
}

class Cell {

	final public int value;
	final public boolean match;
	final public int x;
	final public int y;
	final public Cell base;
	private boolean checked;

	public Cell(final int value, final boolean match, final int x, final int y,
			final Cell base) {
		this.value = value;
		this.match = match;
		this.x = x;
		this.y = y;
		this.base = base;
		this.checked = false;
	}

	public void switchToChecked() {
		this.checked = true;
	}

	public boolean isChecked() {
		return this.checked;
	}
}
