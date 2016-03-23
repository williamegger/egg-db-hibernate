package com.egg.code.commons;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class Common {

	public static void buildFile(String tlpPath, String tlpName, String filepath, Map<String, Object> contextMap) {
		Writer writer = null;
		try {
			File file = new File(filepath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			VelocityEngine engine = new VelocityEngine();
			Properties p = new Properties();
			p.setProperty("file.resource.loader.path", tlpPath);
			p.setProperty("input.encoding", "UTF-8");
			p.setProperty("output.encoding", "UTF-8");
			p.setProperty("runtime.log.info.stacktrace", "false");
			p.setProperty("runtime.log", "d:/logs/velocity.log");
			p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
			engine.init(p);

			VelocityContext context = new VelocityContext(contextMap);
			Template tlp = engine.getTemplate(tlpName);
			writer = new FileWriter(file);
			tlp.merge(context, writer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static String firstUpper(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String firstLower(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

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
