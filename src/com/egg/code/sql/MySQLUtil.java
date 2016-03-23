package com.egg.code.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySQLUtil extends SQLUtil {

	private static MySQLUtil instance = null;

	public static synchronized MySQLUtil getInstance() {
		if (instance == null) {
			instance = new MySQLUtil();
		}
		return instance;
	}

	private MySQLUtil() {
	}

	public List<String> getTables() {
		String sql = "select TABLE_NAME from `TABLES` where TABLE_SCHEMA = ? ";
		List<Map<String, Object>> maps = query(sql, database);
		if (isBlank(maps)) {
			return null;
		}

		List<String> result = new ArrayList<String>();
		for (Map<String, Object> map : maps) {
			result.add((String) map.get("TABLE_NAME"));
		}
		return result;
	}

	/**
	 * get information of columns in the table
	 * 
	 * <pre>
	 * key :
	 * name
	 * type
	 * length
	 * precision
	 * scale
	 * ref
	 * </pre>
	 */
	public List<Map<String, Object>> getColumns(String table) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	s1.COLUMN_NAME as `name`, ");
		sql.append("	s1.DATA_TYPE as `type`, ");
		sql.append("	s1.CHARACTER_MAXIMUM_LENGTH as `length`, ");
		sql.append("	s1.NUMERIC_PRECISION as `precision`, ");
		sql.append("	s1.NUMERIC_SCALE as `scale`, ");
		sql.append("	s2.REFERENCED_TABLE_NAME as `ref` ");
		sql.append("FROM ");
		sql.append("	`COLUMNS` AS s1 ");
		sql.append("LEFT JOIN KEY_COLUMN_USAGE AS s2 ON s1.TABLE_SCHEMA = s2.TABLE_SCHEMA ");
		sql.append("AND s1.TABLE_NAME = s2.TABLE_NAME ");
		sql.append("AND s1.COLUMN_NAME = s2.COLUMN_NAME ");
		sql.append("WHERE ");
		sql.append("	s1.TABLE_SCHEMA = ? ");
		sql.append("AND s1.TABLE_NAME = ? ");

		return query(sql, database, table);
	}

}
