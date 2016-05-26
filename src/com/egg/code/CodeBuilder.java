package com.egg.code;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class CodeBuilder {

	private static final String BASE = CodeBuilder.class.getClassLoader().getResource("").getPath();
	private static final String TLP_PATH = BASE + CodeBuilder.class.getPackage().getName().replaceAll("[.]", "/");
	private static final String UTF8 = "UTF-8";

	public static void main(String[] args) {
		CodeBuilder builder = new CodeBuilder();
		builder.build(Bean.class);
	}

	// ==========================================

	private static final String rootPackage = "com.egg";
	private static final String DBHelperPackage = "com.egg.db.hibernate.helper";
	private static final String QueryPackage = "com.egg.db.hibernate.query";
	private static final String LogPackage = "org.slf4j";
	private static final String Log = "Logger";
	private static final String LogFactory = "LoggerFactory";
	private static final String getLog = "getLogger";

	public void build(Class<?>... classes) {
		if (classes == null || classes.length == 0) {
			return;
		}

		buildServiceFactory(classes);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		for (Class<?> clazz : classes) {
			map = this.createContextMap(clazz);
			if (map == null || map.isEmpty()) {
				continue;
			}
			this._buildFile("IDao.vm", map);
			this._buildFile("Dao.vm", map);
			this._buildFile("ISer.vm", map);
			this._buildFile("Ser.vm", map);
			list.add(map);
		}

		System.out.println("// ===== over");
	}

	private Map<String, Object> createContextMap(Class<?> clazz) {
		Map<String, Object> map = new HashMap<String, Object>();

		// 包
		map.put("entityPackage", rootPackage + ".model.entity");
		map.put("idaoPackage", rootPackage + ".model.dao");
		map.put("daoPackage", rootPackage + ".model.dao.impl");
		map.put("iserPackage", rootPackage + ".model.service");
		map.put("serPackage", rootPackage + ".model.service.impl");
		map.put("ePackage", rootPackage + ".model.exception");
		
		map.put("DBHelperPackage", DBHelperPackage);
		map.put("QueryPackage", QueryPackage);
		map.put("LogPackage", LogPackage);
		map.put("Log", Log);
		map.put("LogFactory", LogFactory);
		map.put("getLog", getLog);

		// Entity
		String entityname = clazz.getSimpleName();
		map.put("Entity", entityname);
		map.put("entity", StringUtils.uncapitalize(entityname));
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getModifiers() == Modifier.PRIVATE) {
				map.put("id", field.getName());
				map.put("Id", StringUtils.capitalize(field.getName()));
				map.put("idType", field.getType().getSimpleName());
				break;
			}
		}
		map.put("hasDel", false);
		map.put("hasCreateDate", false);
		map.put("hasLastDate", false);
		String fieldName;
		for (Field field : fields) {
			fieldName = field.getName();
			if ("deleted".equals(fieldName)) {
				map.put("hasDel", true);
			} else if ("createDate".equals(fieldName)) {
				map.put("hasCreateDate", true);
			} else if ("lastDate".equals(fieldName)) {
				map.put("hasLastDate", true);
			}
		}

		// 文件保存路径
		map.put("IDao.vm", "d:/code/IDao/I" + entityname + "Dao.java");
		map.put("Dao.vm", "d:/code/Dao/" + entityname + "Dao.java");
		map.put("ISer.vm", "d:/code/ISer/I" + entityname + "Service.java");
		map.put("Ser.vm", "d:/code/Ser/" + entityname + "Service.java");
		return map;
	}

	private void _buildFile(String tlpName, Map<String, Object> contextMap) {
		Writer writer = null;
		try {
			File file = new File((String) contextMap.get(tlpName));
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			VelocityEngine engine = new VelocityEngine();
			Properties p = new Properties();
			p.setProperty("file.resource.loader.path", TLP_PATH);
			p.setProperty("input.encoding", UTF8);
			p.setProperty("output.encoding", UTF8);
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

	private String _toUnderlineName(String str) {
		if (str == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		char c;
		for (int i = 0, len = str.length(); i < len; i++) {
			c = str.charAt(i);
			if (StringUtils.isAllUpperCase(String.valueOf(c))) {
				if (i > 0) {
					sb.append("_");
				}
			}
			sb.append(String.valueOf(c).toLowerCase());
		}
		return sb.toString();
	}

	private void buildServiceFactory(Class<?>... classes) {
		if (classes == null || classes.length == 0) {
			return;
		}

		String tlp = "public static I{0}Service getI{0}Service() '{'return {0}Service.getInstance();'}'";
		System.out.println("---------------------");
		System.out.println("ServeFactory");
		System.out.println("---------------------");
		for (Class<?> cla : classes) {
			System.out.println(MessageFormat.format(tlp, cla.getSimpleName()));
		}
	}
}
