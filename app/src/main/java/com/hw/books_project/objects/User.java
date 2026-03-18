package com.hw.books_project.objects;

import androidx.annotation.Nullable;

import java.util.Map;

public class User {
    @Nullable
    private String uid = "Null";
    // name of user
    private String name;
    // email of user
    private String email;
    // map {libraryid}_{bookId}_{userId} to return date (loan date is saved in loans)
    private Map<String, Long> loans;
    // map libraryID to true (index for joined libraries)
    private Map<String, Boolean> libraries;

    @Nullable
    public String getUid() {
        return uid;
    }

    public void setUid(@Nullable String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Long> getLoans() {
        return loans;
    }

    public void setLoans(Map<String, Long> loans) {
        this.loans = loans;
    }

    public Map<String, Boolean> getLibraries() {
        return libraries;
    }

    public void setLibraries(Map<String, Boolean> libraries) {
        this.libraries = libraries;
    }
    public void addLoan(Loan loan){
        loans.put(loan.getLoanId(), loan.getLoanDate());
    }
    public void addLoanWithReturnDate(Loan loan, Long returnDate){
        loans.put(loan.getLoanId(),returnDate);
    }
}
