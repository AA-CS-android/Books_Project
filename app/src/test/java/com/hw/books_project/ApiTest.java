package com.hw.books_project;

import com.hw.books_project.models.NliQueryBuilder;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ApiTest {

    @Test
    public void testApi() throws Exception {
        String apiKey = "api_key=8G1Zdce9ir7FxTm6OJ7VIBuzakEniAsm1xQjj6R1";
//        String searchString = "title,contains,";
        String urlString = "https://api.nli.org.il/openlibrary/search?",
                query = "query=",
                materialType = "material_type=",
                outputFormat = "output_format=",
                sortFiled = "sort_field=",
//                countMode,
                searchTitle = "title,contains,";
        searchTitle += "הארי פוטר ואבן";
        materialType += "book";
        outputFormat += "json";
        sortFiled += "title";
        query += searchTitle;


        String req = String.join("&", urlString, apiKey, query, materialType, outputFormat, sortFiled);
        System.out.println(urlString);
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        System.out.println(status);
        assertEquals(200, status);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        System.out.println(content.toString());
    }
    private String createQuery(String title) {

        return "";
    }

}