package com.hw.books_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hw.books_project.R;
import com.hw.books_project.objects.Book;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LoanedBookAdapter extends RecyclerView.Adapter<LoanedBookAdapter.ViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(LoanedBookData data);
    }

    public static class LoanedBookData {
        public Book book;
        public String libraryName;
        public Long returnDate;
        public String libraryId;

        public LoanedBookData(Book book, String libraryName, Long returnDate, String libraryId) {
            this.book = book;
            this.libraryName = libraryName;
            this.returnDate = returnDate;
            this.libraryId = libraryId;
        }
    }

    private final Context context;
    private final List<LoanedBookData> loanList;
    private final OnBookClickListener listener;

    public LoanedBookAdapter(Context context, List<LoanedBookData> loanList, OnBookClickListener listener) {
        this.context = context;
        this.loanList = loanList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_loaned_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoanedBookData data = loanList.get(position);
        Book book = data.book;

        holder.tvBookName.setText(book.getName());
        holder.tvLibraryName.setText("Library: " + data.libraryName);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = sdf.format(new Date(data.returnDate * 1000));
        holder.tvReturnDate.setText("Return Date: " + dateStr);

        long diffInMs = (data.returnDate * 1000) - System.currentTimeMillis();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs);
        
        if (diffInDays < 0) {
            holder.tvDaysRemaining.setText("(Overdue!)");
            holder.tvDaysRemaining.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvDaysRemaining.setText("(" + diffInDays + " days remaining)");
            holder.tvDaysRemaining.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        Glide.with(context)
                .load(book.getCoverImageUrl())
                .placeholder(R.drawable.library_book)
                .error(R.drawable.library_book)
                .into(holder.ivBookCover);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvBookName, tvLibraryName, tvReturnDate, tvDaysRemaining;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvBookName = itemView.findViewById(R.id.tvBookName);
            tvLibraryName = itemView.findViewById(R.id.tvLibraryName);
            tvReturnDate = itemView.findViewById(R.id.tvReturnDate);
            tvDaysRemaining = itemView.findViewById(R.id.tvDaysRemaining);
        }
    }
}
