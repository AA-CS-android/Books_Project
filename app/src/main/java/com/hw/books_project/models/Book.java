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
    private String barcode;

    public Book(@NonNull String bookId, String name, String cover_image_url, List<String> genres, String author, String publisher, String barcode) {
        this.bookId = bookId;
        this.name = name;
        this.cover_image_url = cover_image_url;
        this.genres = genres;
        this.author = author;
        this.publisher = publisher;
        this.barcode = barcode;
    }
    public Book() {
    }

    @NonNull
    public String getBookId() {
        return bookId;
    }

    public void setBookId(@NonNull String bookId) {
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}