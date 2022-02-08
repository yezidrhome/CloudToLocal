package com.wzp.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static int port = 8999;

    public static boolean checkNum(String strIn) {
        Pattern p = Pattern.compile("\\d+");
        return p.matcher(strIn).matches();
    }

	//首字母转小写
    public static String toLowerCaseFirstOne(String s)
    {
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s)
    {
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    public static String getStringFromMap(Map map,String key)
    {
        if(map.containsKey(key) && map.get(key) != null)
        {
            return map.get(key).toString().trim();
        }
        else {
            return "";
        }
    }

    public static String objToString(Object obj)
    {
    	if(obj == null)
    		return "";
    	else {
			return obj.toString();
		}
    }
    
    public static boolean isEmail(Object args) {
 	   if(args == null){
 			return false;
 		}else{
 			Pattern p = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
 			Matcher m = p.matcher(args.toString());
 			return m.matches();
 		}
    }
    
    public static BigDecimal getBigDecimalFromMp(Map map,String key)
    {

        if(map.containsKey(key) && map.get(key) != null)
        {
            if(map.get(key) instanceof BigDecimal)
            {
                return (BigDecimal)map.get(key);
            }
            else
            {
                String val1 = map.get(key).toString().trim();
                if(val1.length() == 0)
                {
                    return  BigDecimal.ZERO;
                }
                return new BigDecimal(val1);
            }
        }
        else {
            return BigDecimal.ZERO;
        }
    }
    public static int getIntFromMp(Map map,String key)
    {

        if(map.containsKey(key) && map.get(key) != null)
        {
            if(map.get(key) instanceof Integer)
            {
                return (Integer)map.get(key);
            }
            {
                return new Integer(map.get(key).toString());
            }
        }
        else {
            return 0;
        }
    }
	   
	public static String quotedStr0(String str) {
	    return String.format("'%s'", str);
	}
	
   public static String quotedStr1(String str) {
        return String.format("\"%s\"", str);
    }
   
   public static String fuzzyQueryStr(String str) {
	   return String.format("'%%%s%%'", str);
   }
	   
	public static boolean validateNull(Object args) {
		if (args == null)
		{
			return true;
		}
		
		if (args instanceof String && ((String)args).length() == 0)
		{
			return true;
		}
		
		return false;
	}

	public static String sendudp(String args) {
		String returnstr = "";
		try {

			DatagramSocket ds = null; // 建立套间字udpsocket服务

			try {
				ds = new DatagramSocket(port); // 实例化套间字，指定自己的port
			} catch (Exception e) {
				logger.error("StringUtils::sendudp catch Exception:", e);
			    //logger.error("Cannot open port! port:", port);
				if (port == 9999)
					port = 8999;
				port++;
				System.exit(1);
			}

			byte[] buf = args.getBytes(); // 数据
			InetAddress destination = null;
			try {
				destination = InetAddress.getLocalHost(); // 需要发送的地址
			} catch (UnknownHostException e) {
				logger.error("StringUtils::sendudp catch Exception:", e);
			}
			
			DatagramPacket dp = new DatagramPacket(buf, buf.length,
					destination, 10000);
			// 打包到DatagramPacket类型中（DatagramSocket的send()方法接受此类，注意10000是接受地址的端口，不同于自己的端口！）
			try {
				ds.setSoTimeout(1);
				ds.send(dp); // 发送数据
				int length = 1024;
				byte message[] = new byte[length];
				DatagramPacket packet = new DatagramPacket(message,	message.length);
				// 接受报文
				ds.receive(packet);
				// 显示时间
				returnstr = new String(packet.getData()).trim();
			} catch (Exception e) {
				logger.error("StringUtils::sendudp catch Exception:", e);
			}
			ds.close();
		} catch (Exception e) {
			logger.error("StringUtils::sendudp catch Exception:", e);
		}
		return returnstr;
	}

	/**
	 *  
	 */
	public static String chanageNull(String source, String target) 
	{
		if (source == null 
		        || source.length() == 0
				|| source.equalsIgnoreCase("null"))
		{
			return target;
		} else 
		{
			return source;
		}
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String filterHtml(String input) {
		if (input == null) {
			return null;
		}
		if (input.length() == 0) {
			return input;
		}
		input = input.replaceAll("&", "&amp;");
		input = input.replaceAll("<", "&lt;");
		input = input.replaceAll(">", "&gt;");
		input = input.replaceAll(" ", "&nbsp;");
		input = input.replaceAll("'", "&#39;");
		input = input.replaceAll("\"", "&quot;");
		return input.replaceAll("\n", "<br>");
	}

	// 生成单据号序列数，不足位的前置补零
	public static String makeSerialNumber(int num) {
		return String.format("%06d", num);
	}

	// 拼装SQL
	public static String makeSerialNumber(String sql, String... args) {
		return MessageFormat.format(sql, args);
	}

	// 拼装SQL中字符型字段的查询值，加上单引号
	public static String makeSQLVarcharParameter(String param) {
		return new StringBuffer().append("'").append(param).append("'")
				.toString();
	}

    public static Map toLowerMap(Map oldmap) 
    {
	   if (oldmap == null)
		   return oldmap;
	   
       Map newmap = new HashMap();
       Iterator iter = oldmap.entrySet().iterator();
       while (iter.hasNext()) {
           Map.Entry entry = (Map.Entry) iter.next();
           String key = ((String) entry.getKey()).toLowerCase();
           Object value = entry.getValue();
           if (value == null)
               value = "";
           newmap.put(key, value);
       }

       return newmap;
    }

    private static List<Map> ToLowerCaseForList(List<Map> list)
    {    	
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			map = StringUtils.toLowerWithHtmlEscape(map);
			list.set(i, map);
		}

		return list;
    }
   
    public static Map toLowerWithHtmlEscape(Map oldmap) 
    {
	    if (oldmap == null)
		   return oldmap;
	   
       Map newmap = new HashMap();
       Iterator iter = oldmap.entrySet().iterator();
       while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry) iter.next();
          String key = ((String) entry.getKey()).toLowerCase();
          Object value = entry.getValue();
          if (value instanceof Map) {
       	   value = toLowerWithHtmlEscape((Map)value);
          }
          else if (value instanceof List) {
       	   value = ToLowerCaseForList((List)value);
          }
          else if (value == null)
              value = "";
          else
       	   value = HtmlUtils.htmlEscape(value.toString());
          newmap.put(key, value);
       }

       return newmap;
    }
   
	public static Map toLowerCaseFirstOneMap(Map oldmap) 
	{
	    Map newmap = new HashMap();
		Iterator iter = oldmap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = toLowerCaseFirstOne((String) entry.getKey());
			Object value = entry.getValue();
			if (value == null)
				value = "";
			newmap.put(key, value);
		}

		return newmap;
	}

    public static HashMap toLowerMap(HashMap oldmap) 
    {
        HashMap newmap = new HashMap();
        Iterator iter = oldmap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = ((String) entry.getKey()).toLowerCase();
            Object value = entry.getValue();
            if (value == null)
                value = "";
            newmap.put(key, value);
        }

        return newmap;
    }
	   
    public static List resultSetToList(ResultSet rs)
            throws java.sql.SQLException {
        if (rs == null)
            return Collections.EMPTY_LIST;

        ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
        int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
        List list = new ArrayList();
        Map rowData = new HashMap();
        while (rs.next()) {
            rowData = new HashMap(columnCount);
            String temp = "";
            for (int i = 1; i <= columnCount; i++) {

                Object ds = rs.getObject(i);
                if (ds != null)
                    temp = rs.getObject(i).toString();
                else
                    temp = "";
                rowData.put(md.getColumnName(i), temp);
            }
            list.add(rowData);
        }
        return list;
    }

	public static String getSql(String tablename, Map pkcolMap, Map transMap,
			JSONObject jo2, String flags) {
		try {
			int flag = 0;
			int updatewhereflag = 0;
			int insertflag = 0;
			int deleteflag = 0;
			int selectflag = 0;
			int updateflag = 0;
			String jostr = jo2.toString();
			JSONObject jo = JSONObject.fromObject(jostr);
			String ins = " insert into " + tablename + " ";
			String ins1 = " (";
			String ins2 = " values (";
			String upd = " update " + tablename + " ";
			String upd1 = "set ";
			String upd2 = " where ";
			String del = " delete from " + tablename + " ";
			String del1 = " where ";
			String sel = " select * from " + tablename + " ";
			String sel1 = " where ";
			for (Iterator iter = jo.keys(); iter.hasNext();) 
			{
				String key = (String) iter.next();
				
				if (selectflag == 0) {
					String colvalue = "";
					colvalue = "'" + jo.get(key).toString() + "'";
					if (!(jo.get(key) instanceof String))
						colvalue = jo.get(key).toString();
					if (jo.get(key) == null && !(jo.get(key) instanceof String))
						colvalue = "null";
					if (transMap.containsKey(key))
						colvalue = "?";
					if (colvalue.contains("##kt:"))
						colvalue = colvalue.substring(1, colvalue.length() - 1)
								.replace("##kt:", "");
					if (pkcolMap.containsKey(key)) {
						sel1 = sel1 + key + "=" + colvalue;
						selectflag++;
					}
					;
				} else {
					String colvalue = "";
					colvalue = "'" + jo.get(key).toString() + "'";
					if (!(jo.get(key) instanceof String))
						colvalue = jo.get(key).toString();
					if (jo.get(key) == null && !(jo.get(key) instanceof String))
						colvalue = "null";
					if (transMap.containsKey(key))
						colvalue = "?";
					if (colvalue.contains("##kt:"))
						colvalue = colvalue.substring(1, colvalue.length() - 1)
								.replace("##kt:", "");
					if (pkcolMap.containsKey(key))
						sel1 = sel1 + " and " + key + "=" + colvalue;
				}

				if (updateflag == 0) {
					String colvalue = "";
					colvalue = "'" + jo.get(key).toString() + "'";
					if (!(jo.get(key) instanceof String))
						colvalue = jo.get(key).toString();
					if (jo.get(key) == null && !(jo.get(key) instanceof String))
						colvalue = "null";
					if (transMap.containsKey(key))
						colvalue = "?";
					if (colvalue.contains("##kt:"))
						colvalue = colvalue.substring(1, colvalue.length() - 1)
								.replace("##kt:", "");
					if (pkcolMap.containsKey(key) && updatewhereflag == 0) 
					{
						upd2 = upd2 + key + "=" + colvalue;
						updatewhereflag++;
					}
					;
					{
						upd1 = upd1 + key + "=" + colvalue;
						updateflag++;
					}
				} else {
					String colvalue = "";
					colvalue = "'" + jo.get(key).toString() + "'";
					if (!(jo.get(key) instanceof String))
						colvalue = jo.get(key).toString();
					if (jo.get(key) == null && !(jo.get(key) instanceof String))
						colvalue = "null";
					if (transMap.containsKey(key))
						colvalue = "?";
					if (colvalue.contains("##kt:"))
						colvalue = colvalue.substring(1, colvalue.length() - 1)
								.replace("##kt:", "");
					if (updatewhereflag > 0 && pkcolMap.containsKey(key))
						upd2 = upd2 + " and " + key + "=" + colvalue;
					if (updatewhereflag == 0 && pkcolMap.containsKey(key)) {
						upd2 = upd2 + " " + key + "=" + colvalue;
						updatewhereflag++;
					}
					upd1 = upd1 + "," + key + "=" + colvalue;
				}

				if (insertflag == 0) {
					ins1 = ins1 + key;
					String colvalue = "";
					colvalue = "'" + jo.get(key).toString() + "'";
					if (!(jo.get(key) instanceof String))
						colvalue = jo.get(key).toString();
					if (jo.get(key) == null && !(jo.get(key) instanceof String))
						colvalue = "null";
					if (transMap.containsKey(key))
						colvalue = "?";
					if (colvalue.contains("##kt:"))
						colvalue = colvalue.substring(1, colvalue.length() - 1)
								.replace("##kt:", "");
					ins2 = ins2 + colvalue;
					insertflag++;
				} else {
					ins1 = ins1 + "," + key;
					String colvalue = "";
					colvalue = "'" + jo.get(key).toString() + "'";
					if (!(jo.get(key) instanceof String))
						colvalue = jo.get(key).toString();
					if (jo.get(key) == null && !(jo.get(key) instanceof String))
						colvalue = "null";
					if (transMap.containsKey(key))
						colvalue = "?";
					if (colvalue.contains("##kt:"))
						colvalue = colvalue.substring(1, colvalue.length() - 1)
								.replace("##kt:", "");
					ins2 = ins2 + "," + colvalue;
				}

				if (deleteflag == 0) {
					String colvalue = "";
					colvalue = "'" + jo.get(key).toString() + "'";
					if (!(jo.get(key) instanceof String))
						colvalue = jo.get(key).toString();
					if (jo.get(key) == null && !(jo.get(key) instanceof String))
						colvalue = "null";
					if (transMap.containsKey(key))
						colvalue = "?";
					if (colvalue.contains("##kt:"))
						colvalue = colvalue.substring(1, colvalue.length() - 1)
								.replace("##kt:", "");
					if (pkcolMap.containsKey(key)) {
						del1 = del1 + key + "=" + colvalue;
						deleteflag++;
					}
					;
				} else {
					String colvalue = "";
					colvalue = "'" + jo.get(key).toString() + "'";
					if (!(jo.get(key) instanceof String))
						colvalue = jo.get(key).toString();
					if (jo.get(key) == null && !(jo.get(key) instanceof String))
						colvalue = "null";
					if (transMap.containsKey(key))
						colvalue = "?";
					if (colvalue.contains("##kt:"))
						colvalue = colvalue.substring(1, colvalue.length() - 1)
								.replace("##kt:", "");
					if (pkcolMap.containsKey(key))
						del1 = del1 + " and " + key + "=" + colvalue;
				}

			}

			ins1 = ins1 + ")";
			ins2 = ins2 + ")";
			ins = ins + ins1 + ins2;

			del = del + del1;
			sel = sel + sel1;
			upd = upd + upd1 + upd2;

			if (flags.equals("ins"))
				return ins;
			if (flags.equals("upd"))
				return upd;
			if (flags.equals("del"))
				return del;
			if (flags.equals("sel"))
				return sel;
			return "";

		} catch (Exception e) {
			logger.error("StringUtils::getSql catch Exception:", e);
			return e.toString();

		}
	}

	public static List<Map<String, Object>> parseJSON2List(String jsonStr) {
		JSONArray jsonArr = JSONArray.fromObject(jsonStr);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Iterator<JSONObject> it = jsonArr.iterator();
		while (it.hasNext()) {
			JSONObject json2 = it.next();
			list.add(parseJSON2Map(json2.toString()));
		}
		return list;
	}

	public static Map<String, Object> parseJSON2Map(String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 最外层解析
		JSONObject json = JSONObject.fromObject(jsonStr);
		for (Object k : json.keySet()) {
			Object v = json.get(k);
			// 如果内层还是数组的话，继续解析
			if (v instanceof JSONArray) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Iterator<JSONObject> it = ((JSONArray) v).iterator();
				while (it.hasNext()) {
					JSONObject json2 = it.next();
					list.add(parseJSON2Map(json2.toString()));
				}
				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), v);
			}
		}
		return map;
	}

	public static List<Map<String, Object>> getListByUrl(String url) {
		try {
			// 通过HTTP获取JSON数据
			InputStream in = new URL(url).openStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return parseJSON2List(sb.toString());
		} catch (Exception e) {
			logger.error("StringUtils::getListByUrl catch Exception:", e);
		}
		return null;
	}

	public static Map<String, Object> getMapByUrl(String url) 
	{
		try {
			// 通过HTTP获取JSON数据
			InputStream in = new URL(url).openStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return parseJSON2Map(sb.toString());
		} catch (Exception e) {
			logger.error("StringUtils::getMapByUrl catch Exception:", e);
		}
		return null;
	}

	public static List rs2list(ResultSet rs) throws Exception {
		List records = new ArrayList();
		try {

			ResultSetMetaData rsmd = rs.getMetaData();
			int fieldCount = rsmd.getColumnCount();
			while (rs.next()) {
				Map valueMap = new LinkedHashMap();
				for (int i = 1; i <= fieldCount; i++) {
					String fieldClassName = rsmd.getColumnClassName(i);
					String fieldName = rsmd.getColumnName(i).toLowerCase();
					// this._recordMappingToMap(fieldClassName, fieldName, rs,
					// valueMap);

					fieldName = fieldName.toLowerCase();
					// 优先规则：常用类型靠前
					if (fieldClassName.equals("java.lang.String")) {
						String s = rs.getString(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.lang.Integer")) {
						int s = rs.getInt(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);// 早期jdk需要包装，jdk1.5后不需要包装
						}
					} else if (fieldClassName.equals("java.lang.Long")) {
						long s = rs.getLong(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.lang.Boolean")) {
						boolean s = rs.getBoolean(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.lang.Short")) {
						short s = rs.getShort(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.lang.Float")) {
						float s = rs.getFloat(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.lang.Double")) {
						double s = rs.getDouble(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.sql.Timestamp")) {
						java.sql.Timestamp s = rs.getTimestamp(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.sql.Date")
							|| fieldClassName.equals("java.util.Date")) {
						java.util.Date s = rs.getDate(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.sql.Time")) {
						java.sql.Time s = rs.getTime(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.lang.Byte")) {
						byte s = rs.getByte(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, new Byte(s));
						}
					} else if (fieldClassName.equals("[B")
							|| fieldClassName.equals("byte[]")) {
						// byte[]出现在SQL Server中
						byte[] s = rs.getBytes(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.math.BigDecimal")) {
						BigDecimal s = rs.getBigDecimal(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.lang.Object")
							|| fieldClassName.equals("oracle.sql.STRUCT")) {
						Object s = rs.getObject(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.sql.Array")
							|| fieldClassName.equals("oracle.sql.ARRAY")) {
						java.sql.Array s = rs.getArray(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.sql.Clob")) {
						java.sql.Clob s = rs.getClob(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else if (fieldClassName.equals("java.sql.Blob")) {
						java.sql.Blob s = rs.getBlob(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					} else {// 对于其它任何未知类型的处理
						Object s = rs.getObject(fieldName);
						if (rs.wasNull()) {
							valueMap.put(fieldName, null);
						} else {
							valueMap.put(fieldName, s);
						}
					}
				}
				records.add(valueMap);
			}
			rs.close();
		} finally {
			rs.close();
		}

		return records;
	}

	/**
	 * 字符串是否有值判断 （true:有值)
	 * @param s
	 * @return
	 */
	public static Boolean isEmpty(String s) {
		if (s != null && !"".equals(s))
			return false;
		return true;
	}
	
    // 截取数字
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }
   
    public static String sideTrim(String stream, String trimstr) {
        // null或者空字符串的时候不处理
        if (stream == null || stream.length() == 0 || trimstr == null || trimstr.length() == 0) {
          return stream;
        }
     
        // 结束位置
        int epos = 0;
     
        // 正规表达式
        String regpattern = "[" + trimstr + "]*+";
        Pattern pattern = Pattern.compile(regpattern, Pattern.CASE_INSENSITIVE);
     
        // 去掉结尾的指定字符 
        StringBuffer buffer = new StringBuffer(stream).reverse();
        Matcher matcher = pattern.matcher(buffer);
        if (matcher.lookingAt()) {
          epos = matcher.end();
          stream = new StringBuffer(buffer.substring(epos)).reverse().toString();
        }
     
        // 去掉开头的指定字符 
        matcher = pattern.matcher(stream);
        if (matcher.lookingAt()) {
          epos = matcher.end();
          stream = stream.substring(epos);
        }
     
        // 返回处理后的字符串
        return stream;
     }
    
    /**
     * 生成指定长度的随机数字和字母
     * @param length
     * @return
     */
    public static String getStringRandom(int length) {
       String val = "";
       Random random = new Random();
       for (int i = 0; i < length; i++) {
          String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
          switch (charOrNum) {
             case "char":
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
                break;
             case "num":
                val += String.valueOf(random.nextInt(10));
                break;
          }
       }
       return val;
    }
    
   private static Logger logger = LoggerFactory.getLogger(StringUtils.class);
}
