/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.db;

import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.exception.FrogCheckException;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.VirtualTable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author f90fd4n david
 * @version 1.0.0: DBDatasProcessor.java, v 0.1 2021-09-16 3:25 下午 david Exp $$
 */
@Slf4j
public class DBDataProcessor {
	private JdbcTemplate jdbcTemplate;

	public DBDataProcessor(DataSource ds) {
		this.jdbcTemplate = new JdbcTemplate(ds);
	}

	public boolean importDepDBData(List<VirtualTable> depTables) {
		if (depTables == null || depTables.isEmpty()) {
			return true;
		}
		// 表
		depTables.forEach(table -> {
			log.info("准备表数据:{}", table.getTableName());
			table.getTableData().forEach(record -> {
				List<String> fields = filterByColumns(record);
				String sql = genInsertSql(table.getTableName(), fields);
				log.info("准备表记录数据执行脚本:{}", sql);
				doImport(record, sql, fields);
			});
		});
		return true;
	}

	/**
	 * Execute SQL to import data
	 *
	 * @param record
	 * @param sql
	 * @param fields
	 */
	protected void doImport(Map<String, Object> record, String sql, List<String> fields) {
		if (record == null || record.isEmpty()) {
			return;
		}

		List<Object> args = new ArrayList<>();
		for (String field : fields) {
			args.add(record.get(field));
		}
		//execute sql
		jdbcTemplate.update(sql, args.toArray());
		String message = "";
		for (Object arg : args) {
			message += arg + ",";
		}
		log.info("Executing sql:" + sql + ",parameters:" + message);
	}

	protected List<String> filterByColumns(Map<String, Object> row) {

		List<String> allFields = new ArrayList<>();
		for (String key : row.keySet()) {
			allFields.add(key);
		}
		return allFields;
	}

	protected String genInsertSql(String tableName, List<String> fields) {
		StringBuffer fieldBuffer = new StringBuffer();
		StringBuffer fieldPlaceholderBuffer = new StringBuffer();

		for (String field : fields) {
			fieldBuffer.append(field).append(",");
			fieldPlaceholderBuffer.append("?").append(",");
		}

		String fieldPart = "insert into " + tableName + " ("
				+ fieldBuffer.substring(0, fieldBuffer.length() - 1) + ") ";

		String placeholderPart = " ("
				+ fieldPlaceholderBuffer.substring(0,
				fieldPlaceholderBuffer.length() - 1) + ") ";
		return fieldPart + "values" + placeholderPart;
	}

	/**
	 * Clean db datas.
	 *
	 * @param depTables the dep tables
	 */
	public void cleanDBData(List<VirtualTable> depTables) {
		if (depTables == null || depTables.isEmpty()) {
			return;
		}
		for (int i = 0; i < depTables.size(); i++) {
			VirtualTable record = depTables.get(i);
			if (record == null) {
				continue;
			}
			// 表名
			String tableName = record.getTableName();
			if (StringUtils.isEmpty(tableName)) {
				throw new FrogTestException("数据格式非法,需要清理数据但是表名为空");
			}
			// 获取表明对应的查询keys
			List<String> selectKeys = GlobalConfigurationHolder.getSelectKeys(tableName);
			// 生成delete sql
			String sql = genDeleteSql(tableName, selectKeys);
			//Assembly parameter and execution sql
			doDelete(record, sql, selectKeys);
		}
	}

	/**
	 * Generate SQL template
	 *
	 * @param tableName
	 * @param selectKeys
	 * @return
	 */
	protected String genDeleteSql(String tableName, List<String> selectKeys) {

		String prefixPart = "delete from " + tableName + " where ";

		String wherePart = "";

		for (String key : selectKeys) {
			wherePart = wherePart + " and " + key + " = ?";
		}

		return prefixPart + StringUtils.substringAfter(wherePart, "and ");
	}

