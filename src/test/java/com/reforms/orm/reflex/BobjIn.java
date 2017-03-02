package com.reforms.orm.reflex;

public class BobjIn {

    private final Integer inticBox;

    private final String stringic;

    public BobjIn(Integer inticBox, String stringic) {
        this.inticBox = inticBox;
        this.stringic = stringic;
    }

    public Integer getInticBox() {
        return inticBox;
    }

    public String getStringic() {
        return stringic;
    }

    @Override
    public String toString() {
        return "[inticBox=" + inticBox + ", stringic=" + stringic + "]";
    }
}
