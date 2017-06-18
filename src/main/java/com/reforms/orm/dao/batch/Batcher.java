package com.reforms.orm.dao.batch;

import static com.reforms.orm.OrmConfigurator.getInstance;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.reforms.orm.dao.IPriorityValues;
import com.reforms.orm.dao.filter.IPsValuesSetter;
import com.reforms.orm.dao.filter.PsValuesSetter;
import com.reforms.orm.dao.filter.param.ParamSetterFactory;

/**
 * Запоминает хронологию действий, связанных с установкой значений в PS с целью их дальнейшего воспроихведения
 * @author evgenie
 */
public class Batcher implements IBatcher, IPriorityValues, IPsValuesSetter {

    private final IPriorityValues origPriorityValues;

    private final IPsValuesSetter origPsValuesSetter;

    private IGetAction getValueAction = null;

    private final List<AddValueRepeatAction> repeatActions = new ArrayList<>();

    public Batcher(IPriorityValues origPriorityValues, IPsValuesSetter origPsValuesSetter) {
        this.origPriorityValues = origPriorityValues;
        this.origPsValuesSetter = origPsValuesSetter;
    }

    @Override
    public int addFilterValue(String filterPrefix, Object filterValue) {
        if (getValueAction == null) {
            throw new IllegalStateException("Нарушен хронологический порядок. Воспроизведение не возможно");
        }
        AddValueRepeatAction action = new AddValueRepeatAction(filterPrefix, getValueAction);
        repeatActions.add(action);
        getValueAction = null;
        return origPsValuesSetter.addFilterValue(filterPrefix, filterValue);
    }

    @Override
    public int setParamsTo(PreparedStatement ps) throws SQLException {
        // Воспроизведение не требуется
        return origPsValuesSetter.setParamsTo(ps);
    }

    @Override
    public Object getPriorityValue(int priority, String key) {
        getValueAction = new GetPriorityValueM1Action(priority, key);
        return origPriorityValues.getPriorityValue(priority, key);
    }

    @Override
    public Object getPriorityValue(int priority, int key) {
        getValueAction = new GetPriorityValueM2Action(priority, key);
        return origPriorityValues.getPriorityValue(priority, key);
    }

    @Override
    public int getParamNameType(int priority) {
        // Воспроизведение не требуется
        return origPriorityValues.getParamNameType(priority);
    }

    @Override
    public void add(IPriorityValues values, PreparedStatement ps) throws SQLException {
        ParamSetterFactory paramSetterFactory = getInstance(ParamSetterFactory.class);
        PsValuesSetter fpss = new PsValuesSetter(paramSetterFactory);
        for (AddValueRepeatAction action : repeatActions) {
            action.addValue(values, fpss);
        }
        fpss.setParamsTo(ps);
        ps.addBatch();
    }
}
