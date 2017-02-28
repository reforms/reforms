package com.reforms.orm.reflex;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class Bobj {

    private final boolean flag;

    private final Boolean flagBox;

    private final byte bytic;

    private final Byte byticBox;

    private final short shortic;

    private final Short shorticBox;

    private final char symbol;

    private final Character symbolBox;

    private final int intic;

    private final Integer inticBox;

    private final float floatic;

    private final Float floaticBox;

    private final double doublic;

    private final Double doublicBox;

    private final long longic;

    private final Long longicBox;

    private final String stringic;

    private final BigDecimal decimalic;

    private final Date datic;

    private final Time timic;

    private final Timestamp timestampic;

    private final BobjType enumic;

    private final BobjIn bobjnnic;

    public Bobj(boolean flag, Boolean flagBox, byte bytic, Byte byticBox, short shortic, Short shorticBox, char symbol, Character symbolBox, int intic,
            Integer inticBox, float floatic, Float floaticBox, double doublic, Double doublicBox, long longic, Long longicBox, String stringic,
            BigDecimal decimalic, Date datic, Time timic, Timestamp timestampic, BobjType enumic, BobjIn bobjnnic) {
        super();
        this.flag = flag;
        this.flagBox = flagBox;
        this.bytic = bytic;
        this.byticBox = byticBox;
        this.shortic = shortic;
        this.shorticBox = shorticBox;
        this.symbol = symbol;
        this.symbolBox = symbolBox;
        this.intic = intic;
        this.inticBox = inticBox;
        this.floatic = floatic;
        this.floaticBox = floaticBox;
        this.doublic = doublic;
        this.doublicBox = doublicBox;
        this.longic = longic;
        this.longicBox = longicBox;
        this.stringic = stringic;
        this.decimalic = decimalic;
        this.datic = datic;
        this.timic = timic;
        this.timestampic = timestampic;
        this.enumic = enumic;
        this.bobjnnic = bobjnnic;
    }

    public boolean isFlag() {
        return flag;
    }

    public Boolean getFlagBox() {
        return flagBox;
    }

    public byte getBytic() {
        return bytic;
    }

    public Byte getByticBox() {
        return byticBox;
    }

    public short getShortic() {
        return shortic;
    }

    public Short getShorticBox() {
        return shorticBox;
    }

    public char getSymbol() {
        return symbol;
    }

    public Character getSymbolBox() {
        return symbolBox;
    }

    public int getIntic() {
        return intic;
    }

    public Integer getInticBox() {
        return inticBox;
    }

    public float getFloatic() {
        return floatic;
    }

    public Float getFloaticBox() {
        return floaticBox;
    }

    public double getDoublic() {
        return doublic;
    }

    public Double getDoublicBox() {
        return doublicBox;
    }

    public long getLongic() {
        return longic;
    }

    public Long getLongicBox() {
        return longicBox;
    }

    public String getStringic() {
        return stringic;
    }

    public BigDecimal getDecimalic() {
        return decimalic;
    }

    public Date getDatic() {
        return datic;
    }

    public Time getTimic() {
        return timic;
    }

    public Timestamp getTimestampic() {
        return timestampic;
    }

    public BobjType getEnumic() {
        return enumic;
    }

    public BobjIn getBobjnnic() {
        return bobjnnic;
    }
}
