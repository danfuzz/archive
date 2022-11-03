import java.io.PrintStream;

public class Sudoku {
	private int[] possibilities;
	int total;

	public Sudoku() {
		possibilities = new int[81];
		total = 9 * 9 * 9;

		for (int i = 0; i < 81; i++) {
			possibilities[i] = 0x3fe;
		}
	}

	public void print(PrintStream out) {
		int[] widths = new int[9];
		for (int x = 0; x < 9; x++) {
			int w = popCount(getPossibilities(x, 0));
			for (int y = 1; y < 9; y++) {
				int w0 = popCount(getPossibilities(x, y));
				if (w0 > w) {
					w = w0;
				}
			}
			widths[x] = w;
		}

		out.println("========================================");
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				int p = getPossibilities(x, y);
				int spc = widths[x] - popCount(p) + 1;
				printPossibilities(p, out);
				for (int i = 0; i < spc; i++) {
					out.print(' ');
				}
			}
			out.println();
		}
	}

	static private void printPossibilities(int bits, PrintStream out) {
		out.print(bitString(bits));
	}
	
	static private String bitString(int bits) {
		StringBuffer sb = new StringBuffer(9);
		boolean any = false;
		for (int i = 1; i <= 9; i++) {
			if ((bits & (1 << i)) != 0) {
				sb.append((char) ('0' + i));
				any = true;
			}
		}

		if (! any) {
			return "x";
		}

		return sb.toString();
	}

	public void put(int x, int y, int value) {
		int bit = 1 << value;

		if (setPossibilities(x, y, bit)) {
			cascade(x, y);
		}
	}

	public int guessAndRecurse(int level) {
		level++;

		Sudoku s = new Sudoku();
		int maxLevel = level;

		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				int p = getPossibilities(x, y);
				int pop = popCount(p);
				if (p == 1) {
					continue;
				}
				for (int i = 1; i <= 9; i++) {
					int bit = (1 << i);
					if ((p & bit) == 0) {
						continue;
					}

					s.copyFrom(this);
					try {
						s.removePossibilities(x, y, bit);
						s.reduce(false);
					}
					catch (RuntimeException ex) {
						// failed quick
						continue;
					}

					if (s.isSolved()) {
						// succeeded quick
						copyFrom(s);
						return maxLevel;
					}

					int l0 = s.guessAndRecurse(level);
					if (l0 > maxLevel) {
						maxLevel = l0;
					}

					copyFrom(s);
					return maxLevel;
				}
			}
		}

		return maxLevel;
	}


	public void reduce(boolean verbose) {
		for (;;) {
			boolean any = false;
			while (findLoners()) {
				if (verbose) System.out.println("FOUND LONERS");
				any = true;
			}
			while (findPairs()) {
				if (verbose) System.out.println("FOUND PAIRS");
				any = true;
			}
			while (findEnclaves()) {
				if (verbose) System.out.println("FOUND ENCLAVES");
				any = true;
			}
			if (! any) {
				break;
			}
		}
	}

	public boolean findLoners() {
		boolean any = false;

		for (int angle = 0; angle < 3; angle++) {
			for (int group = 0; group < 9; group++) {
				int usedBits = 0;
				for (int loc = 0; loc < 9; loc++) {
					int bits = groupGet(angle, group, loc);
					int bits0 = bits;

					if (popCountIsOne(bits)) {
						usedBits |= bits;
						continue;
					}

					bits &= ~usedBits;
					usedBits |= bits0;

					if (bits == 0) {
						continue;
					}

					for (int l0 = loc + 1; l0 < 9; l0++) {
						bits &= ~groupGet(angle, group, l0);
					}

					if ((bits != 0) && groupSet(angle, group, loc, bits)) {
						any = true;
					}
				}
			}
		}

		return any;
	}

	public boolean findPairs() {
		boolean any = false;

		for (int angle = 0; angle < 3; angle++) {
			for (int group = 0; group < 9; group++) {
				for (int loc = 0; loc < 8; loc++) {
					int bits = groupGet(angle, group, loc);
					boolean found = false;
					if (! popCountIsTwo(bits)) {
						continue;
					}

					for (int l0 = loc + 1; l0 < 9; l0++) {
						if (groupGet(angle, group, l0) == bits) {
							found = true;
							break;
						}
					}

					if (! found) {
						continue;
					}

					for (int l0 = 0; l0 < 9; l0++) {
						int b = groupGet(angle, group, l0);
						if (((b & bits) != b) &&
							groupSet(angle, group, l0, b & ~bits))
						{
							any = true;
						}
					}
				}
			}
		}

		return any;
	}

	public boolean findEnclaves() {
		boolean any = false;

		for (int angle = 0; angle < 2; angle++) {
			for (int group = 0; group < 9; group++) {
				valueLoop:
				for (int value = 1; value <= 9; value++) {
					int bit = 1 << value;
					int areas = 0;
					int foundLoc = 0;
					for (int loc = 0; loc < 9; loc++) {
						int b = groupGet(angle, group, loc);
						if (b == bit) {
							continue valueLoop;
						}
						if ((b & bit) != 0) {
							areas |= 1 << (loc / 3);
							foundLoc = loc;
						}
					}
					if (! popCountIsOne(areas)) {
						continue;
					}

					int xy = groupToXY(angle, group, foundLoc);
					int x = xy & 0xff;
					int y = xy >> 8;
					x -= (x % 3);
					y -= (y % 3);
					for (int x0 = 0; x0 < 3; x0++) {
						for (int y0 = 0; y0 < 3; y0++) {
							int x1 = x + x0;
							int y1 = y + y0;
							if (groupContains(angle, group, x1, y1)) {
								continue;
							}
							if (removePossibilities(x1, y1, bit)) {
								cascade(x1, y1);
								any = true;
							}
						}
					}
				}
			}
		}

		// for angle 2 (boxes)
		for (int group = 0; group < 9; group++) {
			valueLoop:
			for (int value = 1; value <= 9; value++) {
				int bit = 1 << value;
				int areasx = 0;
				int areasy = 0;
				int foundLoc = 0;
				for (int loc = 0; loc < 9; loc++) {
					int b = groupGet(2, group, loc);
					if (b == bit) {
						continue valueLoop;
					}
					if ((b & bit) != 0) {
						areasx |= 1 << (loc / 3);
						areasy |= 1 << (loc % 3);
						foundLoc = loc;
					}
				}
				if (popCountIsOne(areasx)) {
					int y = groupToXY(2, group, foundLoc) >> 8;
					for (int x = 0; x < 9; x++) {
						if (groupContains(2, group, x, y)) {
							continue;
						}
						if (removePossibilities(x, y, bit)) {
							cascade(x, y);
							any = true;
						}
					}
				}
				else if (popCountIsOne(areasy)) {
					int x = groupToXY(2, group, foundLoc) & 0xff;
					for (int y = 0; y < 9; y++) {
						if (groupContains(2, group, x, y)) {
							continue;
						}
						if (removePossibilities(x, y, bit)) {
							cascade(x, y);
							any = true;
						}
					}
				}
			}
		}

		return any;
	}

	private void cascade(int x, int y) {
		int bit = getPossibilities(x, y);
		if (! popCountIsOne(bit)) {
			return;
		}

		for (int x0 = 0; x0 < 9; x0++) {
			if (x0 == x) {
				continue;
			}
			if (removePossibilities(x0, y, bit)) {
				cascade(x0, y);
			}
		}

		for (int y0 = 0; y0 < 9; y0++) {
			if (y0 == y) {
				continue;
			}
			if (removePossibilities(x, y0, bit)) {
				cascade(x, y0);
			}
		}

		int xoff = (x % 3);
		int yoff = (y % 3);
		x -= xoff;
		y -= yoff;

		for (int x0 = 0; x0 < 3; x0++) {
			for (int y0 = 0; y0 < 3; y0++) {
				if ((x0 == xoff) && (y0 == yoff)) {
					continue;
				}
				if (removePossibilities(x + x0, y + y0, bit)) {
					cascade(x + x0, y + y0);
				}
			}
		}
	}

	public void copyFrom(Sudoku other) {
		System.arraycopy(other.possibilities, 0, possibilities, 0, 81);
		this.total = other.total;
	}

	public boolean isSolved() {
		return (total == 81);
	}

	private int getPossibilities(int x, int y) {
		return possibilities[y * 9 + x];
	}

	private boolean setPossibilities(int x, int y, int poss) {
		int idx = (y * 9) + x;
		int p = possibilities[idx];
		if (p == poss) {
			return false;
		}
		if (poss == 0) {
			throw new RuntimeException("attempt to set to 0 at (" + x + ", " +
									   y + ")");
		}
		total = total - popCount(p) + popCount(poss);
		possibilities[idx] = poss;
		return true;
	}

	private boolean removePossibilities(int x, int y, int poss) {
		int p = getPossibilities(x, y);
		return setPossibilities(x, y, p & ~poss);
	}

	private int groupGet(int angle, int group, int loc) {
		int xy = groupToXY(angle, group, loc);
		int x = xy & 0xff;
		int y = xy >> 8;
		return getPossibilities(x, y);
	}

	private boolean groupSet(int angle, int group, int loc, int value) {
		int xy = groupToXY(angle, group, loc);
		int x = xy & 0xff;
		int y = xy >> 8;

		if (! setPossibilities(x, y, value)) {
			return false;
		}

		cascade(x, y);
		return true;
	}

	static private boolean groupContains(int angle, int group, int x, int y) {
		// xxx really cheesy implementation
		int xy = x | (y << 8);
		for (int loc = 0; loc < 9; loc++) {
			int xy0 = groupToXY(angle, group, loc);
			if (xy == xy0) {
				return true;
			}
		}
		return false;
	}

	static private int groupToXY(int angle, int group, int loc) {
		switch (angle) {
			case 0: {
				// columns
				return group | (loc << 8);
			}
			case 1: {
				// rows
				return loc | (group << 8);
			}
			default: {
				// squares
				int x = group % 3;
				int y = group / 3;
				int xoff = loc % 3;
				int yoff = loc / 3;
				return (x * 3 + xoff) | ((y * 3 + yoff) << 8);
			}
		}
	}

	static private boolean popCountIsOne(int value) {
		switch (value) {
			case 0x1: case 0x2: case 0x4: case 0x8:
			case 0x10: case 0x20: case 0x40: case 0x80:
			case 0x100: case 0x200: {
				return true;
			}
		}
		return false;
	}

	static private boolean popCountIsTwo(int value) {
		return popCount(value) == 2;
	}

	static private int popCount(int value) {
		int count = 0;
		while (value != 0) {
			if ((value & 1) != 0) {
				count++;
			}
			value >>>= 1;
		}
		return count;
	}
}
