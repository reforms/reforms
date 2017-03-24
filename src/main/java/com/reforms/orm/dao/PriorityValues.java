package com.reforms.orm.dao;

/**
 * В зависимости от типа данных, подставляется или первый или второй фильтр с данными
 * @author evgenie
 */
public class PriorityValues implements IPriorityValues {

    private int basePriorType;

    private int baseIndexCount;

    private int anyIndexCount;

    private IValues baseValues;

    private IValues anyValues;

    public PriorityValues(int basePriorType, IValues baseValues, IValues anyValues) {
        this.basePriorType = basePriorType;
        this.baseValues = baseValues;
        this.anyValues = anyValues;
    }

    @Override
    public Object getPriorityValue(int priority, String key) {
        return get(key, priority == basePriorType ? baseValues : anyValues);
    }

    @Override
    public Object getPriorityValue(int priority, int key) {
        IValues currentValues;
        int newKey;
        if (priority == basePriorType) {
            newKey = key - anyIndexCount;
            baseIndexCount++;
            currentValues = baseValues;
        } else {
            newKey = key - baseIndexCount;
            anyIndexCount++;
            currentValues = anyValues;
        }
        return get(newKey, currentValues);
    }

    @Override
    public int getParamNameType(int priority) {
        return priority == basePriorType ? baseValues.getParamNameType() : anyValues.getParamNameType();
    }

    private Object get(String key, IValues currentValues) {
        return currentValues.get(key);
    }

    private Object get(int key, IValues currentValues) {
        return currentValues.get(key);
    }
}