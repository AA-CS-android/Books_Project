package com.hw.books_project.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.hw.books_project.objects.Book;
import com.hw.books_project.objects.Library;
import com.hw.books_project.objects.Loan;
import com.hw.books_project.objects.User;

import java.security.cert.TrustAnchor;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoanUtils {

    public interface LoanCallback {
        void onSuccess();
        void onFailure(String message);
    }

    /**
     * Attempts to loan a book from a library.
     * Uses a transaction to handle concurrent loan attempts safely.
     */
    public static void loanBook(Library library, Book book, User user, LoanCallback callback) {
        if (library == null || book == null || user == null) {
            callback.onFailure("Invalid loan parameters");
            return;
        }
        String loanId = library.getLibraryId() + "_" + book.getBookId() + "_" + user.getUid();
        Map<String, Object> updates = new HashMap<>();

        updates.put("Libraries/" + library.getLibraryId() + "/books/" + book.getBookId(), ServerValue.increment(-1));
        updates.put("Loans/" + loanId, new Loan(loanId, library.getLibraryId(), book.getBookId(), user.getUid(), getLoanDateUNIX()));
        updates.put("Users/" + user.getUid() + "/loans/" + loanId, new Loan(loanId, library.getLibraryId(), book.getBookId(), user.getUid(), getReturnDateUNIX(library.getMaxLoanDuration())));

        FBRef.refDB.getReference().updateChildren(updates, (error, ref) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
            } else {
                callback.onSuccess();
            }
        });

//
//        DatabaseReference libBooksRef = FBRef.refLibraries.child(library.getLibraryId()).child("books").child(book.getBookId());
//        libBooksRef.runTransaction(new Transaction.Handler() {
//            @NonNull
//            @Override
//            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//                Integer count = currentData.getValue(Integer.class);
//
//                if (count == null || count <= 0) {
//                    // No copies available or book not found in this library's record
//                    return Transaction.abort();
//                }
//
//                // Decrease the number of available copies by 1
//                currentData.setValue(count - 1);
//
//                return Transaction.success(currentData);
//            }
//
//            @Override
//            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//                /// TODO: revert changes (add the book back) if the loan creation did not work
//                if (committed) {
//                    // loan id contains: library id, book id, user id
//                    String loanId = library.getLibraryId() + "_" + book.getBookId() + "_" + user.getUid();
//                    Loan loan = new Loan(loanId, library.getLibraryId(), book.getBookId(), user.getUid(), getLoanDateUNIX());
//                    FBRef.currentUser.addLoanWithReturnDate(loan, getReturnDateUNIX(library.getMaxLoanDuration()));
//                    // new global loan record
//                    FBRef.refLoans.child(loanId).setValue(loan);
//                    // user loan record
//                    FBRef.refUsers.child(FBRef.currentUser.getUid()).child("loans").child(loanId).setValue(getReturnDateUNIX(library.getMaxLoanDuration()));
//                    callback.onSuccess();
//                } else {
//                    String message = (error != null) ? error.getMessage() : "No copies available.";
//                    callback.onFailure(message);
//                }
//            }
//        });
    }

    private static void createLoanRecord(Library library, Book book, User user, LoanCallback callback) {
        // TODO: Implement the logic to create a new Loan object, 
        // save it to the "Loans" branch, and update the User's "loans" branch.
        // For now, we simulate success
        callback.onSuccess();
    }

    public static String getReturnDate(Integer loanDuration) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, loanDuration);
        Date returnDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedReturnDate = sdf.format(returnDate);
        return formattedReturnDate;
    }


    public static String formatDate(Long date) {
        Date dateObj = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(dateObj);
    }

    public static Long getLoanDateUNIX() {
        return LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }
    public static Long getReturnDateUNIX(int loanDuration) {
        return LocalDate.now().plusDays(loanDuration).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }
}
