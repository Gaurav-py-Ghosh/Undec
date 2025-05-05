package com.example.undec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private List<Note> noteList;
    private OnNoteClickListener noteClickListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public CarouselAdapter(List<Note> noteList, OnNoteClickListener listener) {
        this.noteList = noteList;
        this.noteClickListener = listener;
    }

    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView noteImage;
        TextView noteTitle;
        TextView noteCategory;
        TextView noteDate;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            noteImage = itemView.findViewById(R.id.noteImage);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteCategory = itemView.findViewById(R.id.noteCategory);
            noteDate = itemView.findViewById(R.id.noteDate);
        }
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_card, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        Note note = noteList.get(position);

        // Set all note information
        holder.noteTitle.setText(note.getTitle());
        holder.noteCategory.setText(note.getCategory());
        holder.noteDate.setText(note.getDate());
        holder.noteImage.setImageResource(note.getImageResId());

        // Add click listener
        holder.itemView.setOnClickListener(v -> {
            if (noteClickListener != null) {
                noteClickListener.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void updateNotes(List<Note> newNotes) {
        noteList.clear();
        noteList.addAll(newNotes);
        notifyDataSetChanged();
    }
}