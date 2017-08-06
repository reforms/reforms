package com.reforms.cf.struct;

/**
 * The Signature Attribute
 *
 * @author evgenie
 */
public class AttrSignatureStruct extends AttrBaseStruct {

    private int signatureIndex; // u2

    public AttrSignatureStruct() {
        super(Attributes.SIGNATURE_ATTR);
    }

    public int getSignatureIndex() {
        return signatureIndex;
    }

    public void setSignatureIndex(int signatureIndex) {
        this.signatureIndex = signatureIndex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" signatureIndex=").append(signatureIndex);
        return builder.toString();
    }

}
