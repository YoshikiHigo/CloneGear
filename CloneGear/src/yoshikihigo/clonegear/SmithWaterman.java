package yoshikihigo.clonegear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.SourceFile;
import yoshikihigo.clonegear.data.Statement;

public class SmithWaterman {

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

		final List<ClonedFragment> clonedFragments = new ArrayList<>();

		for (Cell maxCell = getMaxCell(table); 0 < maxCell.value; maxCell = getMaxCell(table)) {
			final Cell minCell = getMinCell(maxCell);
			final byte[][] cloneHash = getCloneHash(minCell, maxCell);
			final ClonedFragment xClonedFragment = getClonedFragment(path1,
					this.statements1, minCell.x, maxCell.x, cloneHash);
			final ClonedFragment yClonedFragment = getClonedFragment(path2,
					this.statements2, minCell.y, maxCell.y, cloneHash);
			if ((0 <= xClonedFragment.getNumberOfTokens())
					&& (0 <= yClonedFragment.getNumberOfTokens())) {
				clonedFragments.add(xClonedFragment);
				clonedFragments.add(yClonedFragment);
				switchToChecked(table, minCell.x, maxCell.x, minCell.y,
						maxCell.y);
			}
		}

		return clonedFragments;
	}

	private Cell getMaxCell(final Cell[][] table) {
		Cell maxCell = table[0][0];
		int x = table.length - 1;
		int y = table[0].length - 1;
		while ((0 < x) || (0 < y)) {

			if (!table[x][y].isChecked() && (maxCell.value < table[x][y].value)) {
				maxCell = table[x][y];
			}

			for (int index = x - 1; 0 <= index; index--) {
				if (!table[index][y].isChecked()
						&& (maxCell.value < table[index][y].value)) {
					maxCell = table[index][y];
				}
			}

			for (int index = y - 1; 0 <= index; index--) {
				if (!table[x][index].isChecked()
						&& (maxCell.value < table[x][index].value)) {
					maxCell = table[x][index];
				}
			}

			x = (x > 0) ? x - 1 : 0;
			y = (y > 0) ? y - 1 : 0;
		}

		return maxCell;
	}

	private Cell getMinCell(final Cell maxCell) {
		Cell minCell = maxCell;
		while(true){
			if(0 == minCell.value){
				break;
			}
			if(minCell.base.isChecked()){
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
				//assert !table[x][y].isChecked() : "this cell must not be a checked-state.";
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
