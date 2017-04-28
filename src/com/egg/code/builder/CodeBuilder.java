package com.egg.code.builder;

import com.egg.code.commons.Common;
import com.egg.code.commons.VelocityUtil;
import com.egg.code.vm.Tlp;
import com.egg.common.utils.FileUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeBuilder {

    public static void main(String[] args) {
        CodeBuilder builder = new CodeBuilder();
        builder.build(
                Void.class
        );
    }

    // ================================================================
    // 根据项目修改
    // ================================================================
    // TODO : 根据项目修改
    private static final String rootPackage = "com.prj.server";
    private static final String idaoPackage = rootPackage + ".model.dao";
    private static final String daoPackage = rootPackage + ".model.dao.impl";
    private static final String iserPackage = rootPackage + ".model.service";
    private static final String serPackage = rootPackage + ".model.service.impl";
    private static final String searchBeanPackage = rootPackage + ".view.bean.searchBean";
    private static final String adminCtrlPackage = rootPackage + ".view.ctrl.admin";


    private static final String CommonsPackage = "com.egg.common";
    private static final String PageInfoPackage = "com.egg.db.hibernate.bean";
    private static final String DBHelperPackage = "com.egg.db.hibernate.helper";
    private static final String QueryPackage = "com.egg.db.hibernate.query";
    private static final String LogPackage = "org.slf4j";
    private static final String Log = "Logger";
    private static final String LogFactory = "LoggerFactory";
    private static final String getLog = "getLogger";

    // save file
    private static final String _DIR = "d:/_build/code/";
    private static final String iDaoFile = _DIR + idaoPackage.replaceAll("[.]", "/") + "/I{0}Dao.java";
    private static final String daoFile = _DIR + daoPackage.replaceAll("[.]", "/") + "/{0}Dao.java";
    private static final String iSerFile = _DIR + iserPackage.replaceAll("[.]", "/") + "/I{0}Service.java";
    private static final String serFile = _DIR + serPackage.replaceAll("[.]", "/") + "/{0}Service.java";
    private static final String searchBeanFile = _DIR + searchBeanPackage.replaceAll("[.]", "/") + "/{0}SearchBean.java";
    // 后台管理Ctrl
    private static final String ctrlFile = _DIR + adminCtrlPackage.replaceAll("[.]", "/") + "/PM{0}Ctrl.java";
    // 后台管理页面
    private static final String listFile = _DIR + "/page/{0}/list.xhtml";
    private static final String viewFile = _DIR + "/page/{0}/view.xhtml";
    private static final String addFile = _DIR + "/page/{0}/add.xhtml";
    private static final String editFile = _DIR + "/page/{0}/edit.xhtml";

    // 导出Excel
    private static final String exportBeanPackage = rootPackage + ".view.bean.exportBean";
    private static final String ExcelExportPackage = rootPackage + ".common.oa";
    private static final String exportBeanFile = _DIR + exportBeanPackage.replaceAll("[.]", "/") + "/{0}Bean.java";

    private static final String ExportActionPackage = rootPackage + ".view.action.back";
    private static final String ExportActionName = "ExportAction";
    private static final String ExportActionUrl = "/export";
    private static final String ExportActionFile = _DIR + ExportActionPackage.replaceAll("[.]", "/") + "/" + ExportActionName + ".java";

    public void build(Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            return;
        }

        buildServiceFactory(classes);

        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        for (Class<?> clazz : classes) {
            map = this.createContextMap(clazz);
            if (map == null || map.isEmpty()) {
                continue;
            }

            String filename = clazz.getSimpleName();
            String lFilename = Common.firstLower(clazz.getSimpleName());

            VelocityUtil.buildFile(Tlp.PATH, "IDao.vm", MessageFormat.format(iDaoFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "Dao.vm", MessageFormat.format(daoFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "ISer.vm", MessageFormat.format(iSerFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "Ser.vm", MessageFormat.format(serFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "SearchBean.vm", MessageFormat.format(searchBeanFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "ExportBean.vm", MessageFormat.format(exportBeanFile, filename), map);

            VelocityUtil.buildFile(Tlp.PATH, "pf_ctrl.vm", MessageFormat.format(ctrlFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "pf_view.vm", MessageFormat.format(viewFile, lFilename), map);
            VelocityUtil.buildFile(Tlp.PATH, "pf_edit.vm", MessageFormat.format(editFile, lFilename), map);
            VelocityUtil.buildFile(Tlp.PATH, "pf_add.vm", MessageFormat.format(addFile, lFilename), map);
            VelocityUtil.buildFile(Tlp.PATH, "pf_list.vm", MessageFormat.format(listFile, lFilename), map);

            mapList.add(map);
        }
        buildExportAction(mapList);

        System.out.println("// ===== over");
    }

    private Map<String, Object> createContextMap(Class<?> clazz) {
        Map<String, Object> map = new HashMap<String, Object>();

        // 包
        map.put("rootPackage", rootPackage);
        map.put("idaoPackage", idaoPackage);
        map.put("daoPackage", daoPackage);
        map.put("iserPackage", iserPackage);
        map.put("serPackage", serPackage);
        map.put("searchBeanPackage", searchBeanPackage);
        map.put("adminCtrlPackage", adminCtrlPackage);

        map.put("CommonsPackage", CommonsPackage);
        map.put("PageInfoPackage", PageInfoPackage);
        map.put("entityPackage", rootPackage + ".model.entity");
        map.put("ePackage", rootPackage + ".model.exception");

        map.put("DBHelperPackage", DBHelperPackage);
        map.put("QueryPackage", QueryPackage);
        map.put("LogPackage", LogPackage);
        map.put("Log", Log);
        map.put("LogFactory", LogFactory);
        map.put("getLog", getLog);

        map.put("exportBeanPackage", exportBeanPackage);
        map.put("ExcelExportPackage", ExcelExportPackage);
        map.put("ExportActionPackage", ExportActionPackage);
        map.put("ExportActionName", ExportActionName);
        map.put("ExportActionUrl", ExportActionUrl);

        // Entity
        String entityname = clazz.getSimpleName();
        map.put("Entity", entityname);
        map.put("entity", Common.firstLower(entityname));
        map.put("hasDel", false);
        map.put("hasCreateDate", false);
        map.put("hasLastDate", false);
        map.put("hasCreateMan", false);
        map.put("hasLastMan", false);
        String fieldName;
        boolean isFirst = true;
        List<Map<String, Object>> fieldList = new ArrayList<Map<String, Object>>();
        Map<String, Object> fMap = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getModifiers() == Modifier.PRIVATE) {
                fieldName = field.getName();
                if ("deleted".equals(fieldName)) {
                    map.put("hasDel", true);
                } else if ("createDate".equals(fieldName)) {
                    map.put("hasCreateDate", true);
                } else if ("lastDate".equals(fieldName)) {
                    map.put("hasLastDate", true);
                } else if ("createMan".equals(fieldName)) {
                    map.put("hasCreateMan", true);
                } else if ("lastMan".equals(fieldName)) {
                    map.put("hasLastMan", true);
                }

                if (isFirst) {
                    map.put("id", fieldName);
                    map.put("Id", Common.firstUpper(fieldName));
                    map.put("idType", field.getType().getSimpleName());
                    isFirst = false;
                } else {
                    fMap = new HashMap<String, Object>();
                    fMap.put("name", fieldName);
                    fMap.put("Name", Common.firstUpper(fieldName));
                    fMap.put("Type", field.getType().getSimpleName());
                    fMap.put("isRef", Common.isRef(field.getType()));

                    fieldList.add(fMap);
                }
            }
        }
        map.put("fieldList", fieldList);
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

    private void buildExportAction(List<Map<String, Object>> mapList) {
        try {
            if (mapList == null || mapList.isEmpty()) {
                return;
            }

            // 复制项目中的ExportAction.java，如果没有则创建新文件
            File codeFile = new File(ExportActionFile);
            if (!codeFile.exists()) {
                String fileName = ExportActionName + ".java";
                String prjRoot = System.getProperty("user.dir");
                String prjFilePath = prjRoot + "/src/" + ExportActionPackage.replaceAll("[.]", "/") + "/" + fileName;
                File prjFile = new File(prjFilePath);
                if (prjFile.exists()) {
                    FileUtil.copyFile(new File(prjFilePath), codeFile);
                } else {
                    VelocityUtil.buildFile(Tlp.PATH, "ExportAction.vm", ExportActionFile, new HashMap<String, Object>());
                }
            }
            final String txt = FileUtil.readText(codeFile);
            int headIndex = txt.indexOf("import");
            int footIndex = txt.lastIndexOf("}");

            // 生成import和方法
            StringBuilder importTxt = new StringBuilder();
            StringBuilder methodTxt = new StringBuilder();
            for (int i = 0, len = mapList.size(); i < len; i++) {
                importTxt.append(VelocityUtil.buildStr(Tlp.PATH, "ExportActionImport.vm", mapList.get(i)));
                methodTxt.append(VelocityUtil.buildStr(Tlp.PATH, "ExportActionMethod.vm", mapList.get(i)));
            }

            // 拼装结果
            StringBuilder rlt = new StringBuilder();
            rlt.append(txt.substring(0, headIndex))
                    .append(importTxt)
                    .append(txt.substring(headIndex, footIndex))
                    .append(methodTxt)
                    .append(txt.substring(footIndex));

            // 保存文件
            FileUtil.save(rlt.toString(), codeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    public class Bean {
        private Integer beanId;
        private String username;
        private String password;
        private String nickname;
        private Boolean deleted;
        private Integer createMan;
        private Long createDate;
        private Integer lastMan;
        private Long lastDate;
    }

}
