package com.wzp.mes.syncfactorydata;

import com.wzp.dao.iot.IIotSyncDataDao;
import com.wzp.dao.local.ILocalSyncDataDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class SyncFactoryDataApplicationTests {

	@Autowired
	private IIotSyncDataDao iIotSyncDataDao;

	@Autowired
	private ILocalSyncDataDao iLocalSyncDataDao;


	private Logger logger = LoggerFactory.getLogger(SyncFactoryDataApplicationTests.class);

	@Test
	public void path() {
		String path = System.getProperty("user.dir");
		System.out.println(path);
	}

}
