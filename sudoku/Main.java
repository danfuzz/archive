public class Main {
	private final static boolean DEBUG = false;

	static public void main(String[] args) 
		throws Exception
	{
		Sudoku s = new Sudoku();

		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				char c = readValidChar();
				if ((c >= '1') && (c <= '9')) {
					s.put(x, y, c - '0');
				}
			}
		}

		if (DEBUG) {
			s.print(System.out);
		}

		s.reduce(true);

		if (DEBUG) {
			s.print(System.out);
		}

		if (s.isSolved()) {
			System.out.println("YAY!");
		}
		else {
			System.out.println("WAH!");
			int level = s.guessAndRecurse(0);
			System.out.println("RECURSION LEVEL " + level);
		}

		s.print(System.out);
	}

	static private char readValidChar() 
		throws Exception
	{
		for (;;) {
			int c = System.in.read();
			if (c == -1) {
				throw new IllegalArgumentException("truncated input");
			}
			if (((c >= '1') && (c <= '9')) || (c == 'x')) {
				return (char) c;
			}
		}
	}
}
