package io.github.nambach.excelutil.core;

interface Navigation<T extends Navigation<T>> {

    T goToCell(String address);

    T goToCell(int row, int col);

    T next();

    T next(int steps);

    T down();

    T down(int steps);

    T enter();

    T enter(int steps);
}
