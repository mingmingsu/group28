package cs361.battleships.models;

public class Minesweeper extends Ship {
    public Minesweeper() {
        super();
        this.size = 2;
        this.kind = "MINESWEEPER";
    }

    @Override
	public void place(char col, int row, boolean isVertical) {
		for (int i=0; i<size; i++) {
			if (isVertical) {
                if (size - i == 2) {
                    Square s = new CaptainQuarter(row+i, col);
                    s.setMaxHits(1);
                    occupiedSquares.add(s);
                } else {
                    occupiedSquares.add(new Square(row+i, col));
                }
			} else {
                if (size - i == 2) {
                    Square s = new CaptainQuarter(row,(char)(col+i));
                    s.setMaxHits(1);
                    occupiedSquares.add(s);
                } else {
                    occupiedSquares.add(new Square(row, (char) (col + i)));
                }
			}
		}
	}
}
