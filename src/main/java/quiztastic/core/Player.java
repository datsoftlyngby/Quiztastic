package quiztastic.core;

public class Player {
    private final String userName;
    private final int points;

    public Player(int id, String userName, int points) {
        this.userName = userName;
        this.points = points;
    }



    public String getUserName() {
        return userName;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return userName;
    }
}
