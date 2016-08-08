package com.egg.code.builder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egg.code.commons.VelocityUtil;
import com.egg.code.vm.Tlp;

public class ApiBuilder {

	public static void main(String[] args) {
	}

	private static final String baseUrl = "http://127.0.0.1/topDirectorServer";
	private static final String filepath = "D:/code/api/{0}API.html";

	public static void build(Class<?>... classes) {
		if (classes == null || classes.length == 0) {
			return;
		}

		for (Class<?> cla : classes) {
			System.out.print(cla.getSimpleName() + " ... ");

			final String java = readJavaFile(cla);
			List<String> methods = getMethods(cla);
			sort(java, methods);
			String comment, url, methodCN, params, paramsInput;
			boolean isJson = false;
			List<Map> methodList = new ArrayList<Map>();
			Map methodMap;
			for (String method : methods) {
				try {
					isJson = false;

					comment = subComment(java, method);
					methodCN = subMethodNameCN(comment);
					url = subUrl(comment);
					params = subParams(comment, method);
					isJson = (params == null || params.isEmpty() || params.charAt(0) == '{');
					paramsInput = buildParamsInput(params, method);

					methodMap = new HashMap();
					methodMap.put("comment", comment);
					methodMap.put("method", method);
					methodMap.put("methodCN", methodCN);
					methodMap.put("url", url);
					methodMap.put("isJson", isJson);
					methodMap.put("paramsInput", paramsInput);
					methodList.add(methodMap);
				} catch (Exception e) {
					System.out.println("parse method error. [" + method + "]");
					e.printStackTrace();
				}
			}

			Map map = new HashMap();
			map.put("baseUrl", baseUrl);
			map.put("Action", cla.getSimpleName());
			map.put("methodList", methodList);

			VelocityUtil.buildFile(Tlp.PATH, "action_api.vm", MessageFormat.format(filepath, cla.getSimpleName()), map);
			System.out.println("[OK]");
		}
	}

	private static String readJavaFile(Class<?> cla) {
		String path = cla.getResource("").getPath();
		path = path.replace("web/WEB-INF/classes", "src");
		path += cla.getSimpleName() + ".java";

		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			String line = null;
			while ((line = read.readLine()) != null) {
				sb.append(line.trim()).append("\r\n");
			}
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private static List<String> getMethods(Class<?> cla) {
		List<String> list = new ArrayList<String>();
		Method[] methods = cla.getDeclaredMethods();
		for (Method m : methods) {
			if (m.getModifiers() == Modifier.PUBLIC) {
				list.add(m.getName());
			}
		}

		return list;
	}

	private static List<String> sort(final String java, List<String> methods) {
		Collections.sort(methods, new Comparator<String>() {

			@Override
			public int compare(String m1, String m2) {
				int i1 = java.indexOf("public void " + m1 + "(");
				int i2 = java.indexOf("public void " + m2 + "(");
				return Math.max(-1, Math.min(i1 - i2, 1));
			}
		});

		return methods;
	}

	private static String subComment(final String java, String method) {
		int methodIndex = java.indexOf("public void " + method + "(");
		int endCommentIndex = java.lastIndexOf("*/", methodIndex);
		if (methodIndex != endCommentIndex + 4) {
			return null;
		}

		int startCommentIndex = java.lastIndexOf("/**", methodIndex);
		String comment = java.substring(startCommentIndex + 5, endCommentIndex);
		comment = comment.replaceAll("\\* ", "").replaceAll("\\*", "").replaceAll("<pre>", "").replaceAll("</pre>", "");

		return comment.trim();
	}

	private static String subUrl(final String comment) {
		if (comment == null || comment.isEmpty()) {
			return null;
		}
		int beginIndex = comment.indexOf("[");
		int endIndex = comment.indexOf("]");
		return comment.substring(beginIndex + 1, endIndex);
	}

	private static String subMethodNameCN(final String comment) {
		if (comment == null || comment.isEmpty()) {
			return null;
		}
		int beginIndex = comment.indexOf("[");
		return comment.substring(0, beginIndex).trim();
	}

	private static String subParams(final String comment, final String method) {
		if (comment == null || comment.isEmpty()) {
			return null;
		}
		int beginOffset = method.length() + 1;
		int beginIndex = comment.indexOf(method + "=");
		if (beginIndex == -1) {
			beginIndex = comment.indexOf("参数：");
			beginOffset = 3;
		}
		int endIndex = comment.indexOf("\r\n\r\n", beginIndex);
		if (beginIndex == -1 || endIndex == -1 || beginIndex > endIndex) {
			return null;
		}
		return comment.substring(beginIndex + beginOffset, endIndex).trim();
	}

	private static String buildParamsInput(String params, String method) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		boolean isJson = (params.charAt(0) == '{');
		if (isJson) {
			return buildInputForJSON(params, method);
		} else {
			return buildInputForKV(params);
		}
	}

	private static String buildInputForJSON(String params, String method) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		String tlp = "<textarea name=\"{0}\" rows=\"{1}\">\r\n{2}\r\n</textarea>";
		return MessageFormat.format(tlp, method, lines(params) + 1, params);
	}

	private static String buildInputForKV(String params) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		String inputTxtTlp = "<span>{0}: </span><input type=\"text\" name=\"{0}\" />";
		String inputFileTlp = "<span>{0}: </span><input type=\"file\" name=\"{0}\" />";
		String[] ps = params.split("\r\n");
		for (String p : ps) {
			String[] kv = p.split("=");
			if ("cbfile".equals(kv[0].trim()) || "BIN".equals(kv[1].trim())) {
				sb.append(MessageFormat.format(inputFileTlp, kv[0].trim())).append("\r\n");
			} else {
				sb.append(MessageFormat.format(inputTxtTlp, kv[0].trim())).append("\r\n");
			}
		}
		return sb.toString();
	}

	private static int lines(final String str) {
		if (str == null || str.isEmpty()) {
			return 0;
		}
		int lines = 1;
		for (int i = 0, len = str.length(); i < len; i++) {
			if (str.charAt(i) == '\r') {
				lines++;
			}
		}
		return lines;
	}

}
