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

    private List<NoteCard> noteList;

    public CarouselAdapter(List<NoteCard> noteList) {
        this.noteList = noteList;
    }

    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView noteImage;
        TextView noteTitle;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            noteImage = itemView.findViewById(R.id.noteImage);
            noteTitle = itemView.findViewById(R.id.noteTitle);
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
        NoteCard note = noteList.get(position);
        holder.noteTitle.setText(note.title);
        holder.noteImage.setImageResource(note.imageResId);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
