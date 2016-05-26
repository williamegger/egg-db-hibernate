package com.egg.code.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.egg.code.commons.VelocityUtil;
import com.egg.code.vm.Tlp;

public class CodeBuilder {

	public static void main(String[] args) {
		CodeBuilder builder = new CodeBuilder();
		builder.build(Bean.class);
	}

	// ==========================================

	private static final String iDaoFile = "d:/code/IDao/I{0}Dao.java";
	private static final String daoFile = "d:/code/Dao/{0}Dao.java";
	private static final String iSerFile = "d:/code/ISer/I{0}Service.java";
	private static final String serFile = "d:/code/Ser/{0}Service.java";

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

		Map<String, Object> map = null;
		for (Class<?> clazz : classes) {
			map = this.createContextMap(clazz);
			if (map == null || map.isEmpty()) {
				continue;
			}

			String filename = clazz.getSimpleName();
			VelocityUtil.buildFile(Tlp.PATH, "IDao.vm", MessageFormat.format(iDaoFile, filename), map);
			VelocityUtil.buildFile(Tlp.PATH, "Dao.vm", MessageFormat.format(daoFile, filename), map);
			VelocityUtil.buildFile(Tlp.PATH, "ISer.vm", MessageFormat.format(iSerFile, filename), map);
			VelocityUtil.buildFile(Tlp.PATH, "Ser.vm", MessageFormat.format(serFile, filename), map);
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

		return map;
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

	// ==========================================
	public class Bean {
		private Integer beanId;
		private String username;
		private String password;
		private String nickname;
		private Integer userType;
		private Integer status;
		private Integer point;
		private Integer videos;
		private Integer topics;
		private Boolean deleted;
		private Integer createMan;
		private Long createDate;
		private Integer lastMan;
		private Long lastDate;
	}

}
