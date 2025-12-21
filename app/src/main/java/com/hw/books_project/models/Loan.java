package com.hw.books_project.models;

import java.util.Date;

public class Loan {
    private Library library;
    private Book book;
    private User loaner;
    private Date loanDate;
    private Date dueDate;
    private boolean returned;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(Loan.class)
     */
    public Loan() {
    }

    public Loan(Library library, Book book, User loaner, Date loanDate, Date dueDate, boolean returned) {
        this.library = library;
        this.book = book;
        this.loaner = loaner;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = returned;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getLoaner() {
        return loaner;
    }

    public void setLoaner(User loaner) {
        this.loaner = loaner;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
