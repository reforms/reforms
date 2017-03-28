package com.reforms.orm.dao.filter.column;

/**
 *
 * @author evgenie
 */
public enum FilterState {
    /** участвует и в выборке и в sql-выражении */
    FS_ACCEPT(0),
    /** не участвует в выборке, но есть в sql-выражении */
    FS_NOT_ACCEPT(1),
    /** не участвует ни в выборке, ни в sql-выражении */
    FS_REMOVE(2);

    private final int prior;

    private FilterState(int prior) {
        this.prior = prior;
    }

    public int getPrior() {
        return prior;
    }
}
