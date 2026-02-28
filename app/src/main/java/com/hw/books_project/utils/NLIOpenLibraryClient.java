//package com.hw.books_project.utils;
//
//import android.os.Handler;
//import android.os.Looper;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Client for making calls to the National Library of Israel (NLI) Search API.
// */
//public class NLIOpenLibraryClient {
//
//    private static final String BASE_URL = "https://api.nli.org.il/openlibrary/search";
//    private final String apiKey;
//    private final ExecutorService executor = Executors.newSingleThreadExecutor();
//    private final Handler mainHandler = new Handler(Looper.getMainLooper());
//
//    public interface SearchCallback {
//        void onSuccess(String response);
//        void onError(Exception e);
//    }
//
//    public NLIOpenLibraryClient(String apiKey) {
//        this.apiKey = apiKey;
//    }
//
//    /**
//     * Builds a single query condition.
//     * Example: buildCondition("title", "exact", "ירושלים") -> "title,exact,%D7%99%D7%A8..."
//     */
//    public String buildCondition(String attribute, String operator, String value) {
//        try {
//            // Encode the Hebrew value correctly for URLs
//            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
//            return attribute + "," + operator + "," + encodedValue;
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    /**
//     * Executes a search request.
//     * @param query The formatted query string (e.g. "title,exact,jerusalem")
//     * @param outputFormat "xml" or "json"
//     * @param callback Callback for handling results on the UI thread
//     */
//    public void search(final String query, final String outputFormat, final SearchCallback callback) {
//        executor.execute(() -> {
//            try {
//                // Construct the full URL
//                StringBuilder urlBuilder = new StringBuilder(BASE_URL);
//                urlBuilder.append("?api_key=").append(apiKey);
//                urlBuilder.append("&query=").append(query);
//                urlBuilder.append("&output_format=").append(outputFormat);
//
//                // Add optional filters if needed (e.g. material_type, availability_type)
//                // urlBuilder.append("&availability_type=online_and_api_access");
//
//                URL url = new URL(urlBuilder.toString());
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//                conn.setRequestProperty("Accept", "application/" + outputFormat);
//
//                int responseCode = conn.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = in.readLine()) != null) {
//                        response.append(line);
//                    }
//                    in.close();
//
//                    final String result = response.toString();
//                    mainHandler.post(() -> callback.onSuccess(result));
//                } else {
//                    throw new Exception("HTTP Error: " + responseCode);
//                }
//            } catch (final Exception e) {
//                mainHandler.post(() -> callback.onError(e));
//            }
//        });
//    }
//
//    public static class QueryBuilder {
//        private final List<String> conditions = new ArrayList<>();
//        private String materialType = "books"; // Default to books
//        private String sortField;
//        private int itemsPerPage = 10;
//        private String outputFormat = "json"; // Default to json
//
//        public QueryBuilder addCondition(String attribute, String operator, String value) {
//            // Format: attribute,operator,value
//            conditions.add(String.format("%s,%s,%s", attribute, operator, value));
//            return this;
//        }
//
//        public QueryBuilder setMaterialType(String type) {
//            this.materialType = type;
//            return this;
//        }
//
//        public QueryBuilder setSortField(String field) {
//            this.sortField = field;
//            return this;
//        }
//
//        public QueryBuilder setItemsPerPage(int count) {
//            this.itemsPerPage = count;
//            return this;
//        }
//
//        public QueryBuilder setOutputFormat(String format) {
//            this.outputFormat = format;
//            return this;
//        }
//
//        public String buildQueryString() {
//            // NLI uses ",AND;" or ",OR;" as delimiters between conditions
//            // using "AND" we narrow the results
//            return String.join(",AND;", conditions);
//        }
//    }
//}

/* --------------- */
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NLIOpenLibraryClient {

    private static final String BASE_URL = "https://api.nli.org.il/openlibrary/search";
    private final String apiKey;

    // Threading tools
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Callback Interface
    public interface SearchCallback {
        void onResult(String response);
        void onError(Exception e);
    }

    public NLIOpenLibraryClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public void executeSearch(QueryBuilder qb, SearchCallback callback) {
        executor.execute(() -> {
            try {
                String fullUrl = buildFullUrl(qb);
                URL url = new URL(fullUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                    in.close();

                    String finalResult = response.toString();
                    // Return result to Main Thread
                    mainHandler.post(() -> callback.onResult(finalResult));
                } else {
                    throw new Exception("Error: " + conn.getResponseCode());
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
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
        private final List<String> conditions = new ArrayList<>();
        String materialType, sortField, outputFormat = "json";
        int itemsPerPage = 10;

        public QueryBuilder addCondition(String attr, String op, String val) {
            conditions.add(attr + "," + op + "," + val);
            return this;
        }

        public QueryBuilder setMaterialType(String t) { this.materialType = t; return this; }
        public QueryBuilder setOutputFormat(String f) { this.outputFormat = f; return this; }

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