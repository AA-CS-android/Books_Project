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
        String apiKey = "8G1Zdce9ir7FxTm6OJ7VIBuzakEniAsm1xQjj6R1";

        String searchString = "title,contains,חיות הפלא";
        String urlString = "https://api.nli.org.il/openlibrary/search?api_key=" + apiKey + "&query=" + searchString;
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
}