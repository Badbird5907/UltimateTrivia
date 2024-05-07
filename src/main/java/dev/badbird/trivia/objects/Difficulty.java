package dev.badbird.trivia.objects;

public enum Difficulty {
    EASY(0,"Easy", 3, 3, 5),
    MEDIUM(1, "Medium", 2, 2, 3),
    HARD(2,"Hard", 1, 1, 5);

    private final int index;
    private final String display;
    private final int maxIncorrect;
    private final int points;
    private final int neededToUpgrade;

    Difficulty(int index, String display, int maxIncorrect, int points, int neededToUpgrade) {
        this.index = index;
        this.display = display;
        this.maxIncorrect = maxIncorrect;
        this.points = points;
        this.neededToUpgrade = neededToUpgrade;
    }

    public static Difficulty getNext(Difficulty in) {
        switch (in) {
            case EASY -> {
                return MEDIUM;
            }
            case MEDIUM -> {
                return HARD;
            }
            default -> {
                return null;
            }
        }
    }

    public static Difficulty getPrev(Difficulty in, Difficulty min) {
        int minI = min.getIndex();
        if (in == EASY) return EASY;
        int next = Math.max(in.getIndex() - 1, minI);
        return getIndex(next);
    }

    public static Difficulty getIndex(int i) {
        switch (i) {
            case 0 -> {
                return EASY;
            }
            case 1 -> {
                return MEDIUM;
            }
            case 2 -> {
                return HARD;
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public int getPoints() {
        return points;
    }

    public String getDisplay() {
        return display;
    }

    public int getMaxIncorrect() {
        return maxIncorrect;
    }

    public int getNeededToUpgrade() {
        return neededToUpgrade;
    }
}
