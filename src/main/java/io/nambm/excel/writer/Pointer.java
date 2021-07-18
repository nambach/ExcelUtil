package io.nambm.excel.writer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.poi.ss.util.CellAddress;

@Setter(AccessLevel.NONE)
@Getter
class Pointer {
    private int row;
    private int col;

    Pointer() {
    }

    public void sync(Pointer p) {
        this.row = p.row;
        this.col = p.col;
    }

    public void reset() {
        this.row = 0;
        this.col = 0;
    }

    @SneakyThrows
    public void update(String address) {
        CellAddress a = new CellAddress(address);
        update(a.getRow(), a.getColumn());
    }

    @SneakyThrows
    public void update(int row, int col) {
        if (row < 0 || col < 0) {
            throw new Exception("Coordinate is negative. ");
        }
        this.row = row;
        this.col = col;
    }

    public void moveRight() {
        this.col++;
    }

    public void moveRight(int steps) {
        this.col += steps;
    }

    public void jumpRight(Pointer other) {
        int colGap = other.col - this.col;
        if (colGap < 0) {
            colGap = 0;
        }
        this.col += 1 + colGap;
    }

    public void moveDown() {
        this.row++;
    }

    public void moveDown(int steps) {
        this.row += steps;
    }

    public void jumpDown(Pointer other) {
        int rowGap = other.row - this.row;
        if (rowGap < 0) {
            rowGap = 0;
        }
        this.row += 1 + rowGap;
    }

    public void enter() {
        this.row++;
        this.col = 0;
    }

    public void enter(Pointer other) {
        jumpDown(other);
        this.col = 0;
    }

    public boolean same(Pointer other) {
        return this.row == other.row && this.col == other.col;
    }
}
