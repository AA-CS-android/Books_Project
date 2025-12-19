package com.hw.books_project.models;

import android.util.TimeUtils;

import java.sql.Time;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Library {
    private String name;
    private int maxLoanDuration;
    private int maxLoanCount;
    private int reloanCooldown;
    //weekly opening hours
    private List<String> openingDaysTimes;

    private List<Book> books;
    private List<User> users;
    private List<User> admins;
    private List<Loan> loans;

    final private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("u:HH:mm", Locale.getDefault());

    public Library() {
        this.name = "NAME_LESS";
        this.maxLoanDuration = -1;
        this.maxLoanCount = -1;
        this.reloanCooldown = -1;
        this.openingDaysTimes = null;
        this.books = null;
        this.users = null;
        this.admins = null;
    }

    public Library(String name, int maxLoanDuration, int maxLoanCount, int reloanCooldown, List<String> openingDaysTimes, List<Book> books, List<User> users, List<User> admins, List<Loan> loans) {
        this.name = name;
        this.maxLoanDuration = maxLoanDuration;
        this.maxLoanCount = maxLoanCount;
        this.reloanCooldown = reloanCooldown;
        this.openingDaysTimes = openingDaysTimes;
        this.books = books;
        this.users = users;
        this.admins = admins;
        this.loans = loans;
    }

    /// @param timeString format: uhhmm where u is day of week
    public LocalDate openingTimeParse(String timeString)
    {
        return LocalDate.parse(timeString, formatter);
    }
    public String openingTimeFormat(int day, int hour, int minute)
    {
        return String.valueOf(day) + ":" + String.valueOf(hour) + ":" + String.valueOf(minute);
    }
    public String openingTimeFormat(LocalDate time)
    {
        return formatter.format(time);
    }
}
