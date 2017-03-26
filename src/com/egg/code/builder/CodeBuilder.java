package com.egg.code.builder;

import com.egg.code.commons.Common;
import com.egg.code.commons.VelocityUtil;
import com.egg.code.vm.Tlp;

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
            String lFilename = Common.firstLower(clazz.getSimpleName());

            VelocityUtil.buildFile(Tlp.PATH, "IDao.vm", MessageFormat.format(iDaoFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "Dao.vm", MessageFormat.format(daoFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "ISer.vm", MessageFormat.format(iSerFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "Ser.vm", MessageFormat.format(serFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "SearchBean.vm", MessageFormat.format(searchBeanFile, filename), map);

            VelocityUtil.buildFile(Tlp.PATH, "pf_ctrl.vm", MessageFormat.format(ctrlFile, filename), map);
            VelocityUtil.buildFile(Tlp.PATH, "pf_view.vm", MessageFormat.format(viewFile, lFilename), map);
            VelocityUtil.buildFile(Tlp.PATH, "pf_edit.vm", MessageFormat.format(editFile, lFilename), map);
            VelocityUtil.buildFile(Tlp.PATH, "pf_add.vm", MessageFormat.format(addFile, lFilename), map);
            VelocityUtil.buildFile(Tlp.PATH, "pf_list.vm", MessageFormat.format(listFile, lFilename), map);
        }

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
