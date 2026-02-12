package com.hw.books_project.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @NonNull
    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(@NonNull String libraryId) {
        this.libraryId = libraryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public int getMaxLoanDuration() {
        return maxLoanDuration;
    }

    public void setMaxLoanDuration(int maxLoanDuration) {
        this.maxLoanDuration = maxLoanDuration;
    }

    public int getMaxLoanCount() {
        return maxLoanCount;
    }

    public void setMaxLoanCount(int maxLoanCount) {
        this.maxLoanCount = maxLoanCount;
    }

    public Map<String, Integer> getBooks() {
        return books;
    }

    public void setBooks(Map<String, Integer> books) {
        this.books = books;
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Boolean> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public String toString() {
        return name != null ? name : "Unnamed Library";
    }
}
