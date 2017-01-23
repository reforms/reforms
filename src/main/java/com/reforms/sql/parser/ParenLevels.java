package com.reforms.sql.parser;

import java.util.ArrayDeque;

class ParenLevels extends ArrayDeque<ParenLevel> {

    private int depth;

    void incDepth() {
        depth++;
    }

    void decDepth() {
        depth--;
    }

    void changeDepth(int value) {
        depth += value;
    }

    int getDepth() {
        return depth;
    }
}
