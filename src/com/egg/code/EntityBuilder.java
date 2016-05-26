package com.egg.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.egg.code.commons.Common;
import com.egg.code.sql.MySQLUtil;

public class EntityBuilder {

	public static void main(String[] args) {
		String database = "iteacher";
		String packageName = "com.bbt.iteacher.server.model.entity";
		String fileDir = "D:/code";
//		build(database, packageName, fileDir);
		build(new String[] {
				"banner_detail",
				"banner_log",
				"banner_stat",
				"banner",
				"advertiser",
				"banner_position"
			}, database, packageName, fileDir);

		System.out.println("OVER");
	}

	private static final String BASE = EntityBuilder.class.getClassLoader().getResource("").getPath();
	private static final String TLP_PATH = BASE + EntityBuilder.class.getPackage().getName().replaceAll("[.]", "/");
	private static final Map<String, String> wrap_types = new HashMap<String, String>();

	public static void build(String database, String packageName, String fileDir) {
		List<String> tables = MySQLUtil.getInstance().getTables(database);
		for (String table : tables) {
			build(table, database, packageName, fileDir);
		}
	}

	public static void build(String[] tables, String database, String packageName, String fileDir) {
		for (String table : tables) {
			build(table, database, packageName, fileDir);
		}
	}

	public static void build(String table, String database, String packageName, String fileDir) {
		List<Map<String, Object>> columns = MySQLUtil.getInstance().getColumns(database, table);
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put("table", table);
		contextMap.put("packageName", packageName);
		contextMap.put("refs", getRefs(columns));
		contextMap.put("entity", Common.strToCamel(table));
		contextMap.put("Entity", Common.firstUpper(Common.strToCamel(table)));
		contextMap.put("fields", columnsToFields(columns));

		String entityPath = fileDir + "/" + contextMap.get("Entity") + ".java";
		Common.buildFile(TLP_PATH, "Entity.vm", entityPath, contextMap);
		String xmlPath = fileDir + "/" + contextMap.get("Entity") + ".hbm.xml";
		Common.buildFile(TLP_PATH, "EntityXml.vm", xmlPath, contextMap);
	}

	private static void intiTypes() {
		wrap_types.put("int", "Integer");
		wrap_types.put("bit", "Integer");
		wrap_types.put("bit1", "Boolean");
		wrap_types.put("bigint", "Long");
		wrap_types.put("decimal", "Double");
		wrap_types.put("varbinary", "String");
		wrap_types.put("varchar", "String");
		wrap_types.put("text", "String");
	}

	private static String getJavaWrapType(String sqlType, Integer precision) {
		if (wrap_types.isEmpty()) {
			intiTypes();
		}
		if (Common.isBlank(sqlType)) {
			return "";
		}
		if ("bit".equals(sqlType) && precision == 1) {
			sqlType += precision;
		}
		return wrap_types.get(sqlType);
	}

	private static List<Map<String, Object>> columnsToFields(List<Map<String, Object>> columns) {
		if (Common.isBlank(columns)) {
			return null;
		}
		List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
		Map<String, Object> field;
		for (int i = 0, len = columns.size(); i < len; i++) {
			field = columnToField(columns.get(i));
			field.put("isId", (i == 0));
			fields.add(field);
		}
		return fields;
	}

	private static Map<String, Object> columnToField(Map<String, Object> column) {
		if (Common.isBlank(column)) {
			return null;
		}
		Map<String, Object> field = new HashMap<String, Object>();
		field.put("columnInfo", column);
		field.put("column", column.get("name"));
		if (!Common.isBlank(column.get("ref"))) {
			field.put("isRef", true);
			String name = (String) column.get("name");
			if (name.endsWith("id")) {
				name = name.substring(0, name.length() - 2);
			}
			field.put("name", Common.strToCamel(name));
			field.put("Name", Common.firstUpper((String) field.get("name")));
			field.put("type", Common.firstUpper(Common.strToCamel((String) column.get("ref"))));
		} else {
			field.put("isRef", false);
			field.put("name", Common.strToCamel((String) column.get("name")));
			field.put("Name", Common.firstUpper((String) field.get("name")));
			field.put("type", getJavaWrapType((String) column.get("type"), (Integer) column.get("precision")));
		}
		return field;
	}

	private static Set<String> getRefs(List<Map<String, Object>> columns) {
		if (Common.isBlank(columns)) {
			return null;
		}
		Set<String> refs = new HashSet<String>();
		for (Map<String, Object> column : columns) {
			if (!Common.isBlank(column.get("ref"))) {
				refs.add(Common.firstUpper(Common.strToCamel((String) column.get("ref"))));
			}
		}
		return refs;
	}

}
