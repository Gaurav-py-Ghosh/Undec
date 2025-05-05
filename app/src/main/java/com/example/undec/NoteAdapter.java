package com.example.undec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<Note> notes;
    private final OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public NoteAdapter(List<Note> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_card, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
        holder.itemView.setOnClickListener(v -> listener.onNoteClick(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView category1TextView;
        private final TextView category2TextView;
        private final TextView dateTextView;
        private final ImageView noteImageView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.noteTitle);
            category1TextView = itemView.findViewById(R.id.noteCategory1);
            category2TextView = itemView.findViewById(R.id.noteCategory2);
            dateTextView = itemView.findViewById(R.id.noteDate);
            noteImageView = itemView.findViewById(R.id.noteImage);
        }

        public void bind(Note note) {
            titleTextView.setText(note.getTitle());
            noteImageView.setImageResource(note.getImageResId());

            String[] categories = note.getCategory().split(" • ");
            category1TextView.setText(categories[0].trim());
            if (categories.length > 1) {
                category2TextView.setText(categories[1].trim());
                category2TextView.setVisibility(View.VISIBLE);
            } else {
                category2TextView.setVisibility(View.GONE);
            }

            dateTextView.setText(note.getDate());
        }
    }
}