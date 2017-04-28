package com.egg.code.builder;

import com.egg.code.commons.Common;
import com.egg.code.commons.VelocityUtil;
import com.egg.code.sql.MySQLUtil;
import com.egg.code.vm.Tlp;

import java.util.*;

public class EntityBuilder {

    public static void main(String[] args) {
        // 根据项目修改
        final String database = "mj_anhui";
        final String packageName = "com.prj.server.model.entity";

        String fileDir = "D:/_build/code/" + packageName.replaceAll("[.]", "/");
        build(new String[]{
                "card_room_base_info"
        }, database, packageName, fileDir);

        System.out.println("OVER");
    }

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
        VelocityUtil.buildFile(Tlp.PATH, "Entity.vm", entityPath, contextMap);
        String xmlPath = fileDir + "/" + contextMap.get("Entity") + ".hbm.xml";
        VelocityUtil.buildFile(Tlp.PATH, "EntityXml.vm", xmlPath, contextMap);
    }

    private static void initTypes() {
        wrap_types.put("int", "Integer");
        wrap_types.put("tinyint", "Boolean");
        wrap_types.put("bit", "Integer");
        wrap_types.put("bit1", "Boolean");
        wrap_types.put("bigint", "Long");
        wrap_types.put("decimal", "Double");
        wrap_types.put("varbinary", "String");
        wrap_types.put("varchar", "String");
        wrap_types.put("text", "String");
        wrap_types.put("timestamp", "java.sql.Timestamp");
    }

    private static String getJavaWrapType(String sqlType, Integer precision) {
        if (wrap_types.isEmpty()) {
            initTypes();
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

        String nullable = (String) column.get("nullable");
        if (nullable == null || !"NO".equalsIgnoreCase(nullable)) {
            nullable = "false";
        } else {
            nullable = "true";
        }
        column.put("nullable", nullable);

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
            String javaType = getJavaWrapType((String) column.get("type"), (Integer) column.get("precision"));
            field.put("type", javaType);
            if (javaType.startsWith("java")) {
                field.put("fullType", javaType);
            } else {
                field.put("fullType", "java.lang." + javaType);
            }
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
