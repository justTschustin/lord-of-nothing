package io.github.lord_of_nothing.grid;

public class Tile {
    public static final int BASE_TILE_SIZE = 32;

    private final int x;
    private final int y;
    private boolean clicked;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.clicked = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
