package com.hw.books_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.hw.books_project.R;
import com.hw.books_project.objects.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookAdapter extends ArrayAdapter<Book> {

    private Map<String, Integer> bookCounts;

    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    public void setBookCounts(Map<String, Integer> bookCounts) {
        this.bookCounts = bookCounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        ImageView ivBookCover = listItemView.findViewById(R.id.ivBookCover);
        TextView tvBookName = listItemView.findViewById(R.id.tvBookName);
        TextView tvBookAuthor = listItemView.findViewById(R.id.tvBookAuthor);
        TextView tvBookGenres = listItemView.findViewById(R.id.tvGenres);
        TextView tvAvailableAmount = listItemView.findViewById(R.id.tvAvailableAmount);

        if (currentBook != null) {
            tvBookName.setText(currentBook.getName());
            tvBookAuthor.setText(currentBook.getAuthor());
            
            List<String> genres = currentBook.getGenres();
            if (genres != null && !genres.isEmpty()) {
                tvBookGenres.setText(String.join(", ", genres));
            } else {
                tvBookGenres.setText("");
            }

            if (bookCounts != null && bookCounts.containsKey(currentBook.getBookId())) {
                tvAvailableAmount.setText("Available: " + bookCounts.get(currentBook.getBookId()));
                tvAvailableAmount.setVisibility(View.VISIBLE);
            } else {
                tvAvailableAmount.setVisibility(View.GONE);
            }

            Glide.with(getContext())
                    .load(currentBook.getCoverImageUrl())
                    .placeholder(R.drawable.library_book)
                    .error(R.drawable.library_book)
                    .into(ivBookCover);
        }

        return listItemView;
    }
}
