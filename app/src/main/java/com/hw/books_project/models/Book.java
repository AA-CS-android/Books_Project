package com.hw.books_project.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Book implements Serializable {
    @NonNull
    private String bookId = "Null";
    private String name;
    private String coverImageUrl;
    private List<String> genres;
    private String author;
    private String isnb;
    private String barcode;

    public Book(@NonNull String bookId, String name, String coverImageUrl, List<String> genres, String author, String publisher, String barcode, String isbn) {
        this.bookId = bookId;
        this.name = name;
        this.coverImageUrl = coverImageUrl;
        this.genres = genres;
        this.author = author;
        this.barcode = barcode;
        this.isnb = isbn;
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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
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

    public String getIsnb() {return isnb;}

    public void setIsnb(String isnb) {this.isnb = isnb;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) &&
                Objects.equals(author, book.author) &&
                Objects.equals(isnb, book.isnb) &&
                Objects.equals(coverImageUrl, book.coverImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author, isnb, coverImageUrl);
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", name='" + name + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", genres=" + genres +
                ", author='" + author + '\'' +
                ", isnb='" + isnb + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}