package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The BootstrapMethod struct
 *
 * @author evgenie
 */
public class BootstrapMethodStruct {
    private int bootstrapMethodRef; // u2
    private int numBootstrapArguments; // u2
    private int[] bootstrapArguments; // u2Array

    public int getBootstrapMethodRef() {
        return bootstrapMethodRef;
    }

    public void setBootstrapMethodRef(int bootstrapMethodRef) {
        this.bootstrapMethodRef = bootstrapMethodRef;
    }

    public int getNumBootstrapArguments() {
        return numBootstrapArguments;
    }

    public void setNumBootstrapArguments(int numBootstrapArguments) {
        this.numBootstrapArguments = numBootstrapArguments;
    }

    public int[] getBootstrapArguments() {
        return bootstrapArguments;
    }

    public void setBootstrapArguments(int[] bootstrapArguments) {
        this.bootstrapArguments = bootstrapArguments;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BootstrapMethodStruct [bootstrapMethodRef=").append(bootstrapMethodRef)
                .append(", numBootstrapArguments=").append(numBootstrapArguments).append(", bootstrapArguments=")
                .append(Arrays.toString(bootstrapArguments)).append("]");
        return builder.toString();
    }

}
