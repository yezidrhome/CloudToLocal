package com.wzp.dao.iot;

import java.util.List;
import java.util.Map;

public interface IIotSyncDataDao {

    List<String> getTableColumn(Map para);

    List<String> getAllTableName(Map para);

    int getDataCount(Map para);

    List<Map> getDataByLimit(Map para);

}
