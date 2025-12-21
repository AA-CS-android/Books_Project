package com.hw.books_project.models;

import java.util.List;

public class Library {
    private String uid;
    private String name;
    private int maxLoanDuration;
    private int maxLoanCount;
    private int reloanCooldown;
    private List<String> openingDaysTimes;

    // These lists can be populated later
    private List<String> books; // Storing book UIDs
    private List<String> users; // Storing user UIDs
    private List<String> admins; // Storing user UIDs
    private List<String> loans; // Storing loan UIDs

    public Library() {
        // Default constructor required for calls to DataSnapshot.getValue(Library.class)
    }

    // --- Getters ---
    public String getUid() { return uid; }
    public String getName() { return name; }
    public int getMaxLoanDuration() { return maxLoanDuration; }
    public int getMaxLoanCount() { return maxLoanCount; }
    public int getReloanCooldown() { return reloanCooldown; }
    public List<String> getOpeningDaysTimes() { return openingDaysTimes; }
    public List<String> getBooks() { return books; }
    public List<String> getUsers() { return users; }
    public List<String> getAdmins() { return admins; }
    public List<String> getLoans() { return loans; }

    // --- Setters ---
    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setMaxLoanDuration(int maxLoanDuration) { this.maxLoanDuration = maxLoanDuration; }
    public void setMaxLoanCount(int maxLoanCount) { this.maxLoanCount = maxLoanCount; }
    public void setReloanCooldown(int reloanCooldown) { this.reloanCooldown = reloanCooldown; }
    public void setOpeningDaysTimes(List<String> openingDaysTimes) { this.openingDaysTimes = openingDaysTimes; }
    public void setBooks(List<String> books) { this.books = books; }
    public void setUsers(List<String> users) { this.users = users; }
    public void setAdmins(List<String> admins) { this.admins = admins; }
    public void setLoans(List<String> loans) { this.loans = loans; }
}
