package com.reforms.orm.scenario;

import static com.reforms.orm.scenario.ScriptIterator.getResourceIterator;

import org.junit.After;
import org.junit.Before;

import com.reforms.orm.DataDbTest;

/**
 * Тестируем выборку для объектов по сценарию
 * @author evgenie
 */
public class TestScenarioDao extends DataDbTest {

    private String scenarioName;

    public TestScenarioDao(String scenarioName, String bdName) {
        super(bdName);
        this.scenarioName = scenarioName;
    }

    @Before
    public void beforeTest() throws Exception {
        ScriptIterator si = getResourceIterator(scenarioName, getClass());
        while (si.hasNext()) {
            String statement = si.next();
            invokeStatement(statement);
        }
    }

    @After
    public void afterTest() {
        close();
    }
}