package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class Square {

	@JsonProperty private int row;
	@JsonProperty private char column;
	@JsonProperty private boolean hit = false;

	@JsonProperty protected int timesHit = 0;
	@JsonProperty protected String type = "N";
	@JsonProperty protected int maxHits = 1;

	public Square() {
	}

	public Square(int row, char column) {
		this.row = row;
		this.column = column;
	}

	public char getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}


	@Override
	public boolean equals(Object other) {
		if (other instanceof Square) {
			return ((Square) other).row == this.row && ((Square) other).column == this.column;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * row + column;
	}

	@JsonIgnore
	public boolean isOutOfBounds() {
		return row > 10 || row < 1 || column > 'J' || column < 'A';
	}

	@JsonIgnore
    public boolean isQuarter() {
        return type.equals("CQ");
    }

	public boolean isHit() {
		return hit;
	}

	public void hit() {
        timesHit ++;
        if (timesHit == maxHits) {
            hit = true;
        }
	}

    public boolean canMove(int dir) {
        int oldRow = row;
        char oldCol = column;

        move(dir);
        boolean ret = !isOutOfBounds();

        row = oldRow;
        column = oldCol;

        return ret;
    }

    public void move(int dir) {
        switch (dir) {
        case 1:
            row -= 1;
            break;
        case 2:
            row += 1;
            break;
        case 3:
            column -= 1;
            break;
        case 4:
            column += 1;
            break;
        }
    }

    /* 
     * change the max hits level
     * @param max hits count
     */
    public void setMaxHits(int max) {
        maxHits = max;
    }

	@Override
	public String toString() {
		return "(" + row + ", " + column + ')';
	}
}
