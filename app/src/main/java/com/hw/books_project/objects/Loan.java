package com.hw.books_project.objects;

import androidx.annotation.NonNull;

public class Loan {
    @NonNull
    private String loanId = "Null";
    // library ID
    private String libraryId;
    // book ID
    private String bookId;
    // loaner ID
    private String userId;
    // return date dd/mm/yy
    private Long loanDate;

    public Loan(@NonNull String loanId, String libraryId, String bookId, String userId, Long loanDate) {
        this.loanId = loanId;
        this.libraryId = libraryId;
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
    }

    @NonNull
    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(@NonNull String loanId) {
        this.loanId = loanId;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Long loanDate) {
        this.loanDate = loanDate;
    }
}
