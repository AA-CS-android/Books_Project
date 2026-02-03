package com.hw.books_project.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {
    @NonNull
    private String bookId = "Null";
    private String name;
    private String cover_image_url;
    private List<String> genres;
    private String author;
    private String publisher;
    private String release_date;
    private String language;
    private String status;
    private String ISBN;
    private String danacode;

    public Book() {
    }

    public Book(String bookId, String ISBN, String name, String cover_image_url, List<String> genres, String author, String publisher, String release_date, String language, String status, String danacode) {
        this.bookId = bookId;
        this.ISBN = ISBN;
        this.name = name;
        this.cover_image_url = cover_image_url;
        this.genres = genres;
        this.author = author;
        this.publisher = publisher;
        this.release_date = release_date;
        this.language = language;
        this.status = status;
        this.danacode = danacode;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
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

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getDanacode() {
        return danacode;
    }

    public void setDanacode(String danacode) {
        this.danacode = danacode;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name != null ? this.name : "Unnamed Book";
    }
}
