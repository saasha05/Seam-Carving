package seamcarving;

import java.util.Objects;

public final class Pixel {
    private int col;
    private int row;

    public Pixel(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getX() {
        return row;
    }

    public int getY() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true; }
        if (o == null || getClass() != o.getClass()) {return false; }
        Pixel pixel = (Pixel) o;
        return col == pixel.col &&
                row == pixel.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return "Pixel{" +
                ", row=" + row +
                "col=" + col +
                '}';
    }
}