package yoshikihigo.clonegear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import yoshikihigo.clonegear.data.ClonedFragment;
import yoshikihigo.clonegear.data.Statement;
import yoshikihigo.clonegear.lexer.token.Token;

public class SmithWaterman {

	public static List<ClonedFragment> getClonedFragments(
			final List<Statement> statements1,
			final List<Statement> statements2, final String path1,
			final String path2) {

		if (statements1.isEmpty() || statements2.isEmpty()) {
			return new ArrayList<ClonedFragment>();
		}

		final Cell[][] table = new Cell[statements1.size()][statements2.size()];

		if (statements1.get(0).hash == statements2.get(0).hash) {
			table[0][0] = new Cell(2, true, 0, 0, null);
		} else {
			table[0][0] = new Cell(0, false, 0, 0, null);
		}

		for (int x = 1; x < statements1.size(); x++) {
			final boolean match = Arrays.equals(statements1.get(x).hash,
					statements2.get(0).hash);
			if (table[x - 1][0].value > 2) {
				final Cell base = table[x - 1][0];
				table[x][0] = new Cell(base.value - 1, match, x, 0, base);
			} else {
				table[x][0] = new Cell(match ? 2 : 0, match, x, 0, null);
			}
		}

		for (int y = 1; y < statements2.size(); y++) {
			final boolean match = Arrays.equals(statements1.get(0).hash,
					statements2.get(y).hash);
			if (table[0][y - 1].value > 2) {
				final Cell base = table[0][y - 1];
				table[0][y] = new Cell(base.value - 1, match, 0, y, base);
			} else {
				table[0][y] = new Cell(match ? 2 : 0, match, 0, y, null);
			}
		}

		for (int x = 1; x < statements1.size(); x++) {
			for (int y = 1; y < statements2.size(); y++) {
				final boolean match = Arrays.equals(statements1.get(0).hash,
						statements2.get(y).hash);
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

		final List<Change> changes = new ArrayList<Change>();
		Cell current = table[array1.size() - 1][array2.size() - 1];
		final SortedSet<Integer> xdiff = new TreeSet<Integer>();
		final SortedSet<Integer> ydiff = new TreeSet<Integer>();
		while (true) {

			if (current.match) {

				if (!xdiff.isEmpty() || !ydiff.isEmpty()) {
					final List<Statement> xStatements = xdiff.isEmpty() ? Collections
							.<Statement> emptyList() : array1.subList(
							xdiff.first(), xdiff.last() + 1);
					final List<Statement> yStatements = ydiff.isEmpty() ? Collections
							.<Statement> emptyList() : array2.subList(
							ydiff.first(), ydiff.last() + 1);
					final List<Token> xTokens = getTokens(xStatements);
					final List<Token> yTokens = getTokens(yStatements);
					final DiffType diffType = getType(xTokens, yTokens);

					final Code beforeCodeFragment = new Code(software,
							xStatements);
					final Code afterCodeFragment = new Code(software,
							yStatements);
					final ChangeType changeType = beforeCodeFragment.text
							.isEmpty() ? ChangeType.ADD
							: afterCodeFragment.text.isEmpty() ? ChangeType.DELETE
									: ChangeType.REPLACE;
					final Change change = new Change(software, filepath,
							beforeCodeFragment, afterCodeFragment, revision,
							changeType, diffType);
					changes.add(change);
					xdiff.clear();
					ydiff.clear();
				}

			} else {
				final Cell previous = current.base;
				if (null != previous) {
					if (previous.x < current.x) {
						xdiff.add(current.x);
					}
					if (previous.y < current.y) {
						ydiff.add(current.y);
					}
				}
			}

			if (null != current.base) {
				current = current.base;
			} else {
				break;
			}
		}

		return changes;
	}

	private Cell getMaxCell(final Cell[][] table) {
		Cell maxCell = table[0][0];
		for (int x = table.length - 1; 0 <= x; x--) {
			for (int y = table[0].length - 1; 0 <= y; y--) {
				if (!table[x][y].isChecked()
						&& (maxCell.value < table[x][y].value)) {
					maxCell = table[x][y];
				}
			}
		}
		return maxCell;
	}

	private Cell getMinCell(final Cell maxCell) {
		Cell minCell = maxCell;
		while (0 < minCell.value) {
			minCell = minCell.base;
		}
		return minCell;
	}

	public ClonedFragment getClonedFragment(final String path,
			final List<Statement> statements, final int fromIndex,
			final int toIndex) {
		final List<Statement> clonedStatements = new ArrayList<>();
		for (int index = fromIndex; index <= toIndex; index++) {
			clonedStatements.add(statements.get(index));
		}
		new ClonedFragment(path, clonedStatements);
	}

	private void switchToChecked(final Cell[][] table, final int fromX,
			final int toX, final int fromY, final int toY) {
		for (int x = fromX; x <= toX; x++) {
			for (int y = fromY; y <= toY; y++) {
				assert !table[x][y].isChecked() : "this cell must not be a checked-state.";
				table[x][y].switchToChecked();
			}
		}
	}

	public static List<Token> getTokens(final List<Statement> statements) {
		final List<Token> tokens = new ArrayList<Token>();
		for (final Statement statement : statements) {
			tokens.addAll(statement.tokens);
		}
		return tokens;
	}

	public static DiffType getType(final List<Token> tokens1,
			final List<Token> tokens2) {

		if (tokens1.isEmpty() || tokens2.isEmpty()
				|| tokens1.size() != tokens2.size()) {
			return DiffType.TYPE3;
		}

		final Cell[][] table = new Cell[tokens1.size()][tokens2.size()];

		{

			if (tokens1.get(0).getClass() == tokens2.get(0).getClass()) {
				table[0][0] = new Cell(1, true, 0, 0, null);
			} else {
				table[0][0] = new Cell(0, false, 0, 0, null);
			}
			for (int x = 1; x < tokens1.size(); x++) {
				if (tokens1.get(x).getClass() == tokens2.get(0).getClass()) {
					table[x][0] = new Cell(1, true, x, 0, null);
				} else {
					table[x][0] = new Cell(table[x - 1][0].value, false, x, 0,
							table[x - 1][0]);
				}
			}
			for (int y = 1; y < tokens2.size(); y++) {
				if (tokens1.get(0).getClass() == tokens2.get(y).getClass()) {
					table[0][y] = new Cell(1, true, 0, y, null);
				} else {
					table[0][y] = new Cell(table[0][y - 1].value, false, 0, y,
							table[0][y - 1]);
				}
			}
			for (int x = 1; x < tokens1.size(); x++) {
				for (int y = 1; y < tokens2.size(); y++) {
					final Cell left = table[x - 1][y];
					final Cell up = table[x][y - 1];
					final Cell upleft = table[x - 1][y - 1];
					if (tokens1.get(x).getClass() == tokens2.get(y).getClass()) {
						table[x][y] = new Cell(upleft.value + 1, true, x, y,
								upleft);
					} else {
						table[x][y] = (left.value >= up.value) ? new Cell(
								left.value, false, x, y, left) : new Cell(
								up.value, false, x, y, up);
					}
				}
			}

			Cell cell = table[tokens1.size() - 1][tokens2.size() - 1];
			while (true) {
				if (null != cell.base) {
					Cell previous = cell.base;
					if (previous.x == cell.x || previous.y == cell.y) {
						return DiffType.TYPE3;
					}
					cell = previous;
				} else {
					break;
				}
			}
		}

		{
			if (tokens1.get(0).value == tokens2.get(0).value) {
				table[0][0] = new Cell(1, true, 0, 0, null);
			} else {
				table[0][0] = new Cell(0, false, 0, 0, null);
			}
			for (int x = 1; x < tokens1.size(); x++) {
				if (tokens1.get(x).value == tokens2.get(0).value) {
					table[x][0] = new Cell(1, true, x, 0, null);
				} else {
					table[x][0] = new Cell(table[x - 1][0].value, false, x, 0,
							table[x - 1][0]);
				}
			}
			for (int y = 1; y < tokens2.size(); y++) {
				if (tokens1.get(0).value == tokens2.get(y).value) {
					table[0][y] = new Cell(1, true, 0, y, null);
				} else {
					table[0][y] = new Cell(table[0][y - 1].value, false, 0, y,
							table[0][y - 1]);
				}
			}
			for (int x = 1; x < tokens1.size(); x++) {
				for (int y = 1; y < tokens2.size(); y++) {
					final Cell left = table[x - 1][y];
					final Cell up = table[x][y - 1];
					final Cell upleft = table[x - 1][y - 1];
					if (tokens1.get(x).value == tokens2.get(y).value) {
						table[x][y] = new Cell(upleft.value + 1, true, x, y,
								upleft);
					} else {
						table[x][y] = (left.value >= up.value) ? new Cell(
								left.value, false, x, y, left) : new Cell(
								up.value, false, x, y, up);
					}
				}
			}

			Cell cell = table[tokens1.size() - 1][tokens2.size() - 1];
			while (true) {
				if (null != cell.base) {
					Cell previous = cell.base;
					if (previous.x == cell.x || previous.y == cell.y) {
						return DiffType.TYPE2;
					}
					cell = previous;
				} else {
					break;
				}
			}
		}

		return DiffType.TYPE1;
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
