package com.hw.books_project.utils;

import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NLIOpenLibraryClient {

    private static final String BASE_URL = "https://api.nli.org.il/openlibrary/search";
    private final String apiKey;

    // Threading tools
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Executor callbackExecutor;

    // Callback Interface
    public interface SearchCallback {
        void onResult(String response);
        void onError(Exception e);
    }

    public NLIOpenLibraryClient(String apiKey) {
        this(apiKey, command -> new Handler(Looper.getMainLooper()).post(command));
    }

    public NLIOpenLibraryClient(String apiKey, Executor callbackExecutor) {
        this.apiKey = apiKey;
        this.callbackExecutor = callbackExecutor;
    }

    public void executeSearch(QueryBuilder qb, SearchCallback callback) {
        executor.execute(() -> {
            try {
                String fullUrl = buildFullUrl(qb);
                String finalResult = doRequest(fullUrl);
                callbackExecutor.execute(() -> callback.onResult(finalResult));
            } catch (Exception e) {
                callbackExecutor.execute(() -> callback.onError(e));
            }
        });
    }

    String doRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();
            return response.toString();
        } else {
            throw new Exception("Error: " + conn.getResponseCode());
        }
    }

    private String buildFullUrl(QueryBuilder qb) throws Exception {
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append("?api_key=").append(apiKey);

        // Encode the structured query (Hebrew handled here)
        String rawQuery = qb.buildQueryString();
        sb.append("&query=").append(URLEncoder.encode(rawQuery, StandardCharsets.UTF_8.name()));

        if (qb.materialType != null) sb.append("&material_type=").append(qb.materialType);
        if (qb.sortField != null) sb.append("&sort_field=").append(qb.sortField);
        sb.append("&items_per_page=").append(qb.itemsPerPage);
        sb.append("&output_format=").append(qb.outputFormat);

        return sb.toString();
    }

    public static class QueryBuilder {
        public final List<String> languages = Arrays.asList("eng", "heb");

        private final List<String> conditions = new ArrayList<>();
        String materialType = "books", sortField, outputFormat = "json";
        int itemsPerPage = 10;

        public QueryBuilder addCondition(String attr, String op, String val) {
            conditions.add(attr + "," + op + "," + val);
            return this;
        }

        public QueryBuilder addCondition(String val) {
            conditions.add("any" + "," + "contains" + "," + val);
            return this;
        }

        public QueryBuilder searchTitle(String val) {
            conditions.add("title" + "," + "contains" + "," + val);
            return this;
        }

        public QueryBuilder searchLanguage(String val) {
            if (!languages.contains(val)) return this;
            conditions.add("language" + "," + "exact" + "," + val);
            return this;
        }
        public QueryBuilder searchISNB(String val) {
            conditions.add("isbn" + "," + "exact" + "," + val);
            return this;
        }

        //(no need for anything but books)        public QueryBuilder setMaterialType(String t) { this.materialType = t; return this; }
        public QueryBuilder setOutputFormat(String f) { this.outputFormat = f; return this; }
        public QueryBuilder setSortField(String s) { this.outputFormat = s; return this; }

        public String buildQueryString() {
            // Joins conditions with NLI's specific delimiter
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                return String.join(",AND;", conditions);
            } else {
                // Fallback for older Android versions
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < conditions.size(); i++) {
                    sb.append(conditions.get(i));
                    if (i < conditions.size() - 1) sb.append(",AND;");
                }
                return sb.toString();
            }
        }
    }
}
