package com.reforms.cf.struct;

/**
 * The SourceDebugExtension Attribute
 *
 * @author evgenie
 */
public class AttrSourceDebugExtensionStruct extends AttrBaseStruct {

    private byte[] data;

    public AttrSourceDebugExtensionStruct() {
        super(Attributes.SOURCE_DEBUG_EXTENSION_ATTR);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attribute: name=").append(getName()).append(" data_length=")
                .append(data == null ? 0 : data.length);
        return builder.toString();
    }

}
