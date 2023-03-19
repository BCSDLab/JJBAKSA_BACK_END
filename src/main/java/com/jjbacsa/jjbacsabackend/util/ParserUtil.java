package com.jjbacsa.jjbacsabackend.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParserUtil {
    public static Map<String, String> splitQueryString(String queryString) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new LinkedHashMap<String, String>();
        if (queryString == null) {
            return queryPairs;
        }

        String query = queryString;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return queryPairs;
    }
}
