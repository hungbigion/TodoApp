package com.example.todoapp.interfaces;

public interface RecycleViewClickListener {
    void onItemClick(int position);
    void onLongItemClick(int position);
    void onEditButtonClick(int position);
    void onDeleteButtonClick(int position);
    void onDoneButtonClick(int position);
}
