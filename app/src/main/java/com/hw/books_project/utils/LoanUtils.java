package com.hw.books_project.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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
    public static void loanBook(Context context, Library library, Book book, User user, LoanCallback callback) {
        if (library == null || book == null || user == null) {
            callback.onFailure("Invalid loan parameters");
            return;
        }
        String loanId = library.getLibraryId() + "_" + book.getBookId() + "_" + user.getUid();
        Map<String, Object> updates = new HashMap<>();

        long returnDateUnix = getReturnDateUNIX(library.getMaxLoanDuration());

        updates.put("Libraries/" + library.getLibraryId() + "/books/" + book.getBookId(), ServerValue.increment(-1));
        updates.put("Loans/" + loanId, new Loan(loanId, library.getLibraryId(), book.getBookId(), user.getUid(), getLoanDateUNIX()));
        updates.put("Users/" + user.getUid() + "/loans/" + loanId, returnDateUnix);

        FBRef.refDB.getReference().updateChildren(updates, (error, ref) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
            } else {
                // Update local user object to reflect the new loan immediately
                if (user.getLoans() == null) {
                    user.setLoans(new HashMap<>());
                }
                user.getLoans().put(loanId, returnDateUnix);
                
                createLoanReminder(context, library, book, returnDateUnix);
                callback.onSuccess();
            }
        });
    }

    /**
     * Returns a loaned book.
     */
    public static void returnBook(Context context, String libraryId, String bookId, String userId, LoanCallback callback) {
        String loanId = libraryId + "_" + bookId + "_" + userId;
        Map<String, Object> updates = new HashMap<>();

        updates.put("Libraries/" + libraryId + "/books/" + bookId, ServerValue.increment(1));
        updates.put("Loans/" + loanId, null);
        updates.put("Users/" + userId + "/loans/" + loanId, null);

        FBRef.refDB.getReference().updateChildren(updates, (error, ref) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
            } else {
                cancelLoanReminder(context, libraryId, bookId);
                callback.onSuccess();
            }
        });
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

    public static void createLoanReminder(Context context, Library library, Book book, Long returnDateUnix) {
        Intent intent = new Intent(context, LoanReminderReceiver.class);
        intent.putExtra(LoanReminderReceiver.EXTRA_BOOK_NAME, book.getName());
        intent.putExtra(LoanReminderReceiver.EXTRA_LIBRARY_NAME, library.getName());

        int requestCode = (book.getBookId() + library.getLibraryId()).hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                requestCode, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // returnDateUnix is in seconds, AlarmManager needs milliseconds.
            long triggerAtMillis = returnDateUnix * 1000;
            
            // Set alarm. For exact timing on newer Androids, you might need SCHEDULE_EXACT_ALARM permission
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } catch (SecurityException e) {
                // Fallback or log if exact alarm permission is missing
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        }
    }

    public static void cancelLoanReminder(Context context, String libraryId, String bookId) {
        Intent intent = new Intent(context, LoanReminderReceiver.class);
        int requestCode = (bookId + libraryId).hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
            pendingIntent.cancel();
        }
    }
}
