package com.egg.code.commons;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class VelocityUtil {

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

}
