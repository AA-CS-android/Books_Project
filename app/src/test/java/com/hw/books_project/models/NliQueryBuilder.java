package com.hw.books_project.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class NliQueryBuilder {
    private final Map<String, String> queryParams = new HashMap<>();

    public NliQueryBuilder outputFormat(String format)
    {
        if (!(format.equals("json") || format.equals("xml")))
            throw new IllegalArgumentException("Invalid output format: " + format);
        queryParams.put("outputFormat", format);
        return this;
    }
    public NliQueryBuilder withScope(String scope) {
        queryParams.put("scope", scope);
        return this;
    }

    public NliQueryBuilder withQuery(String field, String operator, String value) {


        String query = String.format("%s,%s,%s", field, operator, value);
        queryParams.put("q", query);
        return this;
    }

    public NliQueryBuilder withFreeText(String text) {
        queryParams.put("q", "any,contains," + text);
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            try {
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                  .append("=")
                  .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // This should not happen with UTF-8
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
}
