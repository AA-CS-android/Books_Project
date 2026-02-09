package com.hw.books_project.models;

import android.view.inputmethod.SelectGesture;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class User {
    @Nullable
    private String uid = "Null";
    // name of user
    private String name;
    // email of user
    private String email;
    // map loanID to return date
    private Map<String, Long> loans;
}
