package com.egg.code.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;

public class JSONBuilder {

	public static void main(String[] args) {

		buildJSON(Void.class, true);
		System.out.println("// ------ over");
	}

	/**
	 * 生成toJSON()方法
	 */
	public static void buildJSON(Class<?> cla, boolean isLowerCase) {
		Field[] fields = cla.getDeclaredFields();

		System.out.println("/**");
		System.out.println(" * 转成JSON");
		System.out.println(" * <pre>");
		System.out.println(" * {");

		Class<?> type;
		String name = "";
		for (Field field : fields) {
			if (field.getModifiers() == Modifier.PRIVATE) {
				type = field.getType();
				name = field.getName();
				if (isLowerCase) {
					name = name.toLowerCase();
				}
				if (type == Integer.class || type == Long.class) {
					System.out.println(MessageFormat.format(" * \"{0}\" : xx,", name));
				} else if (type == String.class) {
					System.out.println(MessageFormat.format(" * \"{0}\" : \"xx\",", name));
				} else if (type == Boolean.class) {
					System.out.println(MessageFormat.format(" * \"{0}\" : 0/1,", name));
				} else {
					System.out.println(MessageFormat.format(" * \"{0}\" : '{'},", name));
				}
			}
		}
		System.out.println(" * }");
		System.out.println(" * </pre>");
		System.out.println(" */");

		System.out.println("public JSONObject toJSON() {");
		System.out.println("JSONObject json = new JSONObject();");
		for (Field field : fields) {
			if (field.getModifiers() == Modifier.PRIVATE) {
				type = field.getType();
				name = field.getName();
				if (isLowerCase) {
					name = name.toLowerCase();
				}
				if (type == Boolean.class) {
					System.out.println(
							MessageFormat.format("JSONTool.put(json, \"{0}\", {1} ? 1 : 0);", name, field.getName()));
				} else {
					System.out
							.println(MessageFormat.format("JSONTool.put(json, \"{0}\", {1});", name, field.getName()));
				}
			}
		}

		System.out.println("return json;");
		System.out.println("}");
	}
}