	protected void doDelete(VirtualTable table, String sql, List<String> selectKeys) {
		List<Map<String, Object>> rows = table.getTableData();

		for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
			Map<String, Object> rowData = rows.get(rowNum);
			Object[] args = new Object[selectKeys.size()];
			int i = 0;
			for (; i < args.length; ++i) {
				args[i] = rowData.get(selectKeys.get(i));
			}

			jdbcTemplate.update(sql, args);
			String message = "";
			for (Object arg : args) {
				message += arg + ",";
			}
			log.info("Executing sql:" + sql + ",parameters:" + message);
		}
	}


	public void compare2DBData(List<VirtualTable> virtualTables) {

		// 表
		virtualTables.forEach(table -> {
			log.info("比较表数据:{}", table.getTableName());
			String sql = genSelectSql(table);
			log.info("比较表记录数据执行脚本:{}", sql);
			try {
				doSelectAndCompare(table, sql);
			} catch (FrogCheckException e) {
				throw new FrogCheckException("\n======>表:{}数据校验失败,失败原因:{}", table.getTableName(), e.getMessage());
			} catch (Exception e) {
				throw new FrogTestException("check db data for table: " + table.getTableName() + "error", e);
			}
		});

	}

	protected String genSelectSql(VirtualTable table) {

		String tableName = table.getTableName();


		//Where condition ,Only the default query conditions are supported first
		StringBuffer wherePartBuffer = new StringBuffer();
		List<String> selectKeys = GlobalConfigurationHolder.getSelectKeys(tableName);

		for (String selectKey : selectKeys) {
			wherePartBuffer.append("(" + selectKey + " = ? " + " ) and ");
		}

		String wherePart = wherePartBuffer.substring(0, wherePartBuffer.length() - 4);

		return "select * from " + tableName + " where " + wherePart;
	}

	/**
	 * 按照约定大于配置的思想，这里不做笛卡尔积比较，例如expect:(a1,a2,a3,a4),actual:(b1,b2,b3,b4)
	 * 先根据selectKeys分组，然后按照顺序依次比较(这就要求expect中数据顺序与db中一致)
	 *
	 * @param table
	 * @param sql
	 * @return
	 */
	protected void doSelectAndCompare(VirtualTable table, String sql) {

		List<Map<String, Object>> expects = table.getTableData();

		//The actual value of the placeholder in the select statement
		List<String> selectKeys = GlobalConfigurationHolder.getSelectKeys(table.getTableName());

		// 分组
		// k:selectKey1_selectKey2, v:list
		Map<String, List<Map<String, Object>>> expectGroup = Maps.newHashMap();
		expects.forEach(map -> {
			// 确定key
			StringBuilder sb = new StringBuilder();
			int cnt = 0;
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if (selectKeys.contains(entry.getKey())) {
					sb.append(entry.getValue()).append("_");
					cnt++;
					if (cnt == selectKeys.size()) {
						break;
					}
				}
			}
			String key = StringUtils.substringBeforeLast(sb.toString(), "_");
			if (expectGroup.get(key) == null) {
				expectGroup.put(key, Lists.newArrayList());
			}
			expectGroup.get(key).add(map);
		});
		// 查询
		expectGroup.forEach((k, v) -> {
			// 解析key，组装查询参数
			String[] vals = k.split("_");
			// db
			List<Map<String, Object>> actuals = jdbcTemplate.queryForList(sql, vals);
			String message = "";
			for (Object arg : vals) {
				message += arg + ",";
			}
			log.info("Executing sql:" + sql + ",parameters:" + message);
			// 比较
			// 数目一致
			if (v.size() != actuals.size()) {
				throw new FrogCheckException("\n======>表:{},数据比较不一致,根据key:{},期望条数:{},实际条数:{}", table.getTableName(), message, v.size(), actuals.size());
			}
			// 逐个比较
			for (int i = 0; i < v.size(); i++) {
				Map<String, Object> expect = v.get(i);
				Map<String, Object> actual = actuals.get(i);
				Iterator<String> iterator = expect.keySet().iterator();
				// 由于数据库比较不存在复杂对象，所以可以偷懒，从expect compare to actual,如果expect中没有字段默认无需比较，减少工作量，当然全字段比较才是正确方式
				while (iterator.hasNext()) {
					String fieldName = iterator.next();
					Object expectedFieldValue = expect.get(fieldName);
					Object actualFieldValue = actual.get(fieldName);

					//obtain current flag, compatible with case
					String currentFlag = table.getFlagByFieldNameIgnoreCase(fieldName);
					if (currentFlag != null) {
						if (currentFlag.equalsIgnoreCase("N")) {
							continue;
						} else if (currentFlag.startsWith("D")) {
							//date
							String tmp = currentFlag.replace("D", "");
							long timeFlow = Long.valueOf(StringUtils.isEmpty(tmp) ? "0" : tmp);
							Date realD;
							Date expectD;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							try {
								if (actual.get(fieldName) instanceof Date) {
									realD = (Date) actual.get(fieldName);
								} else {
									realD = sdf.parse((String) actual.get(fieldName));
								}
								if (expectedFieldValue instanceof Date) {
									expectD = (Date) expectedFieldValue;
								} else if (((String) expectedFieldValue).equalsIgnoreCase("now()")) {
									expectD = new Date();
								} else if (((String) expectedFieldValue).equalsIgnoreCase("today")) {
									expectD = new Date();
								} else {
									expectD = sdf.parse((String) expectedFieldValue);
								}
							} catch (ParseException e) {
								throw new FrogTestException(table.getTableName() + " key:  "
										+ fieldName
										+ " is not Date or valid date format");
							}
							Long realTime = realD.getTime();
							Long expectTime = expectD.getTime();
							//different from the given value
							if (Math.abs((realTime - expectTime) / 1000) > timeFlow) {
								throw new FrogCheckException("\n======>" + "Failed checking db param, tableName: "
										+ table.getTableName() + " key:  " + fieldName
										+ " ,value is " + realD + " expect value is " + expectD
										+ "time shift: " + realD.compareTo(expectD) + " is over "
										+ timeFlow);
							}
							continue;
						} else if (currentFlag.equals("R")) {
							Pattern pattern = Pattern.compile(expectedFieldValue.toString());
							Matcher matcher = pattern.matcher(actual.get(fieldName)
									.toString());
							boolean matchRes = matcher.matches();
							if (!matchRes) {
								throw new FrogCheckException("\n======>" + "The comparison of the db fields is failed, table:"
										+ table.getTableName() + " key:" + fieldName
										+ " ,value is " + actual.get(fieldName)
										+ " expect value is " + expectedFieldValue);
							}
							continue;
						} else if (currentFlag.startsWith("L")) {

							String tmp = currentFlag.replace("L", "");
							long minVal = Long.valueOf(StringUtils.isEmpty(tmp) ? "0" : tmp);
							if ((Long) actualFieldValue >= minVal) {
								continue;
							}
						}
					}

					// 1.时间校验
					if (expectedFieldValue instanceof String && toDate((String) expectedFieldValue) != null) {
						Date actualDate = (Date) actual.get(fieldName);
						Date expectDate = toDate((String) expectedFieldValue);

						if (actualDate.compareTo(expectDate) != 0) {
							throw new FrogCheckException("\n======>" + "The comparison of the db fields is failed, table:"
									+ table.getTableName() + " key:" + fieldName + " ,value is "
									+ actualDate + " expect vaule is " + expectDate);
						}
					} else {
						// 2.空校验
						//Do not distinguish between "" and null of string fields in the database
						if ((expectedFieldValue == null || StringUtils.equals(String.valueOf(expectedFieldValue), ""))) {

							if (actualFieldValue != null) {

								if (!StringUtils.equals(String.valueOf(actualFieldValue), "null")
										&& !StringUtils.equals(String.valueOf(actualFieldValue), "")) {
									throw new FrogCheckException("\n======>" + "The comparison of the db fields is failed, table:"
											+ table.getTableName() + " key:" + fieldName
											+ " ,value is " + actualFieldValue + " expect vaule is "
											+ expectedFieldValue);
								}
							}
						} else
							// 其他校验
							if (!String.valueOf(expectedFieldValue).equals(String.valueOf(actualFieldValue))) {
								throw new FrogCheckException("\n======>" + "The comparison of the db fields is failed, table:"
										+ table.getTableName() + " key:" + fieldName + " ,value is "
										+ actual.get(fieldName) + " expect vaule is "
										+ expectedFieldValue);
							}

					}
					log.info("The comparison of the db fields is successful, table:"
							+ table.getTableName() + " ,key:" + fieldName + " ,value is "
							+ actual.get(fieldName) + " expect vaule is "
							+ expectedFieldValue);
				}
			}
		});
	}

	public List<Map<String, Object>> queryForList(String sql) {
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		return result;
	}

	private static Date toDate(String dateStr) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date result = null;
		try {
			result = df.parse(dateStr);
		} catch (ParseException e) {
			// do nothing
		}
		return result;
	}

}