package com.egg.code.commons;

import java.util.Collection;
import java.util.Map;

public class Common {
	
	public static boolean isRef(Class<?> cla) {
		return !(cla == Integer.class
				|| cla == Long.class
				|| cla == Double.class
				|| cla == Boolean.class
				|| cla == String.class
				|| cla == int.class
				|| cla == long.class
				|| cla == double.class
				|| cla == boolean.class
				|| cla == java.sql.Timestamp.class
		);
	}

	public static String firstUpper(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String firstLower(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	// 驼峰命名。aaa_xxx ---> aaaXxx
	public static String strToCamel(String str) {
		if (isBlank(str)) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		boolean toUpper = false;
		char c;
		for (int i = 0, len = str.length(); i < len; i++) {
			c = str.charAt(i);
			if (c == '_') {
				toUpper = true;
				continue;
			}
			if (toUpper) {
				result.append(String.valueOf(c).toUpperCase());
				toUpper = false;
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	// 下划线命名。aaaXxx ---> aaa_xxx
	public static String strToUnderline(String str) {
		if (isBlank(str)) {
			return "";
		}
		String result = "";
		return result;
	}

	public static boolean isBlank(Object obj) {
		if (obj == null) {
			return true;
		}

		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).toString().trim().isEmpty();
		} else if (obj instanceof Map<?, ?>) {
			return ((Map<?, ?>) obj).isEmpty();
		} else if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).isEmpty();
		} else if (obj instanceof Object[]) {
			return (((Object[]) obj).length == 0);
		}

		return false;
	}

}
