package cs361.battleships.models;

public class Submarine extends Ship {
    public Submarine() {
        this.size = 5;
        this.kind = "SUBMARINE";
    }

    @Override
    public void place(char col, int row, boolean isVertical) {
		for (int i=0; i<size-1; i++) {
			if (isVertical) {
                if (size - i == 2) {
                    Square s = new CaptainQuarter(row+i, col);
                    occupiedSquares.add(s);
                } else {
                    occupiedSquares.add(new Square(row+i, col));
                }
			} else {
                if (size - i == 2) {
                    Square s = new CaptainQuarter(row,(char)(col+i));
                    occupiedSquares.add(s);
                } else {
                    occupiedSquares.add(new Square(row, (char) (col + i)));
                }
			}
		}

        if (isVertical) {
            occupiedSquares.add(new Square(row+2, (char) (col + 1)));
        } else {
            occupiedSquares.add(new Square(row-1, (char) (col + 2)));
        }
	}
}
