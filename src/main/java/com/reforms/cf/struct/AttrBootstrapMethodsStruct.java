package com.reforms.cf.struct;

import java.util.Arrays;

/**
 * The BootstrapMethods Attribute
 *
 * @author evgenie
 */
public class AttrBootstrapMethodsStruct extends AttrBaseStruct {

    private int numBootstrapMethods; // u2
    private BootstrapMethodStruct[] bootstrapMethods;

    public AttrBootstrapMethodsStruct() {
        super(Attributes.BOOTSTRAP_METHODS_ATTR);
    }

    public int getNumBootstrapMethods() {
        return numBootstrapMethods;
    }

    public void setNumBootstrapMethods(int numBootstrapMethods) {
        this.numBootstrapMethods = numBootstrapMethods;
    }

    public BootstrapMethodStruct[] getBootstrapMethods() {
        return bootstrapMethods;
    }

    public void setBootstrapMethods(BootstrapMethodStruct[] bootstrapMethods) {
        this.bootstrapMethods = bootstrapMethods;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" numBootstrapMethods=")
                .append(numBootstrapMethods).append(", bootstrapMethods=").append(Arrays.toString(bootstrapMethods));
        return builder.toString();
    }

}
