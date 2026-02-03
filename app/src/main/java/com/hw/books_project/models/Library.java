package com.hw.books_project.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

public class Library implements Serializable {
    @NonNull
    private String libraryId = "Null";
    private String name;
    private int maxLoanDuration;
    private int maxLoanCount;
    private int reloanCooldown;
    /// mapping physical books IDs to book data in books object
    /// each value holds the individual book id, and a boolean indicating if its available or not
    private Map<String, Map<String, Boolean>> books;
    /// mapping user ID to a boolean (True = is in library)
    /// value is boolean as firebase does not allow null object
    private Map<String, Boolean> users;
    /// mapping user ID to a boolean (True = is in library)
    /// value is boolean as firebase does not allow null object
    private Map<String, Boolean> admins;
    /// mapping loan ID to a boolean (True = is in library)
    /// value will be changed later on, once loan algorithm is implemented
    private Map<String, Boolean> loans;

    public Library() {
        // Default constructor required for calls to DataSnapshot.getValue(Library.class)
    }

    public Library(String libraryId, String name, int maxLoanDuration, int maxLoanCount, int reloanCooldown, Map<String, Map<String, Boolean>> books, Map<String, Boolean> users, Map<String, Boolean> admins, Map<String, Boolean> loans) {
        this.libraryId = libraryId;
        this.name = name;
        this.maxLoanDuration = maxLoanDuration;
        this.maxLoanCount = maxLoanCount;
        this.reloanCooldown = reloanCooldown;
        this.books = books;
        this.users = users;
        this.admins = admins;
        this.loans = loans;
    }

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

    public int getReloanCooldown() {
        return reloanCooldown;
    }

    public void setReloanCooldown(int reloanCooldown) {
        this.reloanCooldown = reloanCooldown;
    }

    public Map<String, Map<String, Boolean>> getBooks() {
        return books;
    }

    public void setBooks(Map<String, Map<String, Boolean>> books) {
        this.books = books;
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Boolean> users) {
        this.users = users;
    }

    public Map<String, Boolean> getAdmins() {
        return admins;
    }

    public void setAdmins(Map<String, Boolean> admins) {
        this.admins = admins;
    }

    public Map<String, Boolean> getLoans() {
        return loans;
    }

    public void setLoans(Map<String, Boolean> loans) {
        this.loans = loans;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name != null ? this.name : "Unnamed Library";
    }
}
