package com.egg.code.commons;

import java.util.Collection;
import java.util.Map;

public class UtilFun {

    public static UtilFun getInstance() {
        return InstanceHandler.instance;
    }

    private UtilFun() {
    }

    public boolean isBlank(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof CharSequence) {
            CharSequence val = (CharSequence) obj;
            return val.toString().trim().isEmpty();
        } else if (obj instanceof Map) {
            Map val = (Map) obj;
            return val.isEmpty();
        } else if (obj instanceof Object[]) {
            Object[] val = (Object[]) obj;
            return val.length == 0;
        } else if (obj instanceof Collection) {
            Collection val = (Collection) obj;
            return val.isEmpty();
        }
        return false;
    }

    public boolean isNotBlank(Object obj) {
        return !isBlank(obj);
    }

    public boolean endsWith(Object obj, String end) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CharSequence)) {
            return false;
        }
        return ((CharSequence) obj).toString().endsWith(end);
    }

    public String escape(String obj) {
        if (obj == null) {
            return "";
        }
        obj = obj.replaceAll("&", "&amp;")
                .replaceAll("'", "&apos;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
        return obj;
    }

    private static class InstanceHandler {
        private static final UtilFun instance = new UtilFun();
    }

}
