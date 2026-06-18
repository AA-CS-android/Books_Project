package com.hw.books_project.screens.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hw.books_project.R;
import com.hw.books_project.adapters.LoanedBookAdapter;
import com.hw.books_project.objects.Book;
import com.hw.books_project.utils.FBRef;
import com.hw.books_project.utils.LoanUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// REQUIREMENT: 9.5 Fragment
public class BooksFragment extends Fragment implements LoanedBookAdapter.OnBookClickListener {

    private RecyclerView rvLoanedBooks;
    private TextView tvEmptyMessage;
    private LoanedBookAdapter adapter;
    private final List<LoanedBookAdapter.LoanedBookData> loanDataList = new ArrayList<>();

    public BooksFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        rvLoanedBooks = view.findViewById(R.id.rvLoanedBooks);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        
        rvLoanedBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LoanedBookAdapter(getContext(), loanDataList, this);
        rvLoanedBooks.setAdapter(adapter);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserLoans();
    }

    private void loadUserLoans() {
        if (FBRef.currentUser == null || FBRef.currentUser.getLoans() == null || FBRef.currentUser.getLoans().isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            rvLoanedBooks.setVisibility(View.GONE);
            loanDataList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        tvEmptyMessage.setVisibility(View.GONE);
        rvLoanedBooks.setVisibility(View.VISIBLE);
        loanDataList.clear();

        Map<String, Long> userLoans = FBRef.currentUser.getLoans();
        for (Map.Entry<String, Long> entry : userLoans.entrySet()) {
            String loanId = entry.getKey();
            Long returnDate = entry.getValue();

            // loanId format: libraryId_bookId_userId
            String[] parts = loanId.split("_");
            if (parts.length >= 2) {
                String libraryId = parts[0];
                String bookId = parts[1];
                fetchLoanDetails(libraryId, bookId, returnDate);
            }
        }
    }

    private void fetchLoanDetails(String libraryId, String bookId, Long returnDate) {
        FBRef.refLibraries.child(libraryId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot libSnapshot) {
                String libraryName = libSnapshot.getValue(String.class);
                if (libraryName == null) libraryName = "Unknown Library";

                final String finalLibraryName = libraryName;

                FBRef.refBooks.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot bookSnapshot) {
                        Book book = bookSnapshot.getValue(Book.class);
                        if (book != null) {
                            loanDataList.add(new LoanedBookAdapter.LoanedBookData(book, finalLibraryName, returnDate, libraryId));
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onBookClick(LoanedBookAdapter.LoanedBookData data) {
        showReturnDialog(data);
    }

    private void showReturnDialog(LoanedBookAdapter.LoanedBookData data) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_return_book, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        ImageView ivCover = dialogView.findViewById(R.id.ivBookCoverLarge);
        TextView tvName = dialogView.findViewById(R.id.tvBookNameLarge);
        TextView tvLibrary = dialogView.findViewById(R.id.tvLibraryNameLarge);
        TextView tvReturnDate = dialogView.findViewById(R.id.tvReturnDateLarge);
        TextView tvDaysRemaining = dialogView.findViewById(R.id.tvDaysRemainingLarge);
        Button btnReturn = dialogView.findViewById(R.id.btnReturn);
        Button btnBack = dialogView.findViewById(R.id.btnBack);

        tvName.setText(data.book.getName());
        tvLibrary.setText("Library: " + data.libraryName);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvReturnDate.setText("Return Date: " + sdf.format(new Date(data.returnDate * 1000)));

        long diffInMs = (data.returnDate * 1000) - System.currentTimeMillis();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs);
        
        if (diffInDays < 0) {
            tvDaysRemaining.setText("(Overdue!)");
            tvDaysRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvDaysRemaining.setText("(" + diffInDays + " days remaining)");
            tvDaysRemaining.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        Glide.with(this)
                .load(data.book.getCoverImageUrl())
                .placeholder(R.drawable.library_book)
                .into(ivCover);

        btnReturn.setOnClickListener(v -> {
            if (FBRef.currentUser == null) return;
            
            LoanUtils.returnBook(getContext(), data.libraryId, data.book.getBookId(), FBRef.currentUser.getUid(), new LoanUtils.LoanCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Book returned successfully", Toast.LENGTH_SHORT).show();
                    // Remove from local user object map to keep sync before next fetch
                    FBRef.currentUser.getLoans().remove(data.libraryId + "_" + data.book.getBookId() + "_" + FBRef.currentUser.getUid());
                    loadUserLoans();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getContext(), "Failed to return book: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
