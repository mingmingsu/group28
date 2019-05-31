package cs361.battleships.models;

public class CaptainQuarter extends Square {
    public CaptainQuarter(int row, char col) {
        super(row, col);
        this.timesHit = 0;
        this.maxHits = 2;
        this.type = "CQ";
    }
}
