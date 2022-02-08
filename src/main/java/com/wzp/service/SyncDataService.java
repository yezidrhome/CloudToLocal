package com.wzp.service;

import com.wzp.dao.iot.IIotSyncDataDao;
import com.wzp.dao.local.ILocalSyncDataDao;
import com.wzp.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("syncDataService")
public class SyncDataService {

    private Logger logger = LoggerFactory.getLogger(SyncDataService.class);

    @Autowired
    private ILocalSyncDataDao iLocalSyncDataDao;
    @Autowired
    private IIotSyncDataDao iIotSyncDataDao;

    private Map<String, String> ignoreTable = new HashMap<>();

    private final String tableSchema = "wzp";

    //本地化部署需要忽略的表
    private void InitIgnoreTable(){
        ignoreTable.put("sys_log", "日志表");
    }

    public void getAllTable() throws Exception {
        InitIgnoreTable();
        Map para = new HashMap();
        para.put("table_schema", tableSchema);
        List<String> uptableNames = iIotSyncDataDao.getAllTableName(para);
        List<String> tableNames = uptableNames.stream().map(String::toLowerCase).collect(Collectors.toList());
        int count = 1;

        for (String tablename : tableNames) {
            //需要忽略的表
            if(ignoreTable.containsKey(tablename)){
                continue;
            }else if(tablename.startsWith("temp_") || tablename.startsWith("sync_")){
                continue;
            }

            Map keyVals = new HashMap();
            para.put("table_name",tablename);
            List<String> upcolumns = iIotSyncDataDao.getTableColumn(para);
            List<String> columns = upcolumns.stream().map(String::toLowerCase).collect(Collectors.toList());
//            判断是否包含某些字段
//            if(columns.contains("***")){}
            int dataCount = iIotSyncDataDao.getDataCount(para);
            keyVals.put("dataCount", dataCount);

            count++;

            //自增获取Id
//            keyVals.put("id", seqService.getSeq("sync_table_config"));
            keyVals.put("name", tablename);

            Map savePara = new HashMap();
            savePara.put("table", "sync_table_config");
            savePara.put("fields", keyVals);

            logger.info("savePara:" + savePara);
            iLocalSyncDataDao.save(savePara);
        }
    }

    //同步智塑云数据到本地
    public void syncDataToLocal(){

        Map para = new HashMap();
        para.put("table_schema", tableSchema);

        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.SSS");
        
        List<Map> SyncTableConfigs = ToLowerCaseForList(iLocalSyncDataDao.queryAllSyncTableConfig(null));
        for(Map SyncTableConfig : SyncTableConfigs){
            int dataCount = StringUtils.getIntFromMp(SyncTableConfig, "datacount");
            String tableName = StringUtils.getStringFromMap(SyncTableConfig, "name");

            if(dataCount < 1){
                //若为空表，直接跳过
                logger.info("SyncDataService::syncDataToLocal dataCount < 1 continue:" + tableName);
                continue;
            }else {
                //若有数据
                logger.info("SyncDataService::syncDataToLocal insert:" + tableName + " start");

                para.put("table_name",tableName);
                List<String> tableColumns = iIotSyncDataDao.getTableColumn(para);

                int overCount = dataCount;
                int limitCount = 0;
                int dataSize = 0;
                Map param = new HashMap();
                param.put("table_name", tableName);
                do{
                    param.put("limitCount", limitCount);
                    param.put("tableColumns", tableColumns);
                    long queryStartTime = System.currentTimeMillis();
                    List<Map> datas = ToLowerCaseForList(iIotSyncDataDao.getDataByLimit(param));
                    logger.info("SyncDataService::syncDataToLocal getData end:" + formatter.format(System.currentTimeMillis() - queryStartTime));
                    dataSize = datas.size();
                    logger.info("SyncDataService::syncDataToLocal " + tableName + " limitCount:" + limitCount + " ,dataCount:" + dataCount);
                    long saveStartTime = System.currentTimeMillis();
                    batchSaveTableData(datas, tableName);
                    logger.info("SyncDataService::syncDataToLocal batchSaveTableData end:" + formatter.format(System.currentTimeMillis() - saveStartTime));
                    limitCount += 1000;
                    overCount -= 1000;
                }while (overCount > 1000 || dataSize == 1000);
                logger.info("SyncDataService::syncDataToLocal insert:" + tableName + " end");
            }
        }
    }

    private void batchSaveTableData(List<Map> datas, String tableName){
        if(datas.size() > 0){
            Map savePara = new HashMap();
            savePara.put("table", tableName);
            savePara.put("datas", datas);
            try{
                iLocalSyncDataDao.batchSave(savePara);
            }catch (Exception e){
                logger.error("SyncDataService::saveTableData catch Exception:", e);
            }
        }
    }


    public List<Map> ToLowerCaseForList(List<Map> list) {
        if (list.size() > 0) {
            for(int i = 0; i < list.size(); ++i) {
                Map map = (Map)list.get(i);
                map = StringUtils.toLowerMap(map);
                list.set(i, map);
            }
        }

        return list;
    }

    public static void main(String[] args) {

    }

}
