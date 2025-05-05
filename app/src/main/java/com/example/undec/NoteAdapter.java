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

    private List<Note> notes;
    private OnNoteClickListener listener;

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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView categoryTextView;
        private TextView dateTextView;
        private ImageView noteImageView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.noteTitle);
            categoryTextView = itemView.findViewById(R.id.noteCategory);
            dateTextView = itemView.findViewById(R.id.noteDate);
            noteImageView = itemView.findViewById(R.id.noteImage);
        }

        public void bind(Note note) {
            titleTextView.setText(note.getTitle());
            categoryTextView.setText(note.getCategory());
            dateTextView.setText(note.getDate());
            noteImageView.setImageResource(note.getImageResId());
        }
    }
}