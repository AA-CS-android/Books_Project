package com.hw.books_project.models;

import java.util.Date;
import java.util.List;

public class Library {
    private String name;
    private int maxLoanDuration;
    private int maxLoanCount;
    private int reloanCooldown;
    //weekly opening hours
    private Date openingHours;
    private Date closingHours;
    private List<String> openingDays;

    private List<Book> books;
    private List<User> users;
    private List<User> admins;
    private List<Loan> loans;

}
