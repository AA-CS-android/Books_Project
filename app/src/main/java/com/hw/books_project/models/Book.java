package com.hw.books_project.models;

import java.util.List;

public class Book
{
    private String name;
    private List<String> ganers;
    private String auother;
    private String publisher;
    private String realese_date;
    private String language;

    /*
        If not borrowed then empty
        Else will hold the phone number/mail of current holder
    */
    private String status;

//    private String googleBooksID;
//    private String selfLink;
//    private String ISBN;
//    private String danacode;


    public Book() {
    }

    public Book(String name, List<String> ganers, String auother, String publisher, String realese_date, String language, String status) {
        this.name = name;
        this.ganers = ganers;
        this.auother = auother;
        this.publisher = publisher;
        this.realese_date = realese_date;
        this.language = language;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGaners() {
        return ganers;
    }

    public void setGaners(List<String> ganers) {
        this.ganers = ganers;
    }

    public String getAuother() {
        return auother;
    }

    public void setAuother(String auother) {
        this.auother = auother;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRealese_date() {
        return realese_date;
    }

    public void setRealese_date(String realese_date) {
        this.realese_date = realese_date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
