package com.reforms.cf.descriptor;

import com.reforms.cf.signature.Signature;

/**
 * The BaseType: one of B C D F I J S Z
 * @author evgenie
 *
 * Делаем допущение, что базовый дескриптор можно рассматривать как и сигнатуру.
 */
public class BaseType extends FieldDescriptor implements Signature {

    public BaseType(char term) {
        super(term, Descriptors.getTermName(term));
    }

    @Override
    public String getDescriptor() {
        return "" + getTerm();
    }

    @Override
    public String getSignature() {
        return "" + getTerm();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseType '").append(getTerm()).append("' is ").append(getValue());
        return builder.toString();
    }

}
