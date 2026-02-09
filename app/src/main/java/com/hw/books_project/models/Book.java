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


}