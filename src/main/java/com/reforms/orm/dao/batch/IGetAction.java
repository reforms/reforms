package com.reforms.orm.dao.batch;

import com.reforms.orm.dao.IPriorityValues;

/**
 * Контракт на воспроизведение произвольного действия
 * @author evgenie
 */
interface IGetAction {

    Object getPriorityValue(IPriorityValues values);
}
