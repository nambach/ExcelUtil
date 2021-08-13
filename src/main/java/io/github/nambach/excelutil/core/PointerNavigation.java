package io.github.nambach.excelutil.core;

import org.apache.poi.ss.util.CellAddress;

class PointerNavigation {
    private final Pointer pointer = new Pointer();
    private final Pointer pivot = new Pointer();

    public CellAddress getCellAddress() {
        return new CellAddress(pointer.getRow(), pointer.getCol());
    }

    public int getRow() {
        return pointer.getRow();
    }

    public int getCol() {
        return pointer.getCol();
    }

    public void goToCell(String address) {
        pointer.update(address);
        pivot.sync(pointer);
    }

    public void goToCell(int row, int col) {
        pointer.update(row, col);
        pivot.sync(pointer);
    }

    public void next() {
        pointer.jumpRight(pivot);
        pivot.sync(pointer);
    }

    public void next(int steps) {
        if (steps > 0) {
            next();
            pointer.moveRight(steps - 1);
            pivot.sync(pointer);
        }
    }

    public void down() {
        pointer.jumpDown(pivot);
        pivot.sync(pointer);
    }

    public void down(int steps) {
        if (steps > 0) {
            down();
            pointer.moveDown(steps - 1);
            pivot.sync(pointer);
        }
    }

    public void enter() {
//        pointer.update(getNextRowIndex(), 0);
        pointer.enter();
        pivot.sync(pointer);
    }

    public void enter(int steps) {
        if (steps > 0) {
            enter();
            pointer.moveDown(steps - 1);
            pivot.sync(pointer);
        }
    }

    public void update(int row, int col) {
        pointer.update(row, col);
        pivot.sync(pointer);
    }

    public void updatePivot(int row, int col) {
        pivot.update(row, col);
    }

    public void updatePivotRight(int steps) {
        pivot.moveRight(steps);
    }

    public void updatePivotDown(int steps) {
        pivot.moveDown(steps);
    }

    public void sync(PointerNavigation other) {
        this.pointer.sync(other.pointer);
        this.pivot.sync(other.pivot);
    }
}
