package com.hw.books_project.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

public class Library implements Serializable {
    @NonNull
    private String libraryId = "Null";
    private String name;
    private String admin;
    private int maxLoanDuration;
    private int maxLoanCount;
    private Map<String, Integer> books;
    private Map<String, Boolean> users;

}
