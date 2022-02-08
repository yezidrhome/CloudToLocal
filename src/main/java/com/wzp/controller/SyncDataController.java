package com.wzp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzp.service.SyncDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 同步数据接口调用
 */
@RestController
public class SyncDataController {

    private static ObjectMapper MAPPER = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(SyncDataController.class);

    @Autowired
    private SyncDataService syncDataService;

    /**
     * 同步数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/syncData/start", method = {RequestMethod.POST,
            RequestMethod.GET}, produces = "text/json; charset=utf-8")
    @CrossOrigin(origins = "*", maxAge = 3600)
    public String syncData(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String sessionId = request.getHeader("sessionid");
        response.setHeader("SessionId", sessionId);
        Map<String, Object> retMap = new HashMap<>();
        try {
            logger.info("Start syncData...");
            syncDataService.getAllTable();
            syncDataService.syncDataToLocal();
            logger.info("Success End syncData...");

            retMap.put("code", 0);
            retMap.put("msg", "success");
            return MAPPER.writeValueAsString(retMap);
        } catch (Exception e) {
            logger.error("SyncDataController::syncData catch exception:", e);
            retMap.put("code", 1);
            retMap.put("msg", "failed");
            try {
                return MAPPER.writeValueAsString(retMap);
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
                return "报错信息："+e1;
            }
        }
    }

}
