package com.egg.code;

import com.egg.common.log.LogKit;
import com.egg.common.utils.DateTimeUtil;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FacesTool {

    private static final String KEY_OPT = "optSuccess";

    public static synchronized FacesTool getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext extContext = facesContext.getExternalContext();
        HttpServletRequest req = (HttpServletRequest) extContext.getRequest();
        HttpServletResponse resp = (HttpServletResponse) extContext.getResponse();
        FacesTool instance = new FacesTool(req, resp);
        return instance;
    }

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;
    private Map<String, String[]> parameterMap;

    public FacesTool(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
        this.parameterMap = req.getParameterMap();
    }

    public HttpServletRequest request() {
        return req;
    }

    public HttpServletResponse response() {
        return resp;
    }

    @SuppressWarnings("unchecked")
    public <T> T attr(String key) {
        return (T) req.getAttribute(key);
    }

    public void attr(String key, Object value) {
        req.setAttribute(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T sessionAttr(String key) {
        if (session == null) {
            return null;
        }
        return (T) session.getAttribute(key);
    }

    public void sessionAttr(String key, Object value) {
        if (session == null) {
            session = req.getSession();
        }
        session.setAttribute(key, value);
    }

    public void removeSessionAttr(String key) {
        sessionAttr(key, null);
        session.removeAttribute(key);
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public String[] params(String key) {
        return parameterMap.get(key);
    }

    public List<Integer> paramsInt(String key) {
        String[] strs = params(key);
        if (strs == null || strs.length == 0) {
            return null;
        }
        List<Integer> list = new ArrayList<Integer>();
        Integer i = null;
        for (String str : strs) {
            try {
                i = Integer.parseInt(str);
                list.add(i);
            } catch (Exception e) {
            }
        }
        return list;
    }

    public String param(String key) {
        String result = null;
        if (parameterMap.containsKey(key)) {
            String[] pArray = parameterMap.get(key);
            if (pArray != null && pArray.length > 0) {
                result = pArray[0];
            }
        }
        if (LogKit.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append(key).append(":").append(result);
            LogKit.debug(sb.toString());
        }
        return result;
    }

    public String param(String key, boolean decode) {
        String val = param(key);
        if (decode) {
            if (val != null && !val.trim().isEmpty()) {
                try {
                    val = URLDecoder.decode(val, "UTF8");
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
        return val;
    }

    public int param(String key, int def) {
        try {
            String val = param(key);
            return (val == null ? def : Integer.valueOf(val));
        } catch (Exception e) {
            return def;
        }
    }

    public long param(String key, long def) {
        try {
            String val = param(key);
            return (val == null ? def : Long.valueOf(val));
        } catch (Exception e) {
            return def;
        }
    }

    public float param(String key, float def) {
        try {
            String val = param(key);
            return (val == null ? def : Float.valueOf(val));
        } catch (Exception e) {
            return def;
        }
    }

    public double param(String key, double def) {
        try {
            String val = param(key);
            return (val == null ? def : Double.valueOf(val));
        } catch (Exception e) {
            return def;
        }
    }

    public Date param(String key, String pattern) {
        try {
            return DateTimeUtil.parse(param(key), pattern);
        } catch (Exception e) {
            return null;
        }
    }

    public void addOptStatus(boolean success) {
        addCallbackParam(KEY_OPT, success);
    }

    public void addCallbackParam(String name, Object obj) {
        org.primefaces.context.RequestContext ctx = org.primefaces.context.RequestContext.getCurrentInstance();
        ctx.addCallbackParam(name, obj);
    }

    public void info(String msg) {
        info(null, msg);
    }

    public void info(String clientId, String msg) {
        if (msg != null && !msg.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(clientId,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
        }
    }

    public void error(String msg) {
        error(null, msg);
    }

    public void error(String clientId, String msg) {
        if (msg != null && !msg.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(clientId,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        }
    }

    public void redirect(String uri) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath() + uri);
        } catch (Exception e) {
            String errorMsg = FacesTool.class + ".redirect():方法异常: " + e.getMessage();
            LogKit.error(errorMsg, e);
        }
    }

    public String contextPath() {
        return req.getContextPath();
    }

    public void gotoHome() {
        try {
            String path = contextPath();
            if (path.length() == 0) {
                path = "/";
            }
            FacesContext.getCurrentInstance().getExternalContext().redirect(path);
        } catch (Exception e) {
            String errorMsg = FacesTool.class + ".gotoHome():方法异常: " + e.getMessage();
            LogKit.error(errorMsg, e);
        }
    }

}
