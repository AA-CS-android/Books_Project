package com.hw.books_project.models;

import android.view.inputmethod.SelectGesture;

import androidx.annotation.Nullable;

import java.util.List;

public class User {
    private String uid;
    private String name;
    private String email;
    private String phone;
    private List<Book> books_read;
    private List<Book> books_holding;
    private List<Book> books_want_to_read;

    public User() {
    }
    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.phone = null;
        this.books_read = null;
        this.books_holding = null;
        this.books_want_to_read = null;
        this.uid = null;
    }

    public User(String name, String email, String uid) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = null;
        this.books_read = null;
        this.books_holding = null;
        this.books_want_to_read = null;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Book> getBooks_read() {
        return books_read;
    }

    public void setBooks_read(List<Book> books_read) {
        this.books_read = books_read;
    }

    public List<Book> getBooks_holding() {
        return books_holding;
    }

    public void setBooks_holding(List<Book> books_holding) {
        this.books_holding = books_holding;
    }

    public List<Book> getBooks_want_to_read() {
        return books_want_to_read;
    }

    public void setBooks_want_to_read(List<Book> books_want_to_read) {
        this.books_want_to_read = books_want_to_read;
    }
}
