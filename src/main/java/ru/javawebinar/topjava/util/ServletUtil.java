package ru.javawebinar.topjava.util;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

public class ServletUtil {
    public static <T> T getParam(HttpServletRequest request, String param, Function<String, T> f) {
        String value = request.getParameter(param);
        if (value == null|| value.isEmpty())
            return null;

        return f.apply(value);
    }
}
