package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The TypePath struct
 *
 * @author evgenie
 */
public class TypePathStruct {
    private int pathLength; // u1
    private PathStruct[] pathes;

    public int getPathLength() {
        return pathLength;
    }

    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }

    public PathStruct[] getPathes() {
        return pathes;
    }

    public void setPathes(PathStruct[] pathes) {
        this.pathes = pathes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TypePath pathLength=").append(pathLength).append(", path=").append(Arrays.toString(pathes));
        return builder.toString();
    }

}
