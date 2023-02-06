package ru.javawebinar.topjava.util;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

public class WebUtil {
    public static <T> T getParam(HttpServletRequest req, String paramName, Function<String, T> f, T defaultValue) {
        String param = req.getParameter(paramName);
        if (param != null && !param.isEmpty()) { return f.apply(param); }

        return defaultValue;
    }

    public static <T> T getParam(HttpServletRequest req, String paramName, Function<String, T> f) {
        return getParam(req, paramName, f, null);
    }
}
