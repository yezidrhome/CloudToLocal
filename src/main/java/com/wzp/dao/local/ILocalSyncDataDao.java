package com.wzp.dao.local;

import java.util.List;
import java.util.Map;

public interface ILocalSyncDataDao {

    int save(Map para);

    int batchSave(Map para);

    List<Map> queryAllSyncTableConfig(Map param);

}
