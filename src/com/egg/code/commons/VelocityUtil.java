package com.egg.code.commons;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class VelocityUtil {

    public static void buildFile(String tlpPath, String tlpName, String filepath, Map<String, Object> contextMap) {
        Writer writer = null;
        try {
            File file = new File(filepath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            writer = new FileWriter(file);
            build(tlpPath, tlpName, contextMap, writer);
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


    public static String buildStr(String tlpPath, String tlpName, Map<String, Object> contextMap) {
        Writer writer = null;
        try {
            writer = new StringWriter();
            build(tlpPath, tlpName, contextMap, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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

    private static void build(String tlpPath, String tlpName, Map<String, Object> contextMap, Writer writer) {
        VelocityEngine engine = new VelocityEngine();
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", tlpPath);
        p.setProperty("input.encoding", "UTF-8");
        p.setProperty("output.encoding", "UTF-8");
        p.setProperty("runtime.log.info.stacktrace", "false");
        p.setProperty("runtime.log", "d:/_build/logs/velocity.log");
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
        engine.init(p);

        VelocityContext context = new VelocityContext(contextMap);
        context.put("util", UtilFun.getInstance());
        Template tlp = engine.getTemplate(tlpName);
        tlp.merge(context, writer);
    }

}
